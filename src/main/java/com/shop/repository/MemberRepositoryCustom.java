package com.shop.repository;

import com.shop.dto.MemberSearchDto;
import com.shop.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberRepositoryCustom {

    Page<Member> getAdminMemberPage(MemberSearchDto memberSearchDto, Pageable pageable); // 멤버 포인트 조회  repository
}
