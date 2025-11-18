import java.time.LocalDate;
import java.util.ArrayList;

public class Hotel {
    public String hotelName;
    public Ratings rating;
    public String address;
    public ArrayList<Room> rooms = new ArrayList<>();
Hotel(){}
    private Hotel(String hotelName, Ratings rating, String address) {
        this.hotelName = hotelName;
        this.rating = rating;
        this.address = address;

        Database.getInstance().hotels.add(this);

    }

    public static Hotel createHotel(String name, Ratings rating, String address) {
        for(Hotel hotel: Database.getInstance().hotels)
        {
            if (hotel.hotelName.equals(name))
            {
                throw new IllegalArgumentException("Hotel Name taken\n");
            }
        }
        return new Hotel(name, rating, address);
    }

    public void createRoom(int roomNumber, double price, Types roomType)
    {   if (rooms.size()>100) throw new IllegalArgumentException("Hotel Full");
        for (Room room: rooms)
            if (room.roomNumber == roomNumber)
                throw new IllegalArgumentException("Room Number Taken\n");


        rooms.add(new Room(roomNumber, price, roomType,this));
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
    public void changeRoomPrice(int roomNumber,int newPrice)
    {
        for (Room room: rooms)
            if (room.roomNumber == roomNumber){room.price = newPrice; return;}
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
}
