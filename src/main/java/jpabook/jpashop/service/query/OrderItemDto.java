package jpabook.jpashop.service.query;

import jpabook.jpashop.domain.OrderItem;
import lombok.Data;

@Data
public class OrderItemDto {
    private String itemName;
    private int orderPrice;
    private int count;

    public OrderItemDto(OrderItem o) {
        itemName = o.getItem().getName();
        orderPrice = o.getOrderPrice();
        count = o.getCount();
    }
}
