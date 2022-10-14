package com.eu.atit.pantheon.server.request.parsing;

import java.util.Map;

class PathParser implements Parser {
    private final String key;

    public PathParser(String key) {
        this.key = key;
    }

    @Override
    public void accept(Map<String, Object> stringObjectMap, String s) {
        stringObjectMap.put(key, s);
    }

    //for unit tests
    String getKey() {
        return key;
    }
}
