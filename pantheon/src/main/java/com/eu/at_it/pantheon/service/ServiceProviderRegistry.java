package com.eu.at_it.pantheon.service;

import com.eu.at_it.pantheon.exceptions.PantheonProviderException;
import com.google.inject.TypeLiteral;

import java.util.HashMap;
import java.util.Map;

public class ServiceProviderRegistry {
    private static ServiceProviderRegistry INSTANCE;

    private final Map<TypeLiteral<?>, Service> serviceCache = new HashMap<>();
    private final Map<TypeLiteral<? extends Service>, ServiceProvider> serviceProviderCache = new HashMap<>();


    private ServiceProviderRegistry() {
    }

    public static synchronized ServiceProviderRegistry INSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new ServiceProviderRegistry();
        }

        return INSTANCE;
    }

    public Service getService(TypeLiteral<? extends Service> serviceTypeLiteral, TypeLiteral<?> servingType) {
        if (serviceCache.containsKey(servingType)) {
            return serviceCache.get(servingType);
        } else {
            if (serviceProviderCache.containsKey(serviceTypeLiteral)) {
                ServiceProvider serviceProvider = serviceProviderCache.get(serviceTypeLiteral);
                Service service = serviceProvider.provide(servingType);

                serviceCache.put(servingType, service);
                return service;
            } else {
                throw new PantheonProviderException("No provider registered for type: " + serviceTypeLiteral);
            }
        }
    }

    public void register(ServiceProvider serviceProvider) {
        TypeLiteral<? extends Service> providerForType = serviceProvider.providerFor();

        if (serviceProviderCache.containsKey(providerForType)) {
            throw new PantheonProviderException("Provider already registered for type: " + providerForType);
        }

        serviceProviderCache.put(providerForType, serviceProvider);
    }

    Map<TypeLiteral<? extends Service>, ServiceProvider> getServiceProviderCache() {
        return serviceProviderCache;
    }

    //for injecting mocks in tests to simplify instantiation when interacting with ServiceProviderRegistry and avoid reflection
    public static void setInstance(ServiceProviderRegistry instance) {
        INSTANCE = instance;
    }

    //for injecting mocks in tests to simplify instantiation when interacting with ServiceProviderRegistry and avoid reflection
    public static void clearInstance() {
        INSTANCE = null;
    }

    //for injecting mocks in tests to simplify instantiation when interacting with ServiceProviderRegistry and avoid reflection
    public void clearServiceProviderCache() {
        serviceProviderCache.clear();
    }

    //for injecting mocks in tests to simplify instantiation when interacting with ServiceProviderRegistry and avoid reflection
    public void clearServiceCache() {
        serviceCache.clear();
    }
}
