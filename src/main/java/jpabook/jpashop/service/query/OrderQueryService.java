package jpabook.jpashop.service.query;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderSearch;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.order.query.OrderFlatDto;
import jpabook.jpashop.repository.order.query.OrderItemQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.*;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class OrderQueryService {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAll(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName(); // LAzy  초기화
            order.getDelivery().getAddress(); // Lazy 초기화
            List<OrderItem> orderItems = order.getOrderItems();
            System.out.println("orderItems size: " + orderItems.size());
            orderItems.stream().forEach(o -> o.getItem().getName());
        }

        return all;
    }

    public List<OrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAll(new OrderSearch());
        return orders.stream().map(OrderDto::new).collect(toList());
    }

    public List<OrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithItems();
        System.out.println("orders.size() = " + orders.size());
        return orders.stream().map(OrderDto::new).collect(toList());
    }

    public List<OrderDto> ordersV3_page(
            @RequestParam(value = "offect", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "100") int limit) {
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);
        System.out.println("orders.size() = " + orders.size());
        return orders.stream().map(OrderDto::new).collect(toList());
    }

    public List<OrderQueryDto> ordersV4() {
        return orderQueryRepository.findOrderQueryDtos();
    }

    public List<OrderQueryDto> ordersV5() {
        return orderQueryRepository.findAllByDto_optimization();
    }

    public List<OrderQueryDto> ordersV6() {
        List<OrderFlatDto> flats = orderQueryRepository.findAllByDto_flat();

        return flats.stream()
                .collect(groupingBy(o -> new OrderQueryDto(o.getOrderId(),
                                o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()),
                        mapping(o -> new OrderItemQueryDto(o.getOrderId(),
                                o.getItemName(), o.getOrderPrice(), o.getCount()), toList())
                )).entrySet().stream()
                .map(e -> new OrderQueryDto(e.getKey().getOrderId(),
                        e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(),
                        e.getKey().getAddress(), e.getValue()))
                .collect(toList());
    }
}

