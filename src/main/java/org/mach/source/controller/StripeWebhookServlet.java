package org.mach.source.controller;

import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/webhook")
public class StripeWebhookServlet extends HttpServlet {

    // Set your webhook secret here
    private static final String WEBHOOK_SECRET = "";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        StringBuilder payload = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            payload.append(line);
        }

        String sigHeader = request.getHeader("Stripe-Signature");

        Event event;

        try {
            event = Webhook.constructEvent(payload.toString(), sigHeader, WEBHOOK_SECRET);
        } catch (SignatureVerificationException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Handle the event
        switch (event.getType()) {
            case "payment_intent.succeeded":
                PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject().orElseThrow();
                // Then define and call a method to handle the successful payment intent.
                // handlePaymentIntentSucceeded(paymentIntent);
                break;
            case "payment_intent.payment_failed":
                paymentIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject().orElseThrow();
                // Then define and call a method to handle the failed payment intent.
                // handlePaymentIntentFailed(paymentIntent);
                break;
            // ... handle other event types
            default:
                System.out.println("Unhandled event type: " + event.getType());
                break;
        }

        response.setStatus(HttpServletResponse.SC_OK);
    }
}

