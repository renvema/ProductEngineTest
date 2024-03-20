package com.renvema;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.renvema.Message.*;

public class TelnetFileSystemServer {

    private static final Queue<String> resultQueue = new ConcurrentLinkedQueue<>();

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println(USAGE_MESSAGE);
            return;
        }

        int serverPort = Integer.parseInt(args[0]);
        String rootPath = args[1];

        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
            System.out.println(SERVER_STARTED_ON_PORT + serverPort);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println(CLIENT_CONNECTED + clientSocket.getInetAddress());

                Thread clientThread = new Thread(() -> handleClient(clientSocket, rootPath));
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket, String rootPath) {
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            writer.println(WELCOME_MESSAGE);
            writer.println(INPUT_PROMPT);

            String input;
            while ((input = reader.readLine()) != null) {
                String[] tokens = input.split(" ");
                if (tokens.length != 2) {
                    writer.println(INVALID_FORMAT_MESSAGE);
                    continue;
                }

                int depth = Integer.parseInt(tokens[0]);
                String mask = tokens[1];

                File rootDir = new File(rootPath);
                if (!rootDir.exists() || !rootDir.isDirectory()) {
                    writer.println(INVALID_DIRECTORY_MESSAGE);
                    continue;
                }

                traverseFileSystem(rootDir, depth, mask);

                // Send results to the client
                while (!resultQueue.isEmpty()) {
                    writer.println(resultQueue.poll());
                }

                writer.println(SEARCH_COMPLETED_MESSAGE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void traverseFileSystem(File rootDir, int depth, String mask) {
        Deque<File> stack = new ArrayDeque<>();
        stack.push(rootDir);

        Deque<Integer> depthStack = new ArrayDeque<>();
        depthStack.push(0);

        while (!stack.isEmpty()) {
            File currentDir = stack.pop();
            int currentDepth = depthStack.pop();

            if (currentDepth > depth) {
                continue;
            }

            for (File file : currentDir.listFiles()) {
                if (file.isDirectory()) {
                    if (currentDepth + 1 <= depth) {
                        stack.push(file);
                        depthStack.push(currentDepth + 1);
                    }
                } else {
                    if (file.getName().contains(mask)) {
                        resultQueue.add(file.getAbsolutePath());
                    }
                }
            }
        }
    }
}
