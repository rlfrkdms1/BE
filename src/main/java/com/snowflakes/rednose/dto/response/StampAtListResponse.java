package com.snowflakes.rednose.dto.response;

import com.snowflakes.rednose.entity.Stamp;
import lombok.Builder;

@Builder
public class StampAtListResponse {
    private String name;
    private String image;
    private int likes;

    public static StampAtListResponse from(Stamp stamp) {
        return StampAtListResponse.builder().name(stamp.getName())
                .image(stamp.getImageUrl())
                .likes(10)
                .build();
    }
}
