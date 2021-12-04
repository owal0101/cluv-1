package com.shop.service;

import com.shop.dto.OrderDto;
import com.shop.entity.*;
import com.shop.repository.*;
import com.shop.dto.OrderHistDto;
import com.shop.dto.OrderItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import org.thymeleaf.util.StringUtils;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final ItemRepository itemRepository;

    private final MemberRepository memberRepository;

    private final OrderRepository orderRepository;

    private final ItemImgRepository itemImgRepository;

    public Long order(OrderDto orderDto, String email){
        Item item = itemRepository.findById(orderDto.getItemId())
                .orElseThrow(EntityNotFoundException::new);

        Member member = memberRepository.findByEmail(email);

        List<OrderItem> orderItemList = new ArrayList<>();
        OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());

        orderItemList.add(orderItem);

        if(member.getPoint() < orderDto.getUsedPoint()) {
            throw new IllegalStateException("포인트가 부족합니다.");
        } // 사용 포인트 보다 가지고 있는 포인트가 적을 시 경고문 출력

        Order order = Order.createOrder(member, orderDto.getUsedPoint(), orderItemList);

        this.processPointUsage(member, order); // 계산된 포인트 함수 호출

        orderRepository.save(order);
        // 생성한 주문 엔티티 저장

        return order.getId();
    }

    @Transactional(readOnly = true)
    public Page<OrderHistDto> getOrderList(String email, Pageable pageable) {

        List<Order> orders = orderRepository.findOrders(email, pageable);

        Long totalCount = orderRepository.countOrder(email);


        List<OrderHistDto> orderHistDtos = new ArrayList<>();

        for (Order order : orders) {
            OrderHistDto orderHistDto = new OrderHistDto(order);
            List<OrderItem> orderItems = order.getOrderItems();
            for (OrderItem orderItem : orderItems) {
                ItemImg itemImg = itemImgRepository.findByItemIdAndRepimgYn
                        (orderItem.getItem().getId(), "Y"); // 주문한 상품의 대표 이미지
                OrderItemDto orderItemDto =
                        new OrderItemDto(orderItem, itemImg.getImgUrl());
                orderHistDto.addOrderItemDto(orderItemDto);
            }

            orderHistDtos.add(orderHistDto);
        }
        // 주문 리스트를 순회하면서 구매 이력 페이지에 전달할 DTO를 생성

        return new PageImpl<OrderHistDto>(orderHistDtos, pageable, totalCount); // 페이지 구현 객체를 생성
    }

    @Transactional(readOnly = true)
    public boolean validateOrder(Long orderId, String email){
        Member curMember = memberRepository.findByEmail(email);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
        Member savedMember = order.getMember();

        if(!StringUtils.equals(curMember.getEmail(), savedMember.getEmail())){
            return false;
        }

        return true;
    }

    public void cancelOrder(Long orderId){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
        order.cancelOrder();
    }

    public Long orders(String email, Integer usedPoint, List<OrderDto> orderDtoList) {
        Member member = memberRepository.findByEmail(email);
        List<OrderItem> orderItemList = new ArrayList<>();

        for (OrderDto orderDto : orderDtoList) {
            Item item = itemRepository.findById(orderDto.getItemId())
                    .orElseThrow(EntityNotFoundException::new);

            OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
            orderItemList.add(orderItem);
        }

        if(member.getPoint() < usedPoint) {
            throw new IllegalStateException("포인트가 부족합니다.");
        } // 부족 경고문

        Order order = Order.createOrder(member, usedPoint, orderItemList);

        this.processPointUsage(member, order); // 포인트 계산 함수 호출

        orderRepository.save(order);

        return order.getId();
    }

    public void processPointUsage(Member member, Order order) {
        member.setPoint(member.getPoint() - order.getUsedPoint() + order.getAccPoint());

        memberRepository.save(member);
    } // 포인트 계산 함수

}