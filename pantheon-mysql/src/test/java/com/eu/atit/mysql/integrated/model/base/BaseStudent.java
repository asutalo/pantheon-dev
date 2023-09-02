package com.eu.atit.mysql.integrated.model.base;

public interface BaseStudent extends WithId, WithName {
    BaseDiploma getDiploma();
}
