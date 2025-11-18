import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;


public class HotelAdmin extends User{
    private Hotel hotel;
    private String password;

    HotelAdmin(){}
    HotelAdmin(String username, String password, LocalDate birthday, String ID,Hotel hotel)
    {
        this.birthday = birthday;
        this.password = password;
        this.username = username;
        this.ID = ID;
        this.hotel = hotel;
        Database.getInstance().hotelAdmins.add(this);
    }
    public boolean logIn(String username, String password,String ID)
    {
        for (HotelAdmin hotelAdmin : Database.getInstance().hotelAdmins)
            if (hotelAdmin.username .equals(username) && hotelAdmin.password.equals(password)&&hotelAdmin.ID.equals(ID))
                return true;

        return false;
    }
    public static @Nullable HotelAdmin signUp(String username, String password, LocalDate birthday, String ID,Hotel hotel)
    {
        for (HotelAdmin hotelAdmin: Database.getInstance().hotelAdmins)
            if (hotelAdmin.username .equals(username))
                throw new IllegalArgumentException("Username Taken");
        for (HotelAdmin hotelAdmin: Database.getInstance().hotelAdmins)
            if (hotelAdmin.ID.equals(ID))throw new IllegalArgumentException("ID already exists");

        if( Admin.validateCredentials(username, password, birthday, ID))
        {
            return new HotelAdmin(username, password, birthday, ID,hotel);

        }
        return null;
    }

    public Hotel getHotel() {
        return hotel;
    }
    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }
    public void createRoom(int roomNumber, double price, Types roomType)
    {   if (hotel.getRooms().size()>100) throw new IllegalArgumentException("Hotel Full");
        for (Room room: hotel.getRooms())
            if (room.roomNumber == roomNumber)
                throw new IllegalArgumentException("Room Number Taken\n");


        hotel.getRooms().add(new Room(roomNumber, price, roomType,getHotel()));
    }
    public void changeRoomPrice(int roomNumber,int newPrice)
    {
        for (Room room: hotel.getRooms())
            if (room.roomNumber == roomNumber){room.price = newPrice; return;}
        throw new IllegalArgumentException("Room Number Invalid");
    }
}
