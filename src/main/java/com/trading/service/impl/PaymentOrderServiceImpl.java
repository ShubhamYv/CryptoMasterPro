package com.trading.service.impl;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.razorpay.Payment;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.trading.domain.PaymentMethod;
import com.trading.domain.PaymentOrderStatus;
import com.trading.modal.PaymentOrder;
import com.trading.modal.User;
import com.trading.pojo.response.PaymentResponse;
import com.trading.repository.PaymentOrderRepository;
import com.trading.service.PaymentOrderService;

@Service
public class PaymentOrderServiceImpl implements PaymentOrderService {

    @Autowired
    private PaymentOrderRepository paymentOrderRepository;

    @Value("${stripe.api.key}")
    private String stripeSecretKey;

    @Value("${razorpay.api.key}")
    private String razorpayApiKey;

    @Value("${razorpay.api.secret}")
    private String razorpaySecretKey;

    @Override
    public PaymentOrder createOrder(User user, Long amount, PaymentMethod paymentMethod) {
        PaymentOrder paymentOrder = new PaymentOrder();
        paymentOrder.setAmount(amount);
        paymentOrder.setUser(user);
        paymentOrder.setPaymentMethod(paymentMethod);
        return paymentOrderRepository.save(paymentOrder);
    }

    @Override
    public PaymentOrder getPaymentOrderById(Long paymentOrderId) throws Exception {
        return paymentOrderRepository.findById(paymentOrderId)
                .orElseThrow(() -> new Exception("Payment Order not found with id::" + paymentOrderId));
    }

    @Override
    public Boolean proceedPaymentOrder(PaymentOrder paymentOrder, String paymentOrderId) throws RazorpayException {
        if (paymentOrder.getStatus().equals(PaymentOrderStatus.PENDING)) {
            if (paymentOrder.getPaymentMethod().equals(PaymentMethod.RAZORPAY)) {
                RazorpayClient razorpay = new RazorpayClient(razorpayApiKey, razorpaySecretKey);
                Payment payment = razorpay.payments.fetch(paymentOrderId);

                Integer amount = payment.get("amount");
                String status = payment.get("status");

                if (status.equals("captured")) {
                    paymentOrder.setStatus(PaymentOrderStatus.SUCCESS);
                    paymentOrderRepository.save(paymentOrder); // Save the updated status
                    return true;
                }

                paymentOrder.setStatus(PaymentOrderStatus.FAILED);
                paymentOrderRepository.save(paymentOrder); // Save the updated status
                return false;
            }
        }
        return false;
    }

    @Override
    public PaymentResponse createRazorpayPaymentLink(User user, Long amount) throws RazorpayException {
        amount = amount * 100;

        try {
            RazorpayClient razorpay = new RazorpayClient(razorpayApiKey, razorpaySecretKey);
            JSONObject paymentLinkReq = new JSONObject();
            paymentLinkReq.put("amount", amount);
            paymentLinkReq.put("currency", "INR");

            JSONObject customer = new JSONObject();
            customer.put("name", user.getFullName());
            customer.put("email", user.getEmail());

            paymentLinkReq.put("customer", customer);

            JSONObject notify = new JSONObject();
            notify.put("email", true);

            paymentLinkReq.put("notify", notify);
            paymentLinkReq.put("reminder_enable", true);
            paymentLinkReq.put("callback_url", "http://localhost:5173/wallet");
            paymentLinkReq.put("callback_method", "get");

            PaymentLink paymentLink = razorpay.paymentLink.create(paymentLinkReq);
            String paymentLinkId = paymentLink.get("id");
            String paymentLinkUrl = paymentLink.get("short_url");

            PaymentResponse response = new PaymentResponse();
            response.setPayment_url(paymentLinkUrl);

            return response;
        } catch (RazorpayException e) {
            System.out.println("Razorpay||Error creating payment link:" + e.getMessage());
            throw new RazorpayException(e.getMessage());
        }
    }

    @Override
    public PaymentResponse createStripePaymentLink(User user, Long amount, Long orderId) {
        Stripe.apiKey = stripeSecretKey;

        try {
            SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:3000/wallet?order_id=" + orderId)
                .setCancelUrl("http://localhost:3000/payment/cancel")
                .addLineItem(
                    SessionCreateParams.LineItem.builder()
                        .setPriceData(
                            SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("usd")
                                .setUnitAmount(amount * 100) // amount in cents
                                .setProductData(
                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName("Top up wallet")
                                        .build())
                                .build())
                        .setQuantity(1L)
                        .build())
                .build();

            Session session = Session.create(params);
            System.out.println("Stripe||Session:" + session);
            PaymentResponse paymentResponse = new PaymentResponse();
            paymentResponse.setPayment_url(session.getUrl());

            return paymentResponse;
        } catch (StripeException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to create Stripe payment link", e);
        }
    }
}
