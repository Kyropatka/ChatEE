package academy.prog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.println("Enter your login: ");
            String login = scanner.nextLine();
            System.out.println("Enter your password: ");
            String password = "/credentials " + scanner.nextLine();

            Message credent = new Message(login, password);
            int result = credent.send(Utils.getURL() + "/add");
            if (result == 200) {
                System.out.println("Authorization Successful");
            } else {
                System.out.println("Wrong Password");
                System.exit(1);
            }

            Thread th = new Thread(new GetThread(login));
            th.setDaemon(true);
            th.start();

            System.out.println("Enter your message: ");
            while (true) {
                String text = scanner.nextLine();
                if (text.isEmpty()) {
                    Message m = new Message(login, "/end");
                    m.send(Utils.getURL() + "/add");
                    break;
                }

                // users
                // @test Hello
                Message m = new Message(login, text);
                if (text.startsWith("/online")) {
                    String encodedCommand = URLEncoder.encode(text, StandardCharsets.UTF_8);
                    URL url = new URL(Utils.getURL() + "/online?command=" + encodedCommand);
                    HttpURLConnection http = (HttpURLConnection) url.openConnection();
                    getOnlineList(http);
                } else {
                    int res = m.send(Utils.getURL() + "/add");

                    if (res != 200) { // 200 OK
                        System.out.println("HTTP error occurred: " + res);
                        return;
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    public static void getOnlineList(HttpURLConnection http) throws IOException {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        InputStream is = http.getInputStream();
        byte[] buf = responseBodyToArray(is);
        String strBuf = new String(buf, StandardCharsets.UTF_8);

        if (strBuf.startsWith("[")) {
            Set onlineUsers = gson.fromJson(strBuf, Set.class);
            System.out.println("Online users: " + onlineUsers);
        } else {
            boolean isOnline = gson.fromJson(strBuf, boolean.class);
            System.out.println("Is user online: " + isOnline);
        }
    }

    private static byte[] responseBodyToArray(InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[10240];
        int r;

        do {
            r = is.read(buf);
            if (r > 0) bos.write(buf, 0, r);
        } while (r != -1);

        return bos.toByteArray();
    }
}
