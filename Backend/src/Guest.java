import java.time.LocalDate;

public class Guest extends User{
    private String password;
Guest(String username, String password, LocalDate birthday)
{
    this.birthday = birthday;
    this.password = password;
    this.username = username;
}
public boolean makeReservation()
{


 return true;
}

    @Override
    public String toString() {
        return "Guest{" +
                "password='" + password + '\'' +
                ", username='" + username + '\'' +
                ", birthday=" + birthday +
                '}';
    }
}
