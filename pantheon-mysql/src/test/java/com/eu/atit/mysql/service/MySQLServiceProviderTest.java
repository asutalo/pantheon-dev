package com.eu.atit.mysql.service;

import com.eu.atit.mysql.client.MySqlClient;
import com.eu.atit.pantheon.service.Service;
import com.google.inject.TypeLiteral;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MySQLServiceProviderTest extends DomainsTestBase {
    private MySQLServiceProvider mySQLServiceProvider;

    @Mock
    private MySqlClient mockMySqlClient;

    @BeforeEach
    void setUp() {
        mySQLServiceProvider = new MySQLServiceProvider(mockMySqlClient);
    }

    @Test
    void provide_shouldCacheInstanceOfMySQLService() {
        TypeLiteral<SimpleTestClass> servingType = TypeLiteral.get(SimpleTestClass.class);

        Service service = mySQLServiceProvider.provide(servingType);

        Assertions.assertNotNull(service);

        Service expectedSameService = mySQLServiceProvider.provide(servingType);

        Assertions.assertEquals(expectedSameService, service);
    }

    @Test
    void providerFor_shouldReturnTypeLiteralOfMySQLService() {
        TypeLiteral<? extends Service> typeLiteral = mySQLServiceProvider.providerFor();

        Assertions.assertNotNull(typeLiteral);
        Assertions.assertEquals(TypeLiteral.get(MySQLService.class), typeLiteral);
    }
}
