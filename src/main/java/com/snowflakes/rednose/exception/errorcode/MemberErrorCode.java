package com.snowflakes.rednose.exception.errorcode;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MemberErrorCode implements ErrorCode {

    NOT_FOUND("존재하지 않는 회원입니다"),
    DUPLICATED_NICKNAME("닉네임이 중복됩니다"),
    NULL_NICKNAME("닉네임 값이 NULL입니다");

    private final String message;
}
