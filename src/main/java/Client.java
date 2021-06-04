import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Client {
    private static final String ADDRESS = "http://localhost:4567/";
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static int lastMessageIndex = 0;

    public static void main(String[] args) {
        var username = args[0];
        scheduler.scheduleAtFixedRate(() -> update(username, lastMessageIndex), 0, 1, TimeUnit.SECONDS);
        runSender(username);
    }

    public static void runSender(String username) {
        var scanner = new Scanner(System.in);
        while (true) {
            var message = scanner.nextLine();
            send(username, message);
        }
    }

    public static void send(String username, String message) {
        try {
            URL getAll = new URL(ADDRESS + "send?user=" + username + "&message=" +
                    message.replace(" ", "%20"));
            getAll.openConnection().getContent();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void update(String username, int from) {
        try {
            URL getAll = new URL(ADDRESS + "getAll?from=" + from);
            URLConnection conn = getAll.openConnection();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    lastMessageIndex++;
                    var sender = inputLine.substring(0, inputLine.indexOf(':'));
                    if (sender.equals(username)) {
                        continue;
                    }
                    System.out.println(inputLine);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
