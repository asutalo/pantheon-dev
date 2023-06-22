package com.eu.atit.mysql.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class InstantiatorTest extends DomainsTestBase {
    @Test
    void get_shouldGenerateAnInstanceFromDefaultConstructor() throws NoSuchMethodException {
        SimpleTestClass actual = new Instantiator<>(getTestClassDefaultConstructor()).get();

        Assertions.assertNotNull(actual);
    }

    @Test
    void get_shouldThrowExceptionWhenInstantiationFails() {
        Assertions.assertThrows(RuntimeException.class, () -> new Instantiator<>(getDoomedToFailDefaultConstructor()).get());
    }
}