package com.eu.atit.mortgage.tracker.model.mortgage;

import com.eu.atit.mortgage.tracker.model.user.User;

import java.util.List;

public record Mortgage(int id, String name, User user, float amount, int duration, List<MortgageSection> mortgageSections) {
}
