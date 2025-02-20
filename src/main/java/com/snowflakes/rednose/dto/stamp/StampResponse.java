package com.snowflakes.rednose.dto.stamp;

import com.snowflakes.rednose.entity.Stamp;
import lombok.Builder;
import lombok.Getter;

@Getter
public class StampResponse {

    private String image;
    private String name;
    private int numberOfLikes;
    private Long id;

    @Builder
    public StampResponse(String image, String name, int numberOfLikes, Long id) {
        this.image = image;
        this.name = name;
        this.numberOfLikes = numberOfLikes;
        this.id = id;
    }

    public static StampResponse of(Stamp stamp, String imageUrl) {
        return StampResponse.builder()
                .image(imageUrl)
                .name(stamp.getName())
                .numberOfLikes(stamp.getNumberOfLikes())
                .id(stamp.getId())
                .build();
    }
}
