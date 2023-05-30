package neilyich;

import javax.bluetooth.*;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import java.io.*;
import java.util.Scanner;

public class Main {
    static final String serverUUID = "0000110100001000800000805F9B34FB";

    static final int TIMEOUT_MILLIS = 500;

    public static void main(String[] args) throws IOException, InterruptedException {

//        if (true) {
//            sendData(new PrintStream(System.out), null);
//            return;
//        }

        System.identityHashCode(null);

        LocalDevice localDevice = LocalDevice.getLocalDevice();

        localDevice.setDiscoverable(DiscoveryAgent.GIAC); // Advertising the service

        //String url = "btspp://localhost:" + serverUUID + ";name=BlueToothServer";
        String url = "btspp://localhost:" + serverUUID + ";name=BlueToothServer";
        StreamConnectionNotifier server = (StreamConnectionNotifier) Connector.open(url);

        System.out.println("Listening to connections");

        while (true) {
            StreamConnection connection = server.acceptAndOpen(); // Wait until client connects
            System.out.println("Got new connection");
            PrintStream writer = new PrintStream(connection.openOutputStream());
            new Thread(() -> sendData(writer, () -> {
                try {
                    connection.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            })).start();
        }
    }

    private static void sendData(PrintStream writer, Runnable finallyTask) {
        try {
            Scanner scanner = new Scanner(new File("data.txt"));
            scanner.nextLine();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] splitted = scanner.nextLine().split(",");
                Thread.sleep(TIMEOUT_MILLIS);
                writer.println(line);
                System.out.println("Sent: " + line);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (finallyTask != null) finallyTask.run();
        }
    }

    private static String handle(String input) {
        switch (input) {
            case "Up": return "\u2191";
            case "UpRight": return "\u2197";
            case "UpLeft": return "\u2196";
            case "Down": return "\u2193";
            case "DownRight": return "\u2198";
            case "DownLeft": return "\u2199";
            case "NoDirection": return "\u2022";
            case "Right": return "\u2192";
            case "Left": return "\u2190";
        }
        return "+";
    }

    private static String handleToUtf8(String input) {
        return handle(input);
    }
}