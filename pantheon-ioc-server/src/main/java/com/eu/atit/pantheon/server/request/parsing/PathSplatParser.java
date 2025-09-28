package com.eu.atit.pantheon.server.request.parsing;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.eu.atit.pantheon.server.request.parsing.ParsingService.PATH_SEPARATOR;

// decorator for PathParser that marks splat parsing
class PathSplatParser extends PathParser {
    public static final String SPLAT_REGEX = "(?![.])"                  // not starting with dot
            + "(?:[a-zA-Z0-9_-]+/)*"     // zero or more path segments (letters, numbers, _, -, no dot)
            + "[a-zA-Z0-9_-]+"           // filename part (no dot yet, can't be empty)
            + "\\.[a-zA-Z0-9_-]+"        // extension required: must include single dot in last segment
            + "(?=[\\?]|$)";             // must be followed by ? or end of string
//            + "(?=[/?]|$)";              // must be followed by /, ? or end of string

    public PathSplatParser(String key) {
        super(key);
    }

    @Override
    public void accept(Map<String, Object> stringObjectMap, Iterator<String> s) {
        StringBuilder sb = new StringBuilder();
        sb.append(s.next());
        while (s.hasNext()) {
            sb.append(PATH_SEPARATOR);
            sb.append(s.next());
        }
        add(stringObjectMap, sb.toString());
    }
}
