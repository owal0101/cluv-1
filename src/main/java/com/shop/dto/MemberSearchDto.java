package com.shop.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberSearchDto {

    private String searchBy;

    private String searchQuery = "";
} // 회원 포인트 및 정보 조회 dto
