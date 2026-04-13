package com.loadix.infrastructure.http.filter;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.loadix.infrastructure.config.RateLimitProperties;

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
        MockHttpServletResponse secondResponse = new MockHttpServletResponse();

        filter.doFilter(secondRequest, secondResponse, new MockFilterChain());

        assertThat(secondResponse.getStatus()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS.value());
    }
}
