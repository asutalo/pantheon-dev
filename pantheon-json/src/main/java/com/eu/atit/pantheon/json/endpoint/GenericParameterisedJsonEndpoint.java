package com.eu.atit.pantheon.json.endpoint;

import com.eu.atit.pantheon.helper.Pair;
import com.eu.atit.pantheon.json.response.JsonResponse;
import com.eu.atit.pantheon.server.response.exception.InternalServerErrorException;
import com.eu.atit.pantheon.server.response.exception.UnprocessableEntityException;
import com.google.inject.TypeLiteral;
import com.sun.net.httpserver.Headers;

import java.util.Map;

/**
 * Basic generic endpoint to provide GET by param, PUT, and DELETE verbs
 */
public class GenericParameterisedJsonEndpoint<T, Q> extends GenericJsonEndpoint<T, Q> {
    public GenericParameterisedJsonEndpoint(String uriDefinition, TypeLiteral<T> typeLiteral) {
        super(uriDefinition, typeLiteral);
    }

    /**
     * Fetches the element specified in query path
     */
    @Override
    public JsonResponse get(Map<String, Object> uriParams, Map<String, Object> requestBody, Headers requestHeaders) {
        try {
            T instance = service.get(uriParams);

            return new JsonResponse(OK, toString(instance));
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
    }

    /**
     * Deletes the element specified in query path
     */
    @Override
    public JsonResponse delete(Map<String, Object> uriParams, Map<String, Object> requestBody, Headers requestHeaders) {
        try {
            T toDelete = service.get(uriParams);

            service.delete(toDelete);

            Pair<String, String> locationPair = getLocation(toDelete);

            return new JsonResponse(ACCEPTED, String.valueOf(withBracers(locationPair.right())));
        } catch (Exception e) {
            throw new UnprocessableEntityException(); //todo proper exception response code
        }
    }

    /**
     * Updates the element specified in query path
     */
    @Override
    public JsonResponse put(Map<String, Object> uriParams, Map<String, Object> requestBody, Headers requestHeaders) {
        try {
            T toUpdate = service.get(uriParams);

            requestBody.keySet().forEach(key -> {
                if (setters().containsKey(key)) setters().get(key).accept(toUpdate, requestBody.get(key));
            });

            service.update(toUpdate);

            return new JsonResponse(ACCEPTED, toString(toUpdate));

        } catch (Exception e) {
            throw new UnprocessableEntityException(); //todo proper exception response code
        }
    }
}
