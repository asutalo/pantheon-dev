package com.eu.atit.mortgage.tracker.model.mortgage;

import java.util.List;

public record MortgageSection(int id, int yearlyPayments, double annualInterestRate, int duration, List<Repayment> repayments) {
}
