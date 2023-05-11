package amqpcore.exception;


public class AMQPValidationException extends RuntimeException {
    public AMQPValidationException(String message) {
        super(message);
    }

    public AMQPValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
