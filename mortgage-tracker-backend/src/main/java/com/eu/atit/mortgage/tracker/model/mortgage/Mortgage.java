package com.eu.atit.mortgage.tracker.model.mortgage;

import com.eu.atit.mortgage.tracker.model.user.User;

import java.util.List;

//select mortgage.id, mortgage.name, mortgage.amount, mortgage.duration, user.id, user.email, user.password, role.id, role.name, role.permission
public record Mortgage(int id, String name, User user, float amount, int duration,
                       List<MortgageSection> mortgageSections) {
}
