package com.eu.at_it.pantheon.service;

import com.google.inject.TypeLiteral;

public interface ServiceProvider {
    Service provide(TypeLiteral<?> servingType);

    TypeLiteral<? extends Service> providerFor();
}
