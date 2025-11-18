import java.time.LocalDate;
import java.util.ArrayList;

public class Hotel {
    private String hotelName;
    private Ratings rating;
    private String address;
    private final ArrayList<Room> rooms = new ArrayList<>();
Hotel(){}
    private Hotel(String hotelName, Ratings rating, String address) {
        this.hotelName = hotelName;
        this.rating = rating;
        this.address = address;

        Database.getInstance().hotels.add(this);

    }

    public String getHotelName() {
        return hotelName;
    }


    public Hotel createHotel(String name, Ratings rating, String address) {
        for(Hotel hotel: Database.getInstance().hotels)
        {
            if (hotel.hotelName.equals(name))
            {
                throw new IllegalArgumentException("Hotel Name taken\n");
            }
        }
        return new Hotel(name, rating, address);
    }
    public Room reserveRoom(int roomNumber,LocalDate checkout)
    {
        if (checkout.isBefore(LocalDate.now()))
            throw new IllegalArgumentException("Time travel not supported.");
        for (Room room: rooms)
            if (room.roomNumber == roomNumber)
            {
                room.setCheckout(checkout);
                if (!room.available) throw new IllegalArgumentException("Room already booked");
                room.available = false;
                return room;
            }

        throw new IllegalArgumentException("Room Number Invalid");
    }

    @Override
    public String toString() {
        return "Hotel{" +
                "hotelName='" + hotelName + '\'' +
                ", rating='" + rating + '\'' +
                ", address='" + address + '\'' +
                '}';
    }

    public ArrayList<Room> getRooms() {
        return rooms;
    }
}
