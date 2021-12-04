package com.shop.repository;

import com.shop.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface MemberRepository extends JpaRepository<Member, Long>,
        QuerydslPredicateExecutor<Member>, MemberRepositoryCustom {

    Member findByEmail(String email);
}