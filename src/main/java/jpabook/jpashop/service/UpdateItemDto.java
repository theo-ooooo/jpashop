package jpabook.jpashop.service;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateItemDto {
    String author;
    String isbn;
    int price;
    String name;
    int stockQuantity;
}
