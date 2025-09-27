package com.eu.atit.pantheon.server.request.parsing;

import com.eu.atit.pantheon.server.response.exception.BadRequestException;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.URI;
import java.util.*;

import static java.util.Arrays.asList;

public class ParsingService {
    private static final ParsingService INSTANCE = new ParsingService();
    private static final String QUERY_PARAM_SEPARATOR = "&";
    static final String PATH_SEPARATOR = "/";
    private static final String PATH_AND_QUERY_SEPARATOR = "\\?(?![^(]*\\))";
    public static final String EQUALS = "=";
    public static final String STAR = "*";

    public static ParsingService getInstance() {
        return INSTANCE;
    }

    public List<Parser> getUriParsers(String uriDefinition) {
        List<Parser> parsers = new ArrayList<>();
        String[] pathAndQuery = splitPathAndQuery(uriDefinition);

        Iterator<String> pathIterator = asList(separatedPath(pathAndQuery)).iterator();

        while (pathIterator.hasNext()) {
            String pathPart = pathIterator.next();
            if (isRegexMarker(pathPart)) {
                String[] pathRegexNameAndPattern = stripParenthesis(pathPart).split(EQUALS);
                if (isSplat(pathRegexNameAndPattern[1])) {
                    if (pathIterator.hasNext()) {
                        throw new IllegalArgumentException("Cannot have path parameters after splat (regex pattern of \"*\"");
                    } else {
                        parsers.add(new PathSplatParser(pathRegexFieldName(pathRegexNameAndPattern)));
                    }
                } else {
                    parsers.add(new PathParser(pathRegexFieldName(pathRegexNameAndPattern)));
                }
            } else {
                parsers.add(new IgnoredParser());
            }
        }

        if (hasQueryParams(pathAndQuery)) {
            String[] queryParameters = queryParameters(pathAndQuery);
            for (String queryParam : queryParameters) {
                parsers.add(new QueryParser(queryFieldName(stripParenthesis(queryParam).split(EQUALS))));
            }
        }

        return parsers;
    }

    public String parseUriDefinition(String uriDefinition) {
        StringBuilder parsedUriStringBuilder = new StringBuilder();
        String[] pathAndQuery = splitPathAndQuery(uriDefinition);
        Iterator<String> iterator = getIterator(separatedPath(pathAndQuery));

        if (uriDefinition.equals("") || uriDefinition.equals("/") || uriDefinition.equals("\\/")) {
            return "\\/";
        }

        while (iterator.hasNext()) {
            String pathParam = iterator.next();
            if (isRegexMarker(pathParam)) {
                String pathRegex = pathRegex(stripParenthesis(pathParam));
                if(isSplat(pathRegex)) {
                    parsedUriStringBuilder.append(PathSplatParser.SPLAT_REGEX);
                }
                else {
                    parsedUriStringBuilder.append(pathRegex);
                }
            } else {
                parsedUriStringBuilder.append(pathParam);
            }

            if (iterator.hasNext()) {
                parsedUriStringBuilder.append(PATH_SEPARATOR);
            }
        }

        if (hasQueryParams(pathAndQuery)) {
            parsedUriStringBuilder.append("?");

            String[] queryParams = queryParameters(pathAndQuery);

            iterator = getIterator(queryParams);

            while (iterator.hasNext()) {
                String queryParam = iterator.next();

                String[] queryField = queryParam.split(EQUALS);

                parsedUriStringBuilder.append(queryFieldName(queryField));
                parsedUriStringBuilder.append(EQUALS);

                parsedUriStringBuilder.append(stripParenthesis(queryRegex(queryField)));

                if (iterator.hasNext()) {
                    parsedUriStringBuilder.append(QUERY_PARAM_SEPARATOR);
                }
            }
        }

        return parsedUriStringBuilder.toString();
    }

    public Map<String, Object> parseRequestUri(String decodedUriString, List<Parser> parsers) {
        ArrayList<Parser> internalParsers = new ArrayList<>(parsers);

        String[] pathAndQuery = splitPathAndQuery(decodedUriString);

        Map<String, Object> parsedParams = new HashMap<>();

        if (hasQueryParams(pathAndQuery)) {
            Iterator<String> queryParamsIterator = asList(queryParameters(pathAndQuery)).iterator();
            while (queryParamsIterator.hasNext()) {
                internalParsers.removeLast().accept(parsedParams, queryParamsIterator);
            }
        }

        Iterator<String> pathParamsIterator = asList(separatedPath(pathAndQuery)).iterator();
        for (int i = 0; i < internalParsers.size(); i++) {
            internalParsers.get(i).accept(parsedParams, pathParamsIterator);
        }

        return parsedParams;
    }

    public String decodeUri(HttpExchange httpExchange) {
        URI uri = httpExchange.getRequestURI();

        String uriString = uri.getPath();
        String uriQuery = uri.getQuery();
        if (uriQuery != null) uriString += "?" + uriQuery;
        return uriString;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> parseRequestBody(HttpExchange httpExchange) throws IOException {
        try {
            ObjectMapper mapper = new ObjectMapper();

            byte[] bytes = httpExchange.getRequestBody().readAllBytes();
            int length = bytes.length;
            return length > 0 ? mapper.readValue(bytes, Map.class) : Map.of();
        } catch (JacksonException jacksonException) {
            throw new BadRequestException();
        }
    }

    private String[] queryParameters(String[] pathAndQuerySplit) {
        return pathAndQuerySplit[1].split(QUERY_PARAM_SEPARATOR);
    }

    private boolean isRegexMarker(String pathPart) {
        return pathPart.contains(EQUALS);
    }

    private boolean isSplat(String regexPart) {
        return regexPart.equals(STAR);
    }

    private String[] splitPathAndQuery(String uriDefinition) {
        return uriDefinition.split(PATH_AND_QUERY_SEPARATOR);
    }

    private String pathRegex(String pathParam) {
        return pathParam.split(EQUALS)[1];
    }

    private String queryRegex(String[] queryField) {
        return queryField[1];
    }

    private String pathRegexFieldName(String[] matcher) {
        return matcher[0];
    }

    private String queryFieldName(String[] queryField) {
        return queryField[0];
    }

    private boolean hasQueryParams(String[] pathAndQuery) {
        return pathAndQuery.length > 1;
    }

    private Iterator<String> getIterator(String[] array) {
        return asList(array).iterator();
    }

    private String[] separatedPath(String[] pathAndQuery) {
        return pathAndQuery[0].split(PATH_SEPARATOR);
    }

    private String stripParenthesis(String pathPart) {
        return reverse(removeFirst(reverse(removeFirst(pathPart, "\\(")), "\\)"));
    }

    private String removeFirst(String removeFrom, String remove) {
        return removeFrom.replaceFirst(remove, "");
    }

    private String reverse(String toReverse) {
        return new StringBuilder().append(toReverse).reverse().toString();
    }
}
