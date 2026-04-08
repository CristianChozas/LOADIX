package com.loadix.infrastructure.http.filter;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.loadix.infrastructure.config.RateLimitProperties;
import com.loadix.infrastructure.http.error.RateLimitExceededException;

class IpRateLimitingFilterTest {

    @Test
    void blocksRequestsOnceCapacityIsExceeded() throws Exception {
        RateLimitProperties properties = new RateLimitProperties(1, Duration.ofMinutes(1), 1, Duration.ofMinutes(15));
        IpRateLimitingFilter filter = new IpRateLimitingFilter(properties);

        MockHttpServletRequest firstRequest = new MockHttpServletRequest("GET", "/api/v1/health");
        firstRequest.setRemoteAddr("127.0.0.1");
        filter.doFilter(firstRequest, new MockHttpServletResponse(), new MockFilterChain());

        MockHttpServletRequest secondRequest = new MockHttpServletRequest("GET", "/api/v1/health");
        secondRequest.setRemoteAddr("127.0.0.1");

        assertThatThrownBy(() -> filter.doFilter(secondRequest, new MockHttpServletResponse(), new MockFilterChain()))
                .isInstanceOf(RateLimitExceededException.class);
    }
}
