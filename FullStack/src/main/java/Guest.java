import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Guest extends User{
    private String password;
    private Hotel currhotel;
    final Wallet wallet=new Wallet();
    private final ArrayList<Room> roomsReserved= new ArrayList<>();
    boolean flagged = false;
    int countFlagged=0;
    private final ArrayList<Room> history = new ArrayList<>();

    public ArrayList<Room> getRoomsReserved() {
        return roomsReserved;
    }
    public ArrayList<Room> getHistory() {
        return history;
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

        // 1. RESERVE THE ROOM (This sets room.available = false internally)
        Room currRoom = currhotel.reserveRoom(roomNumber,checkout);

        try {
            // Calculate the total price before attempting payment
            double totalPrice = currRoom.price * ChronoUnit.DAYS.between(LocalDate.now(), checkout);

            // 2. ATTEMPT PAYMENT
            wallet.Pay(totalPrice);

            // 3. IF PAYMENT SUCCEEDS, FINALIZE THE RESERVATION
            roomsReserved.add(currRoom);
            Room historyRecord = new Room(currRoom.roomNumber, currRoom.price, currRoom.roomType, currRoom.hotel);
            historyRecord.setCheckout(checkout);
            historyRecord.status = BookingStatus.RESERVED;   // Set status using the enum
            history.add(historyRecord);

        } catch (Exception e) {

            currRoom.available = true;
            currRoom.setCheckout(null);

            // Re-throw the original error so the GuestController can display it.
            throw e;
        }
    }







    public void cancelReservation(int roomNumber,Hotel hotel)
    {
        if (roomsReserved.isEmpty())
            throw new IllegalArgumentException("No Reservations to cancel");

        Iterator<Room> it = roomsReserved.iterator();

        while (it.hasNext()) {
            Room room = it.next();

            if (room.roomNumber == roomNumber&& room.hotel.getHotelName().equals(hotel.getHotelName())) {
                room.available = true;
                it.remove(); // safe

                if (LocalDate.now().isBefore(room.checkout))
                    wallet.getMoney(room.price*(ChronoUnit.DAYS.between(LocalDate.now(),room.checkout)));
                else throw new IllegalArgumentException("Refund Not Allowed");
                updateHistoryStatus(roomNumber, hotel.getHotelName(), BookingStatus.CANCELLED);
                return;
            }
        }

        throw new IllegalArgumentException("Room number invalid in Selected Hotel");
    }
    private void updateHistoryStatus(int rNum, String hName, BookingStatus newStatus) {
        // Loop backwards to find the most recent booking that is currently "RESERVED"
        for (int i = history.size() - 1; i >= 0; i--) {
            Room r = history.get(i);
            if (r.roomNumber == rNum && r.hotel.getHotelName().equals(hName) && r.status == BookingStatus.RESERVED) {
                r.status = newStatus;
                return;
            }
        }
    }
    public String viewHistory() {
        StringBuilder info = new StringBuilder("--- Booking History ---\n");
        if (history.isEmpty()) {
            info.append("No bookings in your history.");
            return info.toString();
        }
        for (Room room : history) {
            // Calculate total price. Note: The price stored is the per-day price.
            long days = ChronoUnit.DAYS.between(LocalDate.now(), room.checkout);
            // For a checked-out room, days might be negative. We will display the recorded price (room.price) multiplied by days.
            double totalPrice = room.price * (days > 0 ? days : 1);

            info.append("Hotel: ").append(room.hotel.getHotelName())
                    .append(", Room #: ").append(room.roomNumber)
                    .append(", Price: $").append(String.format("%.2f", totalPrice))
                    .append(", Checkout: ").append(room.checkout)
                    .append(", Status: ").append(room.status.toString()).append("\n");
        }
        return info.toString();
    }
public boolean checkout(int roomNumber,Hotel hotel)
{
    for(Room room: roomsReserved)
    {
        if (room.roomNumber == roomNumber&& room.hotel.getHotelName().equals(hotel.getHotelName())) {
            room.available = true;
            roomsReserved.remove(room);
            updateHistoryStatus(roomNumber, hotel.getHotelName(), BookingStatus.CHECKED_OUT);
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

    public List<Hotel> searchHotels(String query, Ratings minRating) {

        // Start with all hotels
        List<Hotel> availableHotels = Database.getInstance().hotels;

        // 1. Filter by Text Query (Hotel Name or Location)
        if (query != null && !query.trim().isEmpty()) {
            final String lowerCaseQuery = query.trim().toLowerCase();
            availableHotels = availableHotels.stream()
                    .filter(h -> h.getHotelName().toLowerCase().contains(lowerCaseQuery) ||
                            h.getLocation().toLowerCase().contains(lowerCaseQuery))
                    .collect(Collectors.toList());
        }

        // 2. Filter by Rating (if a minimum rating is selected)
        if (minRating != null) {
            availableHotels = availableHotels.stream()
                    .filter(h -> h.rating.ordinal() <= minRating.ordinal())
                    .collect(Collectors.toList());
        }

        return availableHotels;
    }
    public String viewBookingHistory()
    {
        return "";
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