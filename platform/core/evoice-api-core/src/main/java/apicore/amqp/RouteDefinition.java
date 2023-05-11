package apicore.amqp;

import lombok.experimental.UtilityClass;

@UtilityClass
public class RouteDefinition {
    public final String GET_PRODUCTS_BY_ID = "routes.product.find_by_product_id";
    public final String GET_PRODUCTS_BY_USER_ID = "routes.product.find_by_user_id";
}
