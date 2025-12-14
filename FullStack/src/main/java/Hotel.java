import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;

public class Hotel implements Serializable {
    private String hotelName;
    Ratings rating;
    private String address;
    private final ArrayList<Room> rooms = new ArrayList<>();
Hotel(){}
    Hotel(String hotelName, Ratings rating, String address) {
        this.hotelName = hotelName;
        this.rating = rating;
        this.address = address;

        Database.getInstance().hotels.add(this);

    }

    public String getHotelName() {
        return hotelName;
    }


    public Room reserveRoom(int roomNumber, @NotNull LocalDate checkout)
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
        return
                "hotelName='" + hotelName + '\'' +
                ", rating='" + rating + '\'' +
                ", address='" + address + '\''
                ;
    }

    public ArrayList<Room> getRooms() {
        return rooms;
    }

    public String getLocation() {
    return address;
    }

    public Ratings getRating() {return rating;
    }

    public String getAddress() {
    return address;
    }
}
