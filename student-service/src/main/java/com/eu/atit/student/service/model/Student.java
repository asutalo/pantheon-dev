package com.eu.atit.student.service.model;

import java.util.List;

public record Student(int id, String name, Type type, Diploma diploma, List<Course> courses) {
}
