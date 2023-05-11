package amqpcore.exception;


public class AMQPProducerException extends RuntimeException {
    public AMQPProducerException(String message) {
        super(message);
    }

    public AMQPProducerException(String message, Throwable cause) {
        super(message, cause);
    }
}
