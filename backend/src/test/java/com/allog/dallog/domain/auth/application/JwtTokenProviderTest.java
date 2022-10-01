package com.allog.dallog.domain.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.allog.dallog.domain.auth.exception.InvalidTokenException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JwtTokenProviderTest {

    private static final String JWT_SECRET_KEY = "A".repeat(32); // Secret Key는 최소 32바이트 이상이어야함.
    private static final int JWT_ACCESS_TOKEN_EXPIRE_LENGTH = 3600;
    private static final int JWT_REFRESH_TOKEN_EXPIRE_LENGTH = 3600;
    private static final String PAYLOAD = "payload";

    private final JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(JWT_SECRET_KEY,
            JWT_ACCESS_TOKEN_EXPIRE_LENGTH, JWT_REFRESH_TOKEN_EXPIRE_LENGTH);

    @DisplayName("JWT 토큰을 생성한다.")
    @Test
    void JWT_토큰을_생성한다() {
        // given & when
        String actual = jwtTokenProvider.createAccessToken(PAYLOAD);

        // then
        assertThat(actual.split("\\.")).hasSize(3);
    }

    @DisplayName("JWT 토큰의 Payload를 가져온다.")
    @Test
    void JWT_토큰의_Payload를_가져온다() {
        // given
        String token = jwtTokenProvider.createAccessToken(PAYLOAD);

        // when
        String actual = jwtTokenProvider.getPayload(token);

        // then
        assertThat(actual).isEqualTo(PAYLOAD);
    }

    @DisplayName("validateToken 메서드는 만료된 엑세스 토큰을 전달하면 예외를 던진다.")
    @Test
    void validateToken_메서드는_만료된_엑세스_토큰을_전달하면_예외를_던진다() {
        // given
        TokenProvider expiredJwtTokenProvider = new JwtTokenProvider(JWT_SECRET_KEY, 0, 0);
        String expiredToken = expiredJwtTokenProvider.createAccessToken(PAYLOAD);

        // when & then
        assertThatThrownBy(() -> jwtTokenProvider.validateToken(expiredToken))
                .isInstanceOf(InvalidTokenException.class);
    }

    @DisplayName("validateToken 메서드는 만료된 리프레시 토큰을 전달하면 예외를 던진다.")
    @Test
    void validateToken_메서드는_만료된_리프레시_토큰을_전달하면_예외를_던진다() {
        // given
        TokenProvider expiredJwtTokenProvider = new JwtTokenProvider(JWT_SECRET_KEY, 0, 0);
        String expiredToken = expiredJwtTokenProvider.createRefreshToken(PAYLOAD);

        // when & then
        assertThatThrownBy(() -> jwtTokenProvider.validateToken(expiredToken))
                .isInstanceOf(InvalidTokenException.class);
    }

    @DisplayName("validateToken 메서드는 유효하지 않은 토큰을 전달하면 예외를 던진다.")
    @Test
    void validateToken_메서드는_유효하지_않은_토큰을_전달하면_예외를_던진다() {
        // given
        String malformedToken = "malformed";

        // when & then
        assertThatThrownBy(() -> jwtTokenProvider.validateToken(malformedToken))
                .isInstanceOf(InvalidTokenException.class);
    }
}
