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
        serviceFieldProvider = new MySQLServiceFieldsProvider();
    }

    private final Map<TypeLiteral<?>, MySQLService<?>> mySQLServiceMap = new HashMap<>();
    private final Map<TypeLiteral<?>, MySQLModelDescriptor<?>> mySQLModelDescriptorMap = new HashMap<>();
    private final MySQLServiceFieldsProvider serviceFieldProvider;

    @Override
    public MySQLService<?> provide(TypeLiteral<?> servingType) {

        // todo verify if we still need to cache the descriptor
        // originally there was an issue with the descriptors being incomplete if not cached
        // (due to internally using mysql service)
        MySQLModelDescriptor<?> cachedDescriptor = mySQLModelDescriptorMap.get(servingType);

        if (cachedDescriptor == null) {
            cachedDescriptor = mySQLModelDescriptor(servingType);
            mySQLModelDescriptorMap.put(servingType, cachedDescriptor);

            MySQLService<?> mySQLService = new MySQLService<>(dataClient, cachedDescriptor);
            mySQLServiceMap.put(servingType, mySQLService);

            return mySQLService;
        }

        return mySQLServiceMap.get(servingType);
    }

    MySQLModelDescriptor<?> mySQLModelDescriptor(TypeLiteral<?> dataType) {
        return new MySQLModelDescriptor<>(serviceFieldProvider, dataType);
    }

    @Override
    public TypeLiteral<? extends Service> providerFor() {
        return TypeLiteral.get(MySQLService.class);
    }
}
