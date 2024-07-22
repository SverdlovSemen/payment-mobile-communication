package bysverdlov.demo.error;

public class EmailNotValidException extends RuntimeException {
    public EmailNotValidException(String msg){
        super(msg);
    }
}
