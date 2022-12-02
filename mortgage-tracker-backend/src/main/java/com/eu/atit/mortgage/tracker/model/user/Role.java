package com.eu.atit.mortgage.tracker.model.user;

// select role.id, role.name, role.permission from schema.role
public record Role(int id, String name, String permission) {
}
