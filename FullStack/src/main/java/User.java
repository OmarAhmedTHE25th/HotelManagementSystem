import java.io.Serializable;
import java.time.LocalDate;
abstract public class User implements Serializable {
    String username;
    LocalDate birthday;
    String ID;
User(){}
}
