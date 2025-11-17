import java.time.LocalDate;
import java.util.ArrayList;

public class Hotel {
    public String hotelName;
    public float rating;
    public String address;
    public ArrayList<Room> rooms = new ArrayList<>();
    int roomN = 0;
Hotel(){}
    Hotel(String hotelName, float rating, String address) {
        this.hotelName = hotelName;
        this.rating = rating;
        this.address = address;

        Database.getInstance().hotels.add(this);

    }

    public static Hotel createHotel(String name, float rating, String address) {
        for(Hotel hotel: Database.getInstance().hotels)
        {
            if (hotel.hotelName.equals(name))
            {
                throw new IllegalArgumentException("Hotel Name taken\n");
            }
        }
        return new Hotel(name, rating, address);
    }

    public void createRoom(int roomNumber, double price, String roomType)
    {     if (rooms.size()>100) throw new IllegalArgumentException("Hotel Full");
        for (Room room: rooms)
        {
            if (room.roomNumber == roomNumber)
            {
                throw new IllegalArgumentException("Room Number Taken\n");
            }
        }
        rooms.add(new Room(roomNumber, price, roomType));
    }
    public Room reserveRoom(int roomNumber, LocalDate checkout)
    {
        if (LocalDate.now().equals(checkout))
        {
            throw new IllegalArgumentException("PAY UP!!");
        }
        for (Room room: rooms)
        {
            if (room.roomNumber == roomNumber)
            {
                if (!room.available)
                {
                    throw new IllegalArgumentException("Room already booked");

                }
                room.available = false;
                return room;
            }
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
}
