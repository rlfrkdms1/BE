package com.snowflakes.rednose.support.fixture;

import com.snowflakes.rednose.entity.Stamp;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class StampFixture {

    private final String name = "우표";
    private final String imageUrl = "https://image.com/virtual.png";
    private final LocalDateTime createdAt;
    private final int numberOfLikes;

    @Builder
    public StampFixture(LocalDateTime createdAt, int numberOfLikes) {
        this.createdAt = createdAt;
        this.numberOfLikes = numberOfLikes;
    }

    public Stamp toStamp() {
        return Stamp.builder().name(name).imageUrl(imageUrl).createdAt(createdAt).numberOfLikes(numberOfLikes)
                .build();
    }
}
