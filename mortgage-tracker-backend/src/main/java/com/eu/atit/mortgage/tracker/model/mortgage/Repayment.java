package com.eu.atit.mortgage.tracker.model.mortgage;

public record Repayment(int id, String date, float amount, PaymentType paymentType) {
}
