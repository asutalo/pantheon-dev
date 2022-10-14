package com.eu.at_it.pantheon.service;

import com.eu.at_it.pantheon.exceptions.PantheonProviderException;
import com.eu.at_it.pantheon.service.data.DataServiceProvider;
import com.google.inject.TypeLiteral;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ServiceProviderRegistryTest {
    private static final TypeLiteral<Object> OBJECT_TYPE_LITERAL = TypeLiteral.get(Object.class);
    private static final TypeLiteral<Service> SOME_SERVICE_TYPE_LITERAL = TypeLiteral.get(Service.class);
    private final ServiceProviderRegistry serviceProviderRegistry = ServiceProviderRegistry.INSTANCE();
    @Mock
    private Service mockService;
    private final DataServiceProvider dataServiceProvider = new DataServiceProvider(null) {
        @Override
        public Service provide(TypeLiteral<?> servingType) {
            return mockService;
        }

        @Override
        public TypeLiteral<? extends Service> providerFor() {
            return TypeLiteral.get(Service.class);
        }
    };

    @BeforeEach
    void setUp() {
        serviceProviderRegistry.clearServiceProviderCache();
        serviceProviderRegistry.clearServiceCache();
    }

    @Test
    void isSingleton() throws NoSuchMethodException {
        Assertions.assertEquals(serviceProviderRegistry, ServiceProviderRegistry.INSTANCE());

        Method instanceMethod = ServiceProviderRegistry.class.getDeclaredMethod("INSTANCE");

        int modifiers = instanceMethod.getModifiers();

        Assertions.assertTrue(Modifier.isSynchronized(modifiers));
    }

    @Test
    void setInstance() {
        ServiceProviderRegistry mockServiceProviderRegistry = mock(ServiceProviderRegistry.class);

        ServiceProviderRegistry.setInstance(mockServiceProviderRegistry);

        Assertions.assertNotEquals(serviceProviderRegistry, ServiceProviderRegistry.INSTANCE());

        ServiceProviderRegistry.clearInstance();
    }

    @Test
    void registerProvider_shouldStoreInCache() {
        serviceProviderRegistry.register(dataServiceProvider);

        Map<TypeLiteral<? extends Service>, ServiceProvider> serviceProviderCache = serviceProviderRegistry.getServiceProviderCache();
        Assertions.assertTrue(serviceProviderCache.containsKey(SOME_SERVICE_TYPE_LITERAL));
        Assertions.assertEquals(dataServiceProvider, serviceProviderCache.get(SOME_SERVICE_TYPE_LITERAL));
    }

    @Test
    void registerProvider_shouldThrowExceptionWhenProviderAlreadyRegisteredForType() {
        serviceProviderRegistry.register(dataServiceProvider);

        Assertions.assertThrows(PantheonProviderException.class, () -> serviceProviderRegistry.register(dataServiceProvider));
    }

    @Test
    void getService_shouldProvideService() {
        serviceProviderRegistry.register(dataServiceProvider);

        Service actualService = serviceProviderRegistry.getService(SOME_SERVICE_TYPE_LITERAL, OBJECT_TYPE_LITERAL);

        Assertions.assertEquals(mockService, actualService);
        Assertions.assertEquals(actualService, serviceProviderRegistry.getService(SOME_SERVICE_TYPE_LITERAL, OBJECT_TYPE_LITERAL));
    }

    @Test
    void getService_shouldThrowExceptionWhenProviderNotRegistered() {
        Assertions.assertThrows(PantheonProviderException.class, () -> serviceProviderRegistry.getService(SOME_SERVICE_TYPE_LITERAL, OBJECT_TYPE_LITERAL));
    }
}