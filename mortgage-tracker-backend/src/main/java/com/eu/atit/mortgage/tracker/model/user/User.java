package com.eu.atit.mortgage.tracker.model.user;

//select user.id, user.email, user.password, role.id, role.name, role.permission from schema.user  join role  on user.role_id=role.id
public record User (int id, String email, String password, Role role) {
    public User(int id, String email, String password, Role role) {
        this.email = email;
        this.id = id;
        this.password = encrypt(password);
        this.role = role;
    }

    private String encrypt(String password) {
        throw new RuntimeException("lol encryption?");
    }
}
