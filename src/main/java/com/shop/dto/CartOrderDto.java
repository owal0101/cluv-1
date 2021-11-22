package com.shop.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CartOrderDto {

    private Long cartItemId;

    // CartOrderDto 클래스가 자기 자신을 List로 가짐
    private List<CartOrderDto> cartOrderDtoList;

}
