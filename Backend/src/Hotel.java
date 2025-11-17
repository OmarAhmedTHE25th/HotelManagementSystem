import java.util.ArrayList;

public class Hotel {
    public String hotelName;
    public String rating;
    public String address;
   private ArrayList<Room> rooms = new ArrayList<>();

        Hotel(String hotelName, String rating, String address)
        {
            this.hotelName = hotelName;
            this.rating = rating;
            this.address = address;
            Database.getInstance().hotels.add(this);
        }
    public static Hotel create(String name, String rating,String address) {
        Hotel h = new Hotel(name, rating,address);
        Database.getInstance().hotels.add(h);
        return h;
    }
    public boolean checkIn()
    {

     return true;
    }
}
