import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;

public class Admin extends User {
    private final String password;
    Wallet wallet;

    Admin(String username, String password, LocalDate birthday, String ID) {
        this.birthday = birthday;
        this.password = password;
        this.username = username;
        this.ID = ID;
        if(Database.getInstance().getAdmin() == null)Database.getInstance().setAdmin(this);
        else{throw new IllegalArgumentException("Invalid Entry");}
    }
    public boolean logIn(String username, String password,String ID) {
        return Database.getInstance().getAdmin().username.equals(username) &&
                Database.getInstance().getAdmin().password.equals(password) &&
                Database.getInstance().getAdmin().ID.equals(ID);
    }
    public static @Nullable Admin signUp(String username, String password, LocalDate birthday, String ID) {
        if (validateCredentials(username, password, birthday, ID))
          return new Admin(username, password, birthday, ID);
        return null;
    }
    public static boolean validateCredentials(String username, String password, @NotNull LocalDate birthday, String ID) {
        if  (birthday.getYear() < LocalDate.now().getYear()-18 ||
                birthday.getYear() < LocalDate.now().getYear()-120||
                birthday.isAfter(LocalDate.now()))
            throw new IllegalArgumentException("Invalid Age");

       if(!(password.length() >= 8 && password.matches(".*[!@#$%^&].*")))throw new IllegalArgumentException("Weak Password");
       return true;
    }
    public double getRevenue() {
        for(Guest guest: Database.getInstance().guests)
        {
            for (Room room: guest.getRoomsReserved() )
            {
                wallet.getMoney(room.price);
            }
        }
        return wallet.getBalance();
    }
    public void flagOverdueCustomers() {
        for(Guest guest: Database.getInstance().guests)
         for(Room room: guest.getRoomsReserved())
            if (LocalDate.now().isAfter(room.checkout)) {
                guest.flagged = true;
                guest.countFlagged++;
                if (guest.countFlagged > 3) {
                    for (Room room1: guest.getRoomsReserved())
                        room1.available = true;
                    Database.getInstance().guests.remove(guest);
                }
            }
    }
    public void Resign() {
    Database.getInstance().setAdmin(null);
}
    void giveSalary(String  ID)
    {
        for (HotelAdmin hotelAdmin : Database.getInstance().hotelAdmins)
        {
            wallet.Pay(50);
            if (hotelAdmin.ID.equals(ID))hotelAdmin.wallet.getMoney(50);
        }
    }
    public Hotel createHotel(String name, Ratings rating, String address) {
        for(Hotel hotel: Database.getInstance().hotels)
        {
            if (hotel.getHotelName().equals(name))
            {
                throw new IllegalArgumentException("Hotel Name taken\n");
            }
        }
        return new Hotel(name, rating, address);
    }
    public String viewHotels()
    {
        StringBuilder sb = new StringBuilder();
        for (Hotel hotel: Database.getInstance().hotels)
        {
            sb.append(hotel.toString()).append("\n");
        }
        return sb.toString();
    }


    @Override
    public String toString() {
        return "Admin{" +
                "username='" + username + '\'' +
                ", birthday=" + birthday +
                ", ID='" + ID + '\'' +
                '}';
    }
}
