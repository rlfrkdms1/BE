package com.snowflakes.rednose.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;

@Entity
public class SealLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seal_id", nullable = false)
    private Seal seal;

    protected SealLike() {
    }

    @Builder
    private SealLike(Long id, Member member, Seal seal) {
        this.id = id;
        this.member = member;
        this.seal = seal;
    }
}
