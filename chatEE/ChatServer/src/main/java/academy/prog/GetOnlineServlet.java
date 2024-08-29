package academy.prog;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class GetOnlineServlet extends HttpServlet {
    private Users users = Users.getInstance();
    private Gson gson = new Gson();

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String command = req.getParameter("command");
        resp.setContentType("application/json");
        if (command != null) {
            String json = null;
            if (command.startsWith("/online @")) {
                String login = command.substring(command.indexOf("@") + 1).trim();
                json = gson.toJson(users.isUserOnline(login));
            } else if (command.equals("/online")) {
                json = gson.toJson(users.getActiveUsers());
            }
            if (json != null) {
                OutputStream os = resp.getOutputStream();
                byte[] buffer = json.getBytes(StandardCharsets.UTF_8);
                os.write(buffer);
            }
        }
    }

}
