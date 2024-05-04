package com.snowflakes.rednose.service;

import com.snowflakes.rednose.entity.Member;
import com.snowflakes.rednose.entity.Stamp;
import com.snowflakes.rednose.entity.StampLike;
import com.snowflakes.rednose.fixture.MemberFixture;
import com.snowflakes.rednose.fixture.StampFixture;
import com.snowflakes.rednose.repository.MemberRepository;
import com.snowflakes.rednose.repository.StampLikeRepository;
import com.snowflakes.rednose.repository.StampRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StampLikeServiceTest {

    @Mock private MemberRepository memberRepository;
    @Mock private StampRepository stampRepository;
    @Mock private StampLikeRepository stampLikeRepository;

    @InjectMocks private StampLikeService stampLikeService;

    @Test
    void 좋아요를_누를_수_있다() {
        Member member = MemberFixture.builder().id(1L).build();
        Stamp stamp = StampFixture.builder().id(1L).build();
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(stampRepository.findById(1L)).willReturn(Optional.of(stamp));
        given(stampLikeRepository.existsByMemberIdAndStampId(1L, 1L)).willReturn(false);

        assertAll(
                () -> stampLikeService.like(stamp.getId(), member.getId()),
                () -> verify(memberRepository, times(1)).findById(1L),
                () -> verify(stampRepository, times(1)).findById(1L),
                () -> verify(stampLikeRepository, times(1)).save(any(StampLike.class))
        );
    }

}