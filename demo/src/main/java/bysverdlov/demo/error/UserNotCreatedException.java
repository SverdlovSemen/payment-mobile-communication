package bysverdlov.demo.error;

public class UserNotCreatedException extends RuntimeException {
    public UserNotCreatedException(String msg) {super(msg);}
}