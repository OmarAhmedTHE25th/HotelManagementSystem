import java.time.LocalDate;

public class Guest extends User{
    private String password;
    Hotel currhotel;
    Wallet wallet=new Wallet();
    private boolean reserved = false;

Guest(String username, String password, LocalDate birthday,String ID)
{
    this.birthday = birthday;
    this.password = password;
    this.username = username;
    this.ID = ID;
    Database.getInstance().guests.add(this);
}
Guest(){}
public boolean logIn(String username, String password,String ID)
{
    for (Guest guest : Database.getInstance().guests)
    {
        if (guest.username .equals(username) && guest.password.equals(password)&&guest.ID.equals(ID))
        {
            return true;
        }
    }
    return false;
}
public boolean signUp(String username, String password, LocalDate birthday,String ID)
{

    for (Guest guest: Database.getInstance().guests)
    {
        if (guest.username .equals(username))
        {
            return false;
        }
    }
    new Guest(username, password, birthday, ID);
    return true;
}
public boolean chooseHotel(String name)
{
    Database data = new Database();
    for (Hotel hotel : data.hotels)
    {
        if (hotel.hotelName.equals( name))
        {
            currhotel = hotel;
           return true;
        }

    }
    return false;
}
public boolean makeReservation(int roomNumber,LocalDate checkout)
{
    reserved = true;
 Room currRoom = currhotel.reserveRoom(roomNumber,checkout);
 wallet.Pay(currRoom.price);
 return true;
}
public boolean cancelReservation()
{
    if (!reserved)throw new IllegalArgumentException("No Reservation to cancel");
    reserved = false;



    return true;
}


    @Override
    public String toString() {
        return "Guest{" +
                "ID= '" + ID + '\'' +
                ", username= '" + username + '\'' +
                ", birthday= " + birthday +
                ", balance= $" + wallet.getBalance() +
                '}';
    }
}
