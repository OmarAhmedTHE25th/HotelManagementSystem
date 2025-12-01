import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.Period;

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
        this.wallet = new Wallet();
    }
    public boolean logIn(String username, String password,String ID) {
        return Database.getInstance().getAdmin().username.equals(username) &&
                Database.getInstance().getAdmin().password.equals(password) &&
                Database.getInstance().getAdmin().ID.equals(ID);
    }
    public static void signUp(String username, String password, LocalDate birthday, String ID) {
        if (Database.getInstance().getAdmin() != null) {
            throw new IllegalStateException("An Admin account already exists. Cannot register another.");
        }
        if (validateCredentials(password, birthday))
        {   new Admin(username, password, birthday, ID);
        }
    }


    public static boolean validateCredentials(String password, @NotNull LocalDate birthday) {
        LocalDate today = LocalDate.now();
        int age = Period.between(birthday, today).getYears();
        if  (age < 18 || age > 120 || birthday.isAfter(today))
            throw new IllegalArgumentException("Invalid Age");

       if(password.length() < 8 || !password.matches(".*\\d.*")
               || !password.matches(".*[!@#$%^&?<>_+=*{}()~].*"))throw new IllegalArgumentException("Weak Password");
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
    public void createHotel(String name, Ratings rating, String address) {
        for(Hotel hotel: Database.getInstance().hotels)
        {
            if (hotel.getHotelName().equals(name))
            {
                throw new IllegalArgumentException("Hotel Name taken\n");
            }
        }
        new Hotel(name, rating, address);
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
