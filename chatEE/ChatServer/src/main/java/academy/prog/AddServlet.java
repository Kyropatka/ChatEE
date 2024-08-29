package academy.prog;

import jakarta.servlet.http.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/*
    POST(json) -> /add -> AddServlet -> MessageList
    GET -> /get?from=x -> GetListServlet <- MessageList
        <- json[...]
 */

public class AddServlet extends HttpServlet {

    private MessageList msgList = MessageList.getInstance();
    private Users user = Users.getInstance();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        byte[] buf = requestBodyToArray(req); // json
        String bufStr = new String(buf, StandardCharsets.UTF_8);

        Message msg = Message.fromJSON(bufStr);
        if (msg != null) {
            System.out.println("Received message: " + msg.toString());

            if (msg.getText().startsWith("/")) {
                if (msg.getText().startsWith("/credentials")) {
                    doAuth(resp, msg);
                } else if (msg.getText().startsWith("/online @")) {

                } else if (msg.getText().startsWith("/online")) {

                } else if (msg.getText().startsWith("/end")) {
                    user.deleteActiveUser(msg.getFrom());
                }
            } else {
                if (!checkPrivateMessage(msg, resp)) {
                    msgList.add(msg);
                }
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private boolean checkPrivateMessage(Message msg, HttpServletResponse resp) {
        if (msg.getText().startsWith("@")) {
            int endName = msg.getText().indexOf(' ');
            if (endName > 0) {
                String recipient = msg.getText().substring(1, endName);
                msg.setTo(recipient);
                msg.setText(msg.getText().substring(endName + 1));

                Message privateMsg = new Message(msg.getFrom(), msg.getTo(), msg.getText());
                msgList.add(privateMsg);
            }
            return true;
        }
        return false;
    }

    private byte[] requestBodyToArray(HttpServletRequest req) throws IOException {
        InputStream is = req.getInputStream();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[10240];
        int r;

        do {
            r = is.read(buf);
            if (r > 0) bos.write(buf, 0, r);
        } while (r != -1);

        return bos.toByteArray();
    }

    private void doAuth(HttpServletResponse response, Message msg) throws IOException {
        String password = msg.getText().substring(msg.getText().indexOf(" " + 1));
        System.out.println(password);

        if (user.checkLogin(msg.getFrom())) {
            if (user.checkPassword(msg.getFrom(), password)) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("Login successful");

                user.addActiveUser(msg.getFrom());
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Incorrect password");
            }
        } else {
            user.createNewUser(msg.getFrom(), password);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("New user created successfully");

            user.addActiveUser(msg.getFrom());
        }
    }
}
