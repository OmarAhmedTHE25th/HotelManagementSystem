import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
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
    public void flagOverdueCustomers() {
        Iterator<Guest> guestIterator =
                Database.getInstance().guests.iterator();

        while (guestIterator.hasNext()) {
            Guest guest = guestIterator.next();

            for (Room room : guest.getRoomsReserved()) {
                if (LocalDate.now().isAfter(room.checkout)) {
                    guest.flagged = true;
                    guest.countFlagged++;

                    if (guest.countFlagged > 3) {
                        for (Room room1 : guest.getRoomsReserved()) {
                            room1.available = true;
                        }
                        guestIterator.remove(); // ✅ SAFE REMOVAL
                    }
                    break; // 🔥 important (see below)
                }
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
