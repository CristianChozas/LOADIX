package com.loadix.application.port.out;

import java.util.List;

public interface AuthTokenPort {

    String createToken(String subject, List<String> roles);
}
