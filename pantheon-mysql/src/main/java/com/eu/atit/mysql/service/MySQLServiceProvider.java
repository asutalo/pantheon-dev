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
        serviceFieldProvider = new MySQLServiceFieldsProvider(this);
    }

    private final Map<TypeLiteral<?>, MySQLService<?>> mySQLServiceMap = new HashMap<>();
    private final Map<TypeLiteral<?>, MySQLModelDescriptor<?>> mySQLModelDescriptorMap = new HashMap<>();
    private final MySQLServiceFieldsProvider serviceFieldProvider;

    @Override
    public MySQLService<?> provide(TypeLiteral<?> servingType) {

        MySQLModelDescriptor<?> cachedDescriptor = mySQLModelDescriptorMap.get(servingType);

        if (cachedDescriptor == null) {
            cachedDescriptor = mySQLModelDescriptor(servingType);
            mySQLModelDescriptorMap.put(servingType, cachedDescriptor);
            cachedDescriptor.init();

            MySQLService<?> mySQLService = mySQLService(cachedDescriptor);
            mySQLServiceMap.put(servingType, mySQLService);

            return mySQLService;
        }

        return mySQLServiceMap.get(servingType);
    }

    private MySQLModelDescriptor<?> provideMySqlModelDescriptorNoCache(TypeLiteral<?> servingType) {
        MySQLModelDescriptor<?> cachedDescriptor = mySQLModelDescriptorMap.get(servingType);
        if (cachedDescriptor == null) {
            cachedDescriptor = mySQLModelDescriptor(servingType);
            cachedDescriptor.init();
        }

        return cachedDescriptor;
    }

    MySQLService<?> provideMySqlServiceNoCache(TypeLiteral<?> servingType) {
        MySQLService<?> cachedService = mySQLServiceMap.get(servingType);
        if (cachedService == null) {
            MySQLModelDescriptor<?> modelDescriptor = provideMySqlModelDescriptorNoCache(servingType);

            cachedService = mySQLService(modelDescriptor);
        }

        return cachedService;
    }

    MySQLService<?> mySQLService(MySQLModelDescriptor<?> mySQLModelDescriptor) {
        return new MySQLService<>(dataClient, mySQLModelDescriptor);
    }

    MySQLModelDescriptor<?> mySQLModelDescriptor(TypeLiteral<?> dataType) {
        return new MySQLModelDescriptor<>(serviceFieldProvider, dataType);
    }

    @Override
    public TypeLiteral<? extends Service> providerFor() {
        return TypeLiteral.get(MySQLService.class);
    }
}
