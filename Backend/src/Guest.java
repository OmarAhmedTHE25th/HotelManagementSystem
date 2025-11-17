import java.time.LocalDate;

public class Guest extends User{
    private String password;
Guest(String username, String password, LocalDate birthday,String ID)
{
    this.birthday = birthday;
    this.password = password;
    this.username = username;
    this.ID = ID;
    Database.getInstance().guests.add(this);
}
public void browseHotels(String name)
{
    Database data = new Database();
    for (Hotel hotel : data.hotels)
    {
        if (hotel.hotelName.equals( name))
        {

        }

    }
}
public boolean makeReservation(Room room)
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
