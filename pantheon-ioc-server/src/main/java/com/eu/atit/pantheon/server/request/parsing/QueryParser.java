package com.eu.atit.pantheon.server.request.parsing;

import java.util.Iterator;
import java.util.Map;

class QueryParser implements Parser {
    private final String key;

    public QueryParser(String key) {
        this.key = key;
    }

    @Override
    public void accept(Map<String, Object> stringObjectMap, Iterator<String> s) {
        String value = s.next().split("=")[1];
        stringObjectMap.put(key, value);
    }

    //for tests
    String getKey() {
        return key;
    }
}
