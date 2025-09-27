package com.eu.atit.pantheon.server.request.parsing;

import java.util.Iterator;
import java.util.Map;

class PathParser implements Parser {
    private final String key;

    public PathParser(String key) {
        this.key = key;
    }

    @Override
    public void accept(Map<String, Object> stringObjectMap, Iterator<String> s) {
        add(stringObjectMap, s.next());
    }

    //for unit tests
    String getKey() {
        return key;
    }

    void add(Map<String, Object> stringObjectMap, String s) {
        stringObjectMap.put(key, s);
    }
}
