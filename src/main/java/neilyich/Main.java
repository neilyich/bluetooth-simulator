package neilyich;

import javax.bluetooth.*;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import java.io.*;

public class Main {
    static final String serverUUID = "0000110100001000800000805F9B34FB";

    public static void main(String[] args) throws IOException, InterruptedException {

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
            new Thread(() -> {
                try {
                    BufferedReader scanner = new BufferedReader(new InputStreamReader(connection.openInputStream()));
                    PrintStream writer = new PrintStream(connection.openOutputStream());

                    String read = scanner.readLine();
                    while (true) {
                        if (read != null) {
                            System.out.println(read);
                            System.out.println("Received: " + read);
                            String response = handleToUtf8(read);
                            System.out.println("Sending: " + response);
                            writer.println(response);
                        } else {
                            Thread.sleep(100);
                        }
                        read = scanner.readLine();
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    try {
                        connection.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();
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