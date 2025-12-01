import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;

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
    if(countFlagged>3)throw new IllegalArgumentException("Account Terminated");
    for (Guest guest : Database.getInstance().guests)
        if (guest.username .equals(username) && guest.password.equals(password)&&guest.ID.equals(ID))
            return true;

    return false;
}
public static @Nullable Guest signUp(String username, String password, LocalDate birthday, String ID)
{
    for (Guest guest: Database.getInstance().guests)
        if (guest.username .equals(username))
            throw new IllegalArgumentException("Username Taken");
    for (Guest guest: Database.getInstance().guests)
        if (guest.ID.equals(ID))throw new IllegalArgumentException("ID already exists");

    if( Admin.validateCredentials(password, birthday))
    {
       return new Guest(username, password, birthday, ID);

    }
    return null;
}
public boolean chooseHotel(String name)
{
    for (Hotel hotel : Database.getInstance().hotels)
     if (hotel.getHotelName().equals(name))
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
    wallet.Pay(currRoom.price);
 roomsReserved.add(currRoom);
 return true;
}
    public boolean cancelReservation(int roomNumber)
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
                    wallet.getMoney(room.price);

                return true;
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
        return "Guest{" +
                "ID= '" + ID + '\'' +
                ", username= '" + username + '\'' +
                ", birthday= " + birthday +
                ", balance= $" + wallet.getBalance() +
                '}';
    }
}