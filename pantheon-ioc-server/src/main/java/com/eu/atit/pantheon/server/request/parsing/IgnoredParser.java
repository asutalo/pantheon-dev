package com.eu.atit.pantheon.server.request.parsing;

import java.util.Map;

class IgnoredParser implements Parser {
    @Override
    public void accept(Map<String, Object> stringObjectMap, String o) {
        //doNothing
    }
}
