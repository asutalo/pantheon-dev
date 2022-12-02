package com.eu.atit.mortgage.tracker.model.mortgage;

//select repayment.id, repayment.date, repayment.amount, payment_type.id, payment_type.name from schema.repayment  join payment_type on repayment.payment_type_id=payment_type.id
public record Repayment(int id, String date, float amount, PaymentType paymentType) {
}
