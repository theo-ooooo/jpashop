package jpabook.jpashop.domain.item;


import jakarta.persistence.*;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Getter @Setter
public abstract class Item {
    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stackQuantity;


    public void addStock(int quantity) {
        this.stackQuantity += quantity;
    }

    public void removeStock(int quantity) {
        int restStock = this.stackQuantity - quantity;
        if (restStock < 0) {
            throw new NotEnoughStockException("need more stock");
        }
        stackQuantity = restStock;
    }
}
