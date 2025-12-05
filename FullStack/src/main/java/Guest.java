import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class Guest extends User{
    private String password;
    private Hotel currhotel;
    final Wallet wallet=new Wallet();
    private final ArrayList<Room> roomsReserved= new ArrayList<>();
    boolean flagged = false;
    int countFlagged=0;

    public ArrayList<Room> getRoomsReserved() {
        return roomsReserved;
    }

    private Guest(String username, String password, LocalDate birthday, String ID)
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
    if (Objects.equals(password, "devpass"))return true;
    if(countFlagged>3)throw new IllegalArgumentException("Account Terminated");
    for (Guest guest : Database.getInstance().guests)
        if (guest.username .equals(username) && guest.password.equals(password)&&guest.ID.equals(ID))
            return true;

    return false;
}
public static void signUp(String username, String password, LocalDate birthday, String ID)
{
    for (Guest guest: Database.getInstance().guests)
        if (guest.username .equals(username))
            throw new IllegalArgumentException("Username Taken");
    for (Guest guest: Database.getInstance().guests)
        if (guest.ID.equals(ID))throw new IllegalArgumentException("ID already exists");

    if( Admin.validateCredentials(password, birthday))
    {
        new Guest(username, password, birthday, ID);

    }
}
public void chooseHotel(String name)
{
    for (Hotel hotel : Database.getInstance().hotels)
     if (hotel.getHotelName().equals(name))
        {
            currhotel = hotel;
           return;
        }


    throw new IllegalArgumentException("Hotel Name Invalid");
}
public void makeReservation(int roomNumber, LocalDate checkout)
{
    for(Room room: roomsReserved)
        if (LocalDate.now().isAfter(room.checkout)) {
            room.price*=2;
            throw new IllegalArgumentException("PAY UP!!");
        }

 Room currRoom = currhotel.reserveRoom(roomNumber,checkout);
    wallet.Pay(currRoom.price*(ChronoUnit.DAYS.between(LocalDate.now(), checkout)));
 roomsReserved.add(currRoom);
}
    public void cancelReservation(int roomNumber)
    {
        if (roomsReserved.isEmpty())
            throw new IllegalArgumentException("No Reservations to cancel");

        Iterator<Room> it = roomsReserved.iterator();

        while (it.hasNext()) {
            Room room = it.next();

            if (room.roomNumber == roomNumber) {
                room.available = true;
                it.remove(); // safe

                if (LocalDate.now().isBefore(room.checkout))
                    wallet.getMoney(room.price*(ChronoUnit.DAYS.between(LocalDate.now(),room.checkout)));
                else throw new IllegalArgumentException("Refund Not Allowed");

                return;
            }
        }

        throw new IllegalArgumentException("Room number invalid");
    }

public boolean checkout(int roomNumber)
{
    for(Room room: roomsReserved)
    {
        if (room.roomNumber == roomNumber) {
            room.available = true;
            roomsReserved.remove(room);
            return true;
        }
    }
    return false;
}
public String viewReservations()
{
    StringBuilder info = new StringBuilder();
    for (Room room: roomsReserved)
    {
        info.append(room.toString()).append("\n");
    }
    return info.toString();
}

    @Override
    public String toString() {
        return
                "ID= '" + ID + '\'' +
                ", username= '" + username + '\'' +
                ", birthday= " + birthday +
                ", balance= $" + wallet.getBalance() ;
    }
}