package com.loadix.application.usecase.load;

import java.util.Locale;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.loadix.application.dto.response.LoadResponse;
import com.loadix.application.port.in.ReserveLoadPort;
import com.loadix.application.port.out.LoadPaymentPort;
import com.loadix.application.port.out.LoadPort;
import com.loadix.application.port.out.UserAccountPort;
import com.loadix.domain.exception.UserNotFoundException;
import com.loadix.domain.model.LoadPayment;
import com.loadix.domain.model.LoadPublication;
import com.loadix.domain.model.PersistedLoadPublication;
import com.loadix.domain.model.UserAccount;
import com.loadix.domain.valueobject.LoadStatus;
import com.loadix.domain.valueobject.PaymentProvider;
import com.loadix.domain.valueobject.PaymentStatus;
import com.loadix.domain.valueobject.UserRole;
import com.loadix.infrastructure.config.StripeProperties;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.StripeError;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

public class ReserveLoadUseCase implements ReserveLoadPort {

    private static final Logger log = LoggerFactory.getLogger(ReserveLoadUseCase.class);

    private final UserAccountPort userAccountPort;
    private final LoadPort loadPort;
    private final LoadPaymentPort loadPaymentPort;
    private final StripeProperties stripeProperties;

    public ReserveLoadUseCase(
        UserAccountPort userAccountPort,
        LoadPort loadPort,
        LoadPaymentPort loadPaymentPort,
        StripeProperties stripeProperties
    ) {
        this.userAccountPort = userAccountPort;
        this.loadPort = loadPort;
        this.loadPaymentPort = loadPaymentPort;
        this.stripeProperties = stripeProperties;
    }

    @Override
    public LoadResponse execute(String authenticatedEmail, UUID loadId) {
        UserAccount user = userAccountPort.findByEmail(normalizeEmail(authenticatedEmail))
            .orElseThrow(UserNotFoundException::new);

        if (user.role() != UserRole.CARRIER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only carrier users can reserve loads");
        }

        PersistedLoadPublication existing = loadPort.findById(loadId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Load not found"));

        if (existing.status() != LoadStatus.PUBLISHED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only published loads can be reserved");
        }

        createAndPersistStripePayment(existing, user.id());

        LoadPublication reservedLoad = new LoadPublication(
            existing.warehouseUserId(),
            existing.createdByUserId(),
            existing.originAddress(),
            existing.originCity(),
            existing.originPostalCode(),
            existing.destinationAddress(),
            existing.destinationCity(),
            existing.destinationPostalCode(),
            existing.cargoType(),
            existing.weightKg(),
            existing.loadQuantity(),
            existing.loadUnitType(),
            existing.pickupDate(),
            existing.basePriceAmount(),
            existing.notes(),
            existing.specialRequirements(),
            LoadStatus.RESERVED
        );

        PersistedLoadPublication saved = loadPort.updateById(loadId, reservedLoad);
        return toResponse(saved);
    }

    private void createAndPersistStripePayment(PersistedLoadPublication load, UUID carrierUserId) {
        String stripeSecretKey = stripeProperties.secretKey() == null ? null : stripeProperties.secretKey().trim();

        if (stripeSecretKey == null || stripeSecretKey.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Stripe is not configured");
        }

        validateStripeConfiguration(stripeSecretKey);
        Stripe.apiKey = stripeSecretKey;

        try {
            long amountInMinorUnits = load.basePriceAmount().movePointRight(2).longValueExact();
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountInMinorUnits)
                .setCurrency(stripeProperties.currency().toLowerCase(Locale.ROOT))
                .setPaymentMethod(stripeProperties.defaultTestPaymentMethod())
                .setConfirm(true)
                .setAutomaticPaymentMethods(
                    PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                        .setEnabled(true)
                        .setAllowRedirects(PaymentIntentCreateParams.AutomaticPaymentMethods.AllowRedirects.NEVER)
                        .build()
                )
                .putMetadata("loadId", load.id().toString())
                .putMetadata("carrierUserId", carrierUserId.toString())
                .build();

            PaymentIntent intent = PaymentIntent.create(params);
            PaymentStatus paymentStatus = "succeeded".equals(intent.getStatus()) ? PaymentStatus.CONFIRMED : PaymentStatus.FAILED;

            if (paymentStatus != PaymentStatus.CONFIRMED) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment could not be confirmed");
            }

            LoadPayment payment = new LoadPayment(
                null,
                load.id(),
                carrierUserId,
                PaymentProvider.STRIPE,
                intent.getId(),
                load.basePriceAmount(),
                stripeProperties.currency().toUpperCase(Locale.ROOT),
                paymentStatus,
                null,
                null
            );

            loadPaymentPort.save(payment);
        } catch (ArithmeticException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid price amount for payment", ex);
        } catch (StripeException ex) {
            StripeError stripeError = ex.getStripeError();
            String providerMessage = stripeError != null && stripeError.getMessage() != null
                ? stripeError.getMessage()
                : ex.getMessage();
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Stripe payment failed: " + providerMessage, ex);
        }
    }

    private void validateStripeConfiguration(String stripeSecretKey) {
        String defaultPaymentMethod = stripeProperties.defaultTestPaymentMethod();

        log.info(
            "STRIPE_CONFIG mode={} key_type={} default_payment_method={}",
            stripeMode(stripeSecretKey),
            stripeKeyType(stripeSecretKey),
            defaultPaymentMethod
        );

        if (stripeSecretKey.startsWith("pk_")) {
            throw new ResponseStatusException(
                HttpStatus.BAD_GATEWAY,
                "Stripe is configured with a publishable key. Set STRIPE_SECRET_KEY to a secret key that starts with sk_test_ for local testing."
            );
        }

        if (!stripeSecretKey.startsWith("sk_")) {
            throw new ResponseStatusException(
                HttpStatus.BAD_GATEWAY,
                "Stripe secret key format is invalid. STRIPE_SECRET_KEY must start with sk_test_ for local testing."
            );
        }

        if (stripeSecretKey.startsWith("sk_live_") && defaultPaymentMethod != null && defaultPaymentMethod.startsWith("pm_card_")) {
            throw new ResponseStatusException(
                HttpStatus.BAD_GATEWAY,
                "Stripe is configured with a live secret key but the reservation flow uses a test payment method. Use STRIPE_SECRET_KEY=sk_test_... locally."
            );
        }
    }

    private String stripeMode(String stripeSecretKey) {
        if (stripeSecretKey.startsWith("sk_test_") || stripeSecretKey.startsWith("pk_test_")) {
            return "test";
        }
        if (stripeSecretKey.startsWith("sk_live_") || stripeSecretKey.startsWith("pk_live_")) {
            return "live";
        }
        return "unknown";
    }

    private String stripeKeyType(String stripeSecretKey) {
        if (stripeSecretKey.startsWith("sk_")) {
            return "secret";
        }
        if (stripeSecretKey.startsWith("pk_")) {
            return "publishable";
        }
        return "unknown";
    }

    private LoadResponse toResponse(PersistedLoadPublication saved) {
        return new LoadResponse(
            saved.id(),
            saved.warehouseUserId(),
            saved.createdByUserId(),
            new LoadResponse.LocationResponse(saved.originAddress(), saved.originCity(), saved.originPostalCode()),
            new LoadResponse.LocationResponse(saved.destinationAddress(), saved.destinationCity(), saved.destinationPostalCode()),
            saved.cargoType(),
            saved.weightKg(),
            saved.loadQuantity(),
            saved.loadUnitType(),
            saved.pickupDate(),
            saved.basePriceAmount(),
            saved.notes(),
            saved.specialRequirements(),
            saved.status(),
            saved.createdAt(),
            saved.updatedAt()
        );
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
