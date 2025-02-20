package com.snowflakes.rednose.service;

import com.snowflakes.rednose.dto.stampcraft.CreateStampRequest;
import com.snowflakes.rednose.dto.stampcraft.CreateStampCraftRequest;
import com.snowflakes.rednose.dto.stampcraft.CreateStampCraftResponse;
import com.snowflakes.rednose.dto.stampcraft.CreateStampResponse;
import com.snowflakes.rednose.dto.stampcraft.EnterStampCraftResponse;
import com.snowflakes.rednose.dto.stampcraft.LeaveStampCraftResponse;
import com.snowflakes.rednose.dto.stampcraft.PaintStampRequest;
import com.snowflakes.rednose.dto.stampcraft.PaintStampResponse;
import com.snowflakes.rednose.dto.stampcraft.ShowCreateStampProgressResponse;
import com.snowflakes.rednose.dto.stampcraft.StartStampNamingResponse;
import com.snowflakes.rednose.entity.Member;
import com.snowflakes.rednose.entity.Stamp;
import com.snowflakes.rednose.entity.StampCraft;
import com.snowflakes.rednose.entity.StampRecord;
import com.snowflakes.rednose.exception.BadRequestException;
import com.snowflakes.rednose.exception.NotFoundException;
import com.snowflakes.rednose.exception.errorcode.MemberErrorCode;
import com.snowflakes.rednose.exception.errorcode.StampCraftErrorCode;
import com.snowflakes.rednose.repository.MemberRepository;
import com.snowflakes.rednose.repository.StampRecordRepository;
import com.snowflakes.rednose.repository.stamp.StampRepository;
import com.snowflakes.rednose.service.auth.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class StampCraftService {

    private final MemberRepository memberRepository;
    private final StampRecordRepository stampRecordRepository;
    private final StampRepository stampRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PreSignedUrlService preSignedUrlService;

    private Long ID = 0L;
    private static final long LIMIT_TIME = 60 * 60l;
    private Map<Long, StampCraft> stampCrafts = new ConcurrentHashMap<>();
    private Map<String, Long> connections = new ConcurrentHashMap<>();

    @Transactional
    public CreateStampCraftResponse create(CreateStampCraftRequest request, Long memberId) {
        Member member = findMemberById(memberId);
        StampCraft stampCraft = makeStampCraft(request, member);
        stampCrafts.put(ID, stampCraft);
        return CreateStampCraftResponse.from(ID++);
    }

    private StampCraft makeStampCraft(CreateStampCraftRequest request, Member member) {
        return StampCraft.builder()
                .host(member)
                .canvasType(request.getCanvasType())
                .createdAt(LocalDateTime.now())
                .build();
    }

    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(MemberErrorCode.NOT_FOUND));
    }

    public PaintStampResponse paint(Long stampCraftId, PaintStampRequest request) {
        validExistStampCraft(stampCraftId);
        StampCraft stampCraft = stampCrafts.get(stampCraftId);
        stampCraft.paint(request.getX(), request.getY(), request.getColor());
        stampCrafts.put(stampCraftId, stampCraft);
        return PaintStampResponse.from(request);
    }

    private void validExistStampCraft(Long stampCraftId) {
        if (!stampCrafts.containsKey(stampCraftId)) {
            throw new NotFoundException(StampCraftErrorCode.NOT_FOUND);
        }
    }

    public EnterStampCraftResponse enter(Long stampCraftId, SimpMessageHeaderAccessor accessor) {
        Long memberId = connections.get(accessor.getSessionId());
        Member member = findMemberById(memberId);
        validExistStampCraft(stampCraftId);
        StampCraft stampCraft = stampCrafts.get(stampCraftId);
        stampCraft.enter(member);
        return EnterStampCraftResponse.from(member);
    }

    public LeaveStampCraftResponse leave(Long stampCraftId, SimpMessageHeaderAccessor accessor) {
        Long memberId = connections.get(accessor.getSessionId());
        Member member = findMemberById(memberId);
        validExistStampCraft(stampCraftId);
        StampCraft stampCraft = stampCrafts.get(stampCraftId);
        stampCraft.quit(member);
        if (!stampCraft.hasMembers()) {
            stampCrafts.remove(stampCraft);
        }
        if (stampCraft.memberIsHost(member)) {
            stampCraft.chooseNewHost();
        }
        return LeaveStampCraftResponse.from(member, stampCraft);
    }

    @Transactional
    public CreateStampResponse done(CreateStampRequest request, Long memberId, Long stampCraftId) {
        Member host = findMemberById(memberId);
        validExistStampCraft(stampCraftId);
        StampCraft stampCraft = stampCrafts.get(stampCraftId);
        validCorrectHost(host, stampCraft);
        Stamp stamp = stampRepository.save(request.toStamp());
        saveStampRecord(stampCraft, stamp);
        stampCrafts.remove(stampCraftId);
        return CreateStampResponse.of(stamp, preSignedUrlService.getPreSignedUrlForShow(stamp.getImageUrl()));
    }

    private void saveStampRecord(StampCraft stampCraft, Stamp stamp) {
        for (Member member : stampCraft.getMembers()) {
            StampRecord stampRecord = StampRecord.builder().stamp(stamp).member(member).build();
            stampRecordRepository.save(stampRecord);
        }
    }

    private void validCorrectHost(Member host, StampCraft stampCraft) {
        if(!stampCraft.memberIsHost(host)) {
            throw new BadRequestException(StampCraftErrorCode.NOT_HOST);
        }
    }

    public void connect(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String accessToken = headerAccessor.getFirstNativeHeader("Authorization");
        connections.put(sessionId, jwtTokenProvider.getMemberId(accessToken));
    }

    public void disconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        connections.remove(sessionId);
    }

    public ShowCreateStampProgressResponse getProgress(Long stampCraftId) {
        validExistStampCraft(stampCraftId);
        StampCraft stampCraft = stampCrafts.get(stampCraftId);
        LocalDateTime createdAt = stampCraft.getCreatedAt();
        long remain = LIMIT_TIME - Duration.between(createdAt, LocalDateTime.now()).getSeconds();
        return ShowCreateStampProgressResponse.of(stampCraft, remain);
    }

    public StartStampNamingResponse startNaming(Long stampCraftId, Long memberId) {
        Member member = findMemberById(memberId);
        validExistStampCraft(stampCraftId);
        StampCraft stampCraft = stampCrafts.get(stampCraftId);
        validCorrectHost(member, stampCraft);
        return new StartStampNamingResponse();
    }
}
