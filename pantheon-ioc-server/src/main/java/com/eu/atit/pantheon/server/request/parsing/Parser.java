package com.eu.atit.pantheon.server.request.parsing;

import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Simple interface to rename BiConsumer into more domain relevant name
 */
public interface Parser extends BiConsumer<Map<String, Object>, String> {

}