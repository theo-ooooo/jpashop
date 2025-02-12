package jpabook.jpashop.service;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    EntityManager em;
    @Autowired OrderService orderService;
    @Autowired
    OrderRepository orderRepository;

    @Test
    public void 상품주문() throws Exception {
        //given
        Member member = createMember();

        Book book = createBook(10000, 10, "서울 JPA");
        int orderCount = 2;
        //when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);
        //then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals(OrderStatus.ORDER, getOrder.getStatus(), "상품 주문시 상태는 Order");
        assertEquals(1, getOrder.getOrderItems().size(), "주문의 상품 종류 수가 정확해야 한다.");
        assertEquals(10000 * orderCount, getOrder.getTotalPrice(), "주문 가격은 가격 * 수량이다.");
        assertEquals(8, book.getStackQuantity(), "주문 수량만큼 재고가 줄어야한다.");

    }

    private Book createBook(int price, int stackQuantity, String name) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStackQuantity(stackQuantity);
        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원");
        member.setAddress(new Address("서울", "강가", "123-123"));
        em.persist(member);
        return member;
    }

    @Test
    public void 주문취소() throws Exception {
        //given
        Member member = createMember();
        Book item = createBook(10000, 10, "서울 JPA");
        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);
        // when
        orderService.cancelOrder(orderId);
        //then

        Order order = orderRepository.findOne(orderId);

        assertEquals(OrderStatus.CANCEL, order.getStatus(), "주문 취소시 상태는 CANCEL 이다.");
        assertEquals(   10, item.getStackQuantity(),"주문이 취소된 상품은 재고가 복구가 되어야한다.");
    }

    @Test
    public void 상품주문_재고수량초과() throws Exception {
        //given
        Member member = createMember();
        Book item = createBook(10000, 10, "서울 JPA");

        int orderCount = 11;

        //when && then
        assertThrows(NotEnoughStockException.class, () -> {
            orderService.order(member.getId(), item.getId(), orderCount);
        }, "재고 수량이 넘어야한다.");


    }

}