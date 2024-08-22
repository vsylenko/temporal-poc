package ca.sylenko.temporal.model;

import java.util.List;

public record Order(String id, List<OrderItem> orderItems) {
}
