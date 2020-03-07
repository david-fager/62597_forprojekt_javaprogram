package java_server;

public class Session {
    private int id = 0;
    private String username = "unknown";
    private Galgelogik galgelogik;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Galgelogik getGalgelogik() {
        return galgelogik;
    }

    public void setGalgelogik(Galgelogik galgelogik) {
        this.galgelogik = galgelogik;
    }

    @Override
    public String toString() {
        if (galgelogik != null) {
            return "Session#" + id + "\tUsername: " + username + "\tHangman: Has Object.";
        } else {
            return "Session#" + id + "\tUsername: " + username + "\tHangman: No Object.";
        }
    }
}
