interface PaymentProcessor {
    void processPayment();
}

class CreditCardPaymentProcessor implements PaymentProcessor {

    @Override
    public void processPayment() {
        // Credit card payment transactions
    }
}

class PaypalPaymentProcessor implements PaymentProcessor {

    @Override
    public void processPayment() {
        // Paypal card payment transactions
    }
}


/**
 * Factory Method is a creational design pattern that provides a solution to create objects without the need to specify their concrete classes
 */
class PaymentProcessorFactory {
    private final CreditCardPaymentProcessor creditCardPaymentProcessor;
    private final PaypalPaymentProcessor paypalPaymentProcessor;

    public PaymentProcessorFactory(CreditCardPaymentProcessor creditCardPaymentProcessor, PaypalPaymentProcessor paypalPaymentProcessor) {
        this.creditCardPaymentProcessor = creditCardPaymentProcessor;
        this.paypalPaymentProcessor = paypalPaymentProcessor;
    }

    public PaymentProcessor createPaymentProcessor(String paymentMethod) {
        if (paymentMethod.equalsIgnoreCase("creditcard")) {
           return creditCardPaymentProcessor;
        } else if (paymentMethod.equalsIgnoreCase("paypal")) {
            return paypalPaymentProcessor;
        }
        throw new IllegalArgumentException("Invalid payment method: " + paymentMethod);
    }
}

public class Factory {

}