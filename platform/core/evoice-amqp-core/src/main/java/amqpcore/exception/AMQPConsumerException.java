package amqpcore.exception;


public class AMQPConsumerException extends RuntimeException {
    public AMQPConsumerException(String message) {
        super(message);
    }

    public AMQPConsumerException(String message, Throwable cause) {
        super(message, cause);
    }
}
