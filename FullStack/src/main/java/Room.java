import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

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
        return
                "roomNumber= " + roomNumber +
                ", price = $" + price* ChronoUnit.DAYS.between(LocalDate.now(),checkout) +
                ", roomType= '" + roomType + '\'' ;
    }
}
