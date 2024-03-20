package com.renvema;

public class Message {

    public final static String CLIENT_CONNECTED = "Client connected: ";

    public final static String INPUT_PROMPT = "Enter search depth and mask (e.g., '2 *.txt'):";

    public final static String INVALID_DEPTH_MESSAGE = "Invalid depth value.";

    public final static String INVALID_DIRECTORY_MESSAGE = "Invalid root directory.";

    public final static String INVALID_FORMAT_MESSAGE = "Invalid input format. Please enter search depth and mask separated by space.";

    public final static String INVALID_MASK_MESSAGE = "Invalid mask value.";

    public final static String SEARCH_COMPLETED_MESSAGE = "Search completed. Enter new search depth and mask:";

    public final static String SERVER_STARTED_ON_PORT = "Telnet server started on port ";

    public final static String USAGE_MESSAGE = "Usage: java FileSystemTraversal <rootPath> <depth> <mask>";

    public final static String WELCOME_MESSAGE = "Welcome to Telnet File System Server!";

}
