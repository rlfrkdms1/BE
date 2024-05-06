package com.snowflakes.rednose.repository;

import com.snowflakes.rednose.entity.Stamp;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface StampRepository extends JpaRepository<Stamp, Long> {

    Optional<Stamp> findById(Long stampId);

    @Query("select s from Stamp s join StampLike sl on s.id = sl.stamp.id where sl.member.id = :memberId")
    Slice<Stamp> findLikesByMemberId(@Param("memberId") Long memberId);

}
