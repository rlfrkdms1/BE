package com.snowflakes.rednose.controller;

import com.snowflakes.rednose.service.SealLikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.net.URI;

@RequestMapping("/api/v1/seals")
@RestController
@RequiredArgsConstructor
@Slf4j
public class SealLikeController {

    private final SealLikeService sealLikeService;

    @PostMapping("/{sealId}")
    public ResponseEntity<Void> like(Long memberId, @PathVariable Long sealId) {
        sealLikeService.like(sealId, memberId);
        return ResponseEntity.created(URI.create("/api/v1/seals")).build();
    }
}
