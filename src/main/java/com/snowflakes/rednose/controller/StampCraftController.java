package com.snowflakes.rednose.controller;

import com.snowflakes.rednose.dto.stamp.CreatePreSignedUrlRequest;
import com.snowflakes.rednose.dto.stamp.CreatePreSignedUrlResponse;
import com.snowflakes.rednose.dto.stampcraft.CreateStampRequest;
import com.snowflakes.rednose.dto.stampcraft.CreateStampCraftRequest;
import com.snowflakes.rednose.dto.stampcraft.CreateStampCraftResponse;
import com.snowflakes.rednose.dto.stampcraft.CreateStampResponse;
import com.snowflakes.rednose.dto.stampcraft.EnterStampCraftResponse;
import com.snowflakes.rednose.dto.stampcraft.LeaveStampCraftResponse;
import com.snowflakes.rednose.dto.stampcraft.PaintStampRequest;
import com.snowflakes.rednose.service.StampCraftService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StampCraftController {

    private final StampCraftService stampCraftService;

    @PostMapping("/api/v1/stamp-craft")
    public CreateStampCraftResponse create(@RequestBody CreateStampCraftRequest request, Long memberId) {
        return stampCraftService.create(request, 1L);
    }

    @EventListener
    public void connectWebSocket(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        log.info("session connected => {}", sessionId);
    }

    @EventListener
    public void disconnectWebSocket(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        log.info("session disconnected => {}", sessionId);
    }

    @MessageMapping("/stamp-craft/{stamp-craft-id}/enter")
    @SendTo("/sub/stamp-craft/{stamp-craft-id}")
    public EnterStampCraftResponse enter(@DestinationVariable("stamp-craft-id") Long stampCraftId) {
        return stampCraftService.enter(stampCraftId, 1L);
    }

    @MessageMapping("/stamp-craft/{stamp-craft-id}/paint")
    @SendTo("/sub/stamp-craft/{stamp-craft-id}")
    public PaintStampRequest paint(@DestinationVariable("stamp-craft-id") Long stampCraftId, @RequestBody PaintStampRequest request) {
        stampCraftService.paint(stampCraftId, request);
        return request;
    }

    @MessageMapping("/stamp-craft/{stamp-craft-id}/leave")
    @SendTo("/sub/stamp-craft/{stamp-craft-id}")
    public LeaveStampCraftResponse leave(@DestinationVariable("stamp-craft-id") Long stampCraftId) {
        return stampCraftService.leave(stampCraftId, 1L);
    }

    @PostMapping("/pre-signed-url")
    public CreatePreSignedUrlResponse getPreSignedUrl(@RequestBody CreatePreSignedUrlRequest request) {
        return stampCraftService.getPreSignedUrl(request);
    }

    @MessageMapping("/stamp-craft/{stamp-craft-id}/done")
    @SendTo("/sub/stamp-craft/{stamp-craft-id}")
    public CreateStampResponse done(@RequestBody CreateStampRequest request, Long memberId, @DestinationVariable("stamp-craft-id") Long stampCraftId) {
        return stampCraftService.done(request, memberId, stampCraftId);
    }
}
