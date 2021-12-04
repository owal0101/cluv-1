package com.shop.dto;

import com.shop.constant.OrderStatus;
import com.shop.entity.Order;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class OrderHistDto {

    public OrderHistDto(Order order){
        this.orderId = order.getId();
        this.orderDate = order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        this.orderStatus = order.getOrderStatus();
        this.usedPoint = order.getUsedPoint(); // 주문 시 사용된 포인트
        this.accPoint = order.getAccPoint(); // 주문 시 적립된 포인트
        this.totalPrice = order.getTotalPrice(); // 최종 결제 금액
    }

    private Long orderId;
    private String orderDate;
    private OrderStatus orderStatus;
    private Integer usedPoint; // 사용 포인트 dto
    private Integer accPoint; // 적립 포인트 dto
    private Integer totalPrice; // 최종 결제 금액

    private List<OrderItemDto> orderItemDtoList = new ArrayList<>();

    //주문 상품리스트
    public void addOrderItemDto(OrderItemDto orderItemDto){
        orderItemDtoList.add(orderItemDto);
    }

}