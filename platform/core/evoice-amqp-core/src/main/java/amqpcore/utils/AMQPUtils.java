package amqpcore.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AMQPUtils {
    public final String REQIEST_QUEUE_SUFFIX = ".requests.queue";
    public final String REQIEST_DLQ_QUEUE_SUFFIX = ".requests.queue-DLQ";
    public final String RESPONSE_QUEUE_SUFFIX = ".responses.queue";
    public final String RESPONSE_DLQ_QUEUE_SUFFIX = ".responses.queue-DLQ";

    public final String EVOICE_USER_SERVICE = "evoice-user-service";
    public final String EVOICE_PRODUCT_SERVICE = "evoice-product-service";
}
