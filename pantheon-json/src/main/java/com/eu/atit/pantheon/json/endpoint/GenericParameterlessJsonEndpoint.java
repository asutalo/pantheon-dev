package com.eu.atit.pantheon.json.endpoint;

import com.eu.atit.pantheon.helper.Pair;
import com.eu.atit.pantheon.json.response.JsonResponse;
import com.eu.atit.pantheon.server.response.exception.InternalServerErrorException;
import com.google.inject.TypeLiteral;
import com.sun.net.httpserver.Headers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Basic generic endpoint to provide GET (all), and POST verbs
 */
public class GenericParameterlessJsonEndpoint<T, Q> extends GenericJsonEndpoint<T, Q> {
    private String locationRoot;

    public GenericParameterlessJsonEndpoint(String uriDefinition, String locationRoot, TypeLiteral<T> typeLiteral) {
        super(uriDefinition, typeLiteral);
        setLocationRoot(locationRoot);
    }

    /**
     * Fetches all elements of the specified type
     */
    @Override
    public JsonResponse get(Map<String, Object> uriParams, Map<String, Object> requestBody, Headers requestHeaders) {
        try {
            List<T> instances = service.getAll();

            List<String> formattedStrings = instances.stream().map(this::toString).collect(Collectors.toList());
            String join = String.join(",\n\t", formattedStrings);

            return new JsonResponse(OK, "[\n\t" + join + "\n]");
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
    }

    /**
     * Used to register a new user.
     * Responds with the newly created user ID in the body and the uri to the fully detailed user in the "location" header
     */
    @Override
    public JsonResponse post(Map<String, Object> uriParams, Map<String, Object> requestBody, Headers requestHeaders) {
        T instance = service.instanceOfT(requestBody);

        try {
            service.save(instance);

            Pair<String, String> locationPair = getLocation(instance);

            new HashMap<>();

            return new JsonResponse(CREATED, String.valueOf(withBracers(locationPair.right())), Map.of(LOCATION, List.of(locationRoot + locationPair.left())));

        } catch (Exception e) {
            throw new InternalServerErrorException(); //todo proper exception response code
        }
    }

    private void setLocationRoot(String locationRoot) {
        if (!locationRoot.endsWith("/")) {
            locationRoot += "/";
        }

        this.locationRoot = locationRoot;
    }
}
