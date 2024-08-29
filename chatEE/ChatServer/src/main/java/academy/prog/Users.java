package academy.prog;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Users {
    private static final Users user = new Users();

    private String login;
    private String password;
    private final Map<String, String> usersData = new HashMap<String, String>();
    private final Set<String> activeUsers = new HashSet<>();

    public static Users getInstance() {
        return user;
    }

    private Users() {
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void createNewUser(String login, String password) {
        this.usersData.put(login, password);
    }

    public boolean checkLogin(String login) {
        return usersData.containsKey(login);
    }

    public boolean checkPassword(String login, String password) {
        return usersData.get(login).contains(password);
    }

    public void addActiveUser(String login) {
        this.activeUsers.add(login);
    }

    public void deleteActiveUser(String login) {
        this.activeUsers.remove(login);
    }

    public Set<String> getActiveUsers() {
        return activeUsers;
    }

    public boolean isUserOnline(String login) {
        return activeUsers.contains(login);
    }

}
