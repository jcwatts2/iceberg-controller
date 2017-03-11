package iceburg.maincontroller;


/**
 * Created by jwatts on 3/9/17.
 */
public class InitializationException extends RuntimeException {

    public InitializationException(final String message) {
        super(message);
    }

    public InitializationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
