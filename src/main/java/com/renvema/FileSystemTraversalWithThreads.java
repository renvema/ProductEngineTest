package com.renvema;

import java.io.File;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static com.renvema.Message.*;

public class FileSystemTraversalWithThreads {

    private static final BlockingQueue<String> resultQueue = new LinkedBlockingQueue<>();

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println(USAGE_MESSAGE);
            return;
        }

        String rootPath = args[0];
        int depth = Integer.parseInt(args[1]);
        String mask = args[2];

        if (depth < 0) {
            System.out.println(INVALID_DEPTH_MESSAGE);
            return;
        }

        if (mask.isEmpty()) {
            System.out.println(INVALID_MASK_MESSAGE);
            return;
        }

        File rootDir = new File(rootPath);
        if (!rootDir.exists() || !rootDir.isDirectory()) {
            System.out.println(INVALID_DIRECTORY_MESSAGE);
            return;
        }

        Thread searchThread = new Thread(() -> traverseFileSystem(rootDir, depth, mask));
        searchThread.start();

        Thread outputThread = new Thread(() -> {
            while (true) {
                try {
                    String filePath = resultQueue.take();
                    System.out.println(filePath);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        outputThread.start();
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
                        try {
                            resultQueue.put(file.getAbsolutePath());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
