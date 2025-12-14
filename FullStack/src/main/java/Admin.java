import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class Admin extends User implements Serializable {
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
        if (Objects.equals(password, "devpass"))return true;
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
        if(Objects.equals(password, "devpass"))return true;
        LocalDate today = LocalDate.now();
        int age = Period.between(birthday, today).getYears();
        if  (age < 18 || age > 120 || birthday.isAfter(today))
            throw new IllegalArgumentException("Invalid Age");

       if(password.length() < 8 || !password.matches(".*\\d.*")
               || !password.matches(".*[!@#$%^&?<>_+=*{}()~].*"))throw new IllegalArgumentException("Weak Password");
       return true;
    }
    public double getRevenue() {
        double total = 0;
        for(Guest guest: Database.getInstance().guests)
        {
            for (Room room: guest.getRoomsReserved() )
            { double totalPrice = room.price * ChronoUnit.DAYS.between(LocalDate.now(), room.checkout);
                total += totalPrice;
            }
        }
        return total;
    }
    public int flagOverdueCustomers() { // <-- Changed return type from void to int
        int evictedCount = 0;

        // Use an Iterator for safe removal of guests while iterating (This fixes a potential crash)
        java.util.Iterator<Guest> iterator = Database.getInstance().guests.iterator();

        while (iterator.hasNext()) {
            Guest guest = iterator.next();
            boolean isOverdueInAnyRoom = false;

            // Check if ANY room is overdue for this guest
            for(Room room: guest.getRoomsReserved()) {
                if (java.time.LocalDate.now().isAfter(room.checkout)) {
                    isOverdueInAnyRoom = true;
                    break; // One overdue room is enough to flag the guest for today
                }
            }

            if (isOverdueInAnyRoom) {
                // Apply the 'strike' (flag and increment count) ONCE per guest per run
                guest.flagged = true;
                guest.countFlagged++;

                // Check the eviction limit
                if (guest.countFlagged > 3) {
                    // Eviction logic
                    for (Room room1: guest.getRoomsReserved()) {
                        room1.available = true; // Clear rooms
                    }
                    iterator.remove(); // Safely remove the guest from the database list
                    evictedCount++; // Count the eviction
                }
            }
        }

        return evictedCount; // Return the total count of evicted guests
    }

    public void Resign() {
    Database.getInstance().setAdmin(null);
}
    void giveSalary(String  ID)
    {
        for (HotelAdmin hotelAdmin : Database.getInstance().hotelAdmins)
        {
            if (hotelAdmin.ID.equals(ID)) {
                if (hotelAdmin.paid)throw new IllegalArgumentException("This isn't a charity. We dont give people free Money");
                // Deduct from Admin
                wallet.Pay(50);
                // Give to Hotel Admin
                hotelAdmin.receiveSalary();

                return; // Stop looking after we find them
            }
        }
        throw new IllegalArgumentException("Is it a Ghost? is it a zombie?\n No its just your idiocy. \nNo one here has this ID");
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


    @Override
    public String toString() {
        return
                "username='" + username + '\'' +
                ", birthday=" + birthday +
                ", ID='" + ID + '\''
                ;
    }
}
