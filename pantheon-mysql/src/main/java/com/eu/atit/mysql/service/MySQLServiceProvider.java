package com.eu.atit.mysql.service;

import com.eu.atit.pantheon.client.data.DataClient;
import com.eu.atit.pantheon.service.Service;
import com.eu.atit.pantheon.service.data.DataServiceProvider;
import com.google.inject.TypeLiteral;

import java.util.HashMap;
import java.util.Map;

public class MySQLServiceProvider extends DataServiceProvider {
    public MySQLServiceProvider(DataClient dataClient) {
        super(dataClient);
    }

    private final Map<TypeLiteral<?>, MySQLService<?>> mySQLServiceMap = new HashMap<>();

    @Override
    public MySQLService<?> provide(TypeLiteral<?> servingType) {
        MySQLService<?> cachedService = mySQLServiceMap.get(servingType);
        if (cachedService ==null) {
            cachedService = mySQLService(servingType);
            mySQLServiceMap.put(servingType, cachedService);
            cachedService.init(new MySQLServiceFieldsProvider(this));

        }

        return cachedService;
    }

    MySQLService<?> mySQLService(TypeLiteral<?> dataType) {
        return new MySQLService<>(dataClient, dataType);
    }

    @Override
    public TypeLiteral<? extends Service> providerFor() {
        return TypeLiteral.get(MySQLService.class);
    }
}
