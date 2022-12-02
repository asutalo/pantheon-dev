package com.eu.atit.mortgage.tracker.model.mortgage;

import java.util.List;

// select mortgage_section.id, mortgage_section.yearly_payments, mortgage_section.annual_interest_rate, mortgage_section.duration, repayment.id, repayment.date, repayment.amount, payment_type.id, payment_type.name from schema.mortgage_section
// join repayment on repayment.mortgage_section_id = mortgage_section.id
// join payment_type on repayment.payment_type_id=payment_type.id
public record MortgageSection(int id, int yearlyPayments, double annualInterestRate, int duration, List<Repayment> repayments) {
}
