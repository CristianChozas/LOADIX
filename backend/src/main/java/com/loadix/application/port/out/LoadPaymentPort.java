package com.loadix.application.port.out;

import java.util.Optional;
import java.util.UUID;

import com.loadix.domain.model.LoadPayment;
import com.loadix.domain.valueobject.PaymentStatus;

public interface LoadPaymentPort {

    LoadPayment save(LoadPayment payment);

    Optional<LoadPayment> findByLoadIdAndCarrierUserIdAndStatus(UUID loadId, UUID carrierUserId, PaymentStatus status);

    long countByCarrierUserIdAndStatus(UUID carrierUserId, PaymentStatus status);
}
