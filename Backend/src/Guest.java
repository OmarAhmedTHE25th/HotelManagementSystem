import java.time.LocalDate;
import java.util.ArrayList;

public class Guest extends User{
    private String password;
    Hotel currhotel;
    Wallet wallet=new Wallet();
    ArrayList<Room> roomsReserved= new ArrayList<>();

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
        if (guest.username .equals(username) && guest.password.equals(password)&&guest.ID.equals(ID))
            return true;

    return false;
}
public boolean signUp(String username, String password, LocalDate birthday,String ID)
{
    for (Guest guest: Database.getInstance().guests)
        if (guest.username .equals(username))
            return false;

    new Guest(username, password, birthday, ID);
    return true;
}
public boolean chooseHotel(String name)
{
    Database data = new Database();
    for (Hotel hotel : data.hotels)
     if (hotel.hotelName.equals( name))
        {
            currhotel = hotel;
           return true;
        }


    throw new IllegalArgumentException("Hotel Name Invalid");
}
public boolean makeReservation(int roomNumber,LocalDate checkout)
{
    for(Room room: roomsReserved)
        if (LocalDate.now().isAfter(room.checkout)) {
            room.price*=2;
            throw new IllegalArgumentException("PAY UP!!");
        }
 Room currRoom = currhotel.reserveRoom(roomNumber,checkout);
 roomsReserved.add(currRoom);
 wallet.Pay(currRoom.price);
 return true;
}
public boolean cancelReservation(int roomNumber)
{
    if (roomsReserved.isEmpty())throw new IllegalArgumentException("No Reservations to cancel");

    for (Room room: roomsReserved)
        if(room.roomNumber==roomNumber) {
            room.available = true;
            roomsReserved.remove(room);

            if (LocalDate.now().isBefore(room.checkout))
                wallet.refund(room.price);
            return true;
        }
    throw new IllegalArgumentException("Room number invalid");

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
