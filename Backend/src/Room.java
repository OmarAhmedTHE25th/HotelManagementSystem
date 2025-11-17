public class Room {
    public int roomNumber;
    public boolean available = true;
    public double price;
    public String roomType;
    Room(){}
    Room(int roomNumber,double price,String roomType)
    {
        this.roomNumber=roomNumber;
        this.roomType=roomType;
        this.price = price;
    }

}
