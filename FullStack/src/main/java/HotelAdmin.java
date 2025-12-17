import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.time.LocalDate;


public class HotelAdmin extends User implements Serializable {
    private Hotel hotel;
    private String password;
    Wallet wallet;
    boolean paid = false;
    public LocalDate lastPaymentDate;
    HotelAdmin(){}
    HotelAdmin(String username, String password, LocalDate birthday, String ID,Hotel hotel)
    {
        this.birthday = birthday;
        this.password = password;
        this.username = username;
        this.ID = ID;
        this.hotel = hotel;
        this.wallet = new Wallet();
        Database.getInstance().hotelAdmins.add(this);


    }
    public boolean logIn(String username, String password,String ID)
    {
        for (HotelAdmin hotelAdmin : Database.getInstance().hotelAdmins)
            if (hotelAdmin.username .equals(username) && hotelAdmin.password.equals(password)&&hotelAdmin.ID.equals(ID))
                return true;

        return false;
    }
    public static @Nullable HotelAdmin signUp(String username, String password, LocalDate birthday, String ID, Hotel hotel)
    {
        for (HotelAdmin hotelAdmin: Database.getInstance().hotelAdmins)
            if (hotelAdmin.username .equals(username))
                throw new IllegalArgumentException("Username Taken");
        for (HotelAdmin hotelAdmin: Database.getInstance().hotelAdmins)
            if (hotelAdmin.ID.equals(ID))throw new IllegalArgumentException("ID already exists");

        if( Admin.validateCredentials(password, birthday))
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
    {   if (roomNumber <= 0) throw new IllegalArgumentException("Room number must be positive");
        if (price <= 0) throw new IllegalArgumentException("Unless you like debts. Make the price cant be Negative");
        if (hotel.getRooms().size()>100) throw new IllegalArgumentException("Hotel Full. We are done accepting monkeys.");
        for (Room room: hotel.getRooms())
            if (room.roomNumber == roomNumber)
                throw new IllegalArgumentException("Room Number Taken\n");


        hotel.getRooms().add(new Room(roomNumber, price, roomType,getHotel()));
    }
    public void changeRoomPrice(int roomNumber,int newPrice)
    {
        if (newPrice <= 0) throw new IllegalArgumentException("Unless you like debts. Make the price cant be Negative");
        for (Room room: hotel.getRooms())
            if (room.roomNumber == roomNumber){room.price = newPrice; return;}
        throw new IllegalArgumentException("Room Number Invalid");
    }
    public void checkPaymentStatus() {
        // Only check if they are currently marked as paid
        if (paid && lastPaymentDate != null) {
            LocalDate today = LocalDate.now();

            // Calculate days passed
            long daysPassed = java.time.temporal.ChronoUnit.DAYS.between(lastPaymentDate, today);

            // If 30 or more days have passed, reset it
            if (daysPassed >= 30) {
                paid = false;
            }
        }
    }

    public void receiveSalary() {
        this.wallet.getMoney(50);
        this.paid = true;
        this.lastPaymentDate = LocalDate.now(); // Saves "today" as the payment date
    }
}
