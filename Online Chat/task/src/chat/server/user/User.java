package chat.server.user;

import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.TemporalUnit;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class User implements Serializable {

    private String name;

    private byte[] hashedPassword;

    private Roles role;

    private Instant timeOfBan;

    public User(String name, byte[] hashedPassword) {
        this.name = name;
        this.hashedPassword = hashedPassword;
        this.role = Roles.USER;
    }

    public User(String name, byte[] hashedPassword, Roles role) {
        this.name = name;
        this.hashedPassword = hashedPassword;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public byte[] getHashedPassword() {
        return hashedPassword;
    }

    public Roles getRole() {
        return role;
    }

    public void setRole(Roles role) {
        this.role = role;
    }

    public boolean isAdmin() {
        return role == Roles.ADMIN;
    }

    public boolean isModerator() {
        return role == Roles.MODERATOR;
    }

    public boolean isKicked() {
        if (timeOfBan == null) {
            return false;
        }

        if (Instant.now().minusSeconds(300).isAfter(timeOfBan)) {
            timeOfBan = null;
        }

        return timeOfBan != null;
    }

    public void kick() {
        timeOfBan = Instant.now();
    }

    public boolean authenticate(User user) {
        if (user == null) return false;

        if (!Objects.equals(name, user.name)) return false;
        if (!Arrays.equals(hashedPassword, user.hashedPassword)) return false;

        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (!Objects.equals(name, user.name)) return false;
        if (!Arrays.equals(hashedPassword, user.hashedPassword)) return false;
        if (role != user.role) return false;
        return Objects.equals(timeOfBan, user.timeOfBan);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + Arrays.hashCode(hashedPassword);
        result = 31 * result + (role != null ? role.hashCode() : 0);
        result = 31 * result + (timeOfBan != null ? timeOfBan.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("User %s with role %s, hashpw %s", name, role.name(), Arrays.toString(hashedPassword));
    }
}
