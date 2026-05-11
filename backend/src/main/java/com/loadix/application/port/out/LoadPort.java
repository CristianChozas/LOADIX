package com.loadix.application.port.out;

import com.loadix.domain.model.LoadPublication;
import com.loadix.domain.model.PersistedLoadPublication;

public interface LoadPort {

    PersistedLoadPublication save(LoadPublication loadPublication);
}
