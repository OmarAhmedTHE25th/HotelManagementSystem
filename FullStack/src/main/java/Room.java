import java.time.LocalDate;

public class Room {
    public int roomNumber;
    public boolean available = true;
    public double price;
    public Types roomType;
    public LocalDate checkout = null;
    public Hotel hotel;

    Room(){}
    Room(int roomNumber,double price,Types roomType,Hotel hotel)
    {
        if (roomNumber<=0)throw new IllegalArgumentException("Invalid Room Number");
        for (Hotel hotelx: Database.getInstance().hotels)
            for (Room room : hotelx.getRooms()) {
                if (roomNumber == room.roomNumber) throw new IllegalArgumentException("Room Number already exists");
                else this.roomNumber=roomNumber;
            }

        this.roomType=roomType;
        this.price = price;
        this.hotel = hotel;
    }

    public void setCheckout(LocalDate checkout) {
        this.checkout = checkout;
    }


    @Override
    public String toString() {
        return "Room{" +
                "roomNumber= " + roomNumber +
                ", available= " + available +
                ", price= $" + price +
                ", roomType= '" + roomType + '\'' +
                '}';
    }
}
