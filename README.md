# echo-voice-solution

The solution of using users product data developed with spring-boot-cloud
like API Gateway / Eureka Discovery Server / Keycloak / Zipkin / Prometheus / Grafana / Docker Compose

### evoice-api-gateway

The Spring API GATEWAY service like a gate from an external process to an internal one.
It also serves as a user authentication point through KEYCLOAK & resource server.

### evoice-naming-service

The Spring DISCOVERY SERVER service as an observer for the list of declared services.
It also works with the RabbitMQ Load Balancer.

### evoice-user-service

The Spring User Service as a source service for data about system users,
as well as an end point for maintaining information.
To communicate with other services (meaning resource services), an AMQP client is used.

### platform

The platform service is used as a source of aggregated classes
and sets of dependencies for the operation of other services (Maven pom inheritance).

### other application commons

Stack of commons:
...