package com.eu.at_it.pantheon.service.data;

import com.eu.at_it.pantheon.client.data.DataClient;
import com.eu.at_it.pantheon.service.ServiceProvider;

public abstract class DataServiceProvider implements ServiceProvider {
    public final DataClient dataClient;

    public DataServiceProvider(DataClient dataClient) {
        this.dataClient = dataClient;
    }
}
