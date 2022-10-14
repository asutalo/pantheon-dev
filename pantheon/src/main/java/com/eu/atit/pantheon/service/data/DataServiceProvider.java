package com.eu.atit.pantheon.service.data;

import com.eu.atit.pantheon.client.data.DataClient;
import com.eu.atit.pantheon.service.ServiceProvider;

public abstract class DataServiceProvider implements ServiceProvider {
    public final DataClient dataClient;

    public DataServiceProvider(DataClient dataClient) {
        this.dataClient = dataClient;
    }
}
