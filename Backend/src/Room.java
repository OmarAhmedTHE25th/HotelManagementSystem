import java.time.LocalDate;

public class Room {
    public int roomNumber;
    public boolean available = true;
    public double price;
    public String roomType;
    public LocalDate checkout = null;
    public Hotel hotel;

    Room(){}
    Room(int roomNumber,double price,String roomType,Hotel hotel)
    {
        this.roomNumber=roomNumber;
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
