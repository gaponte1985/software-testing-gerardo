package com.amigoscodes.payment.stripe;

import com.amigoscodes.payment.CardPaymentCharge;
import com.amigoscodes.payment.CardPaymentCharger;
import com.amigoscodes.payment.Currency;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
@ConditionalOnProperty(
        value = "stripe.enable",
        havingValue = "false"
)
public class MockStripeService implements CardPaymentCharger {
    @Override
    public CardPaymentCharge chargeCard(
            String cardSource,
            BigDecimal amount,
            Currency currency,
            String description)  {
        return new CardPaymentCharge(true);
    }
}
