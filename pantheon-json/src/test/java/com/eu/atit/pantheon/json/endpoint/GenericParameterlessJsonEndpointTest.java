package com.eu.atit.pantheon.json.endpoint;

import com.eu.atit.pantheon.helper.Pair;
import com.eu.atit.pantheon.json.TestClass;
import com.eu.atit.pantheon.json.provider.EndpointFieldsProvider;
import com.eu.atit.pantheon.json.provider.EndpointFieldsProviderCache;
import com.eu.atit.pantheon.json.response.JsonResponse;
import com.eu.atit.pantheon.server.response.exception.InternalServerErrorException;
import com.eu.atit.pantheon.service.Service;
import com.eu.atit.pantheon.service.ServiceProvider;
import com.eu.atit.pantheon.service.ServiceProviderRegistry;
import com.eu.atit.pantheon.service.data.DataService;
import com.google.inject.TypeLiteral;
import com.sun.net.httpserver.Headers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static com.eu.atit.pantheon.json.endpoint.GenericJsonEndpoint.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class GenericParameterlessJsonEndpointTest {
    private static final Map<String, Object> EMPTY_MAP = Map.of();
    private static final Map<String, Object> SOME_REQUEST_BODY = Map.of("1", 1);
    private static final String SOME_STRING = "someString";
    private static final String SOME_OTHER_STRING = "someOtherString";
    private static final String SOME_LOCATION = "someLocation";

    @Mock
    private DataService<TestClass, Object> mockDataAccessService;

    @Mock
    private TestClass mockObject;

    private GenericParameterlessJsonEndpoint<TestClass, Object> genericParameterlessJsonEndpoint;

    @Mock
    private EndpointFieldsProvider<Object> mockEndpointFieldsProvider;

    @Mock
    private EndpointFieldsProviderCache mockEndpointFieldsProviderCache;

    private final TypeLiteral<TestClass> mockTypeLiteral = TypeLiteral.get(TestClass.class);

    @BeforeAll
    static void initSetUp() {
        DataService mockDataService = mock(DataService.class);

        ServiceProviderRegistry.INSTANCE().register(new ServiceProvider() {
            @Override
            public Service provide(TypeLiteral<?> servingType) {
                return mockDataService;
            }

            @Override
            public TypeLiteral<? extends Service> providerFor() {
                return TypeLiteral.get(DataService.class);
            }
        });
    }

    @BeforeEach
    void setUp() {
        EndpointFieldsProviderCache.setInstance(mockEndpointFieldsProviderCache);
        when(mockEndpointFieldsProviderCache.endpointFieldsProviderFor(any())).thenReturn(mockEndpointFieldsProvider);

        GenericParameterlessJsonEndpoint<TestClass, Object> genericParameterlessJsonEndpointBase = new GenericParameterlessJsonEndpoint<>("", SOME_LOCATION, mockTypeLiteral);
        genericParameterlessJsonEndpointBase.setService(mockDataAccessService);
        genericParameterlessJsonEndpoint = spy(genericParameterlessJsonEndpointBase);
    }

    @Test
    void get() throws Exception {
        doReturn(SOME_STRING).when(genericParameterlessJsonEndpoint).toString(mockObject);
        when(mockDataAccessService.getAll()).thenReturn(List.of(mockObject, mockObject));

        JsonResponse response = genericParameterlessJsonEndpoint.get(EMPTY_MAP, EMPTY_MAP, mock(Headers.class));

        assertEquals(OK, response.getStatusCode());
        assertEquals(String.format("[\n\t%s,\n\t%s\n]", SOME_STRING, SOME_STRING), response.getMessage());
    }

    @Test
    void post() {
        String someLocation = "location";
        String expectedLocation = SOME_LOCATION + "/" + someLocation;
        Pair<String, String> locationPair = new Pair<>(someLocation, SOME_STRING);

        when(mockDataAccessService.instanceOfT(SOME_REQUEST_BODY)).thenReturn(mockObject);

        doReturn(locationPair).when(genericParameterlessJsonEndpoint).getLocation(mockObject);
        doReturn(SOME_OTHER_STRING).when(genericParameterlessJsonEndpoint).withBracers(SOME_STRING);

        JsonResponse response = genericParameterlessJsonEndpoint.post(EMPTY_MAP, SOME_REQUEST_BODY, mock(Headers.class));

        assertEquals(CREATED, response.getStatusCode());
        assertEquals(SOME_OTHER_STRING, response.getMessage());
        assertEquals(Map.of(LOCATION, List.of(expectedLocation), "Content-Type", List.of("application/json")), response.getHeaders());
    }

    @Test
    void get_throwsExceptionWhenServiceThrows() throws Exception {
        when(mockDataAccessService.getAll()).thenThrow(RuntimeException.class);

        Assertions.assertThrows(InternalServerErrorException.class, () -> genericParameterlessJsonEndpoint.get(EMPTY_MAP, EMPTY_MAP, mock(Headers.class)));
    }

    @Test
    void post_throwsExceptionWhenServiceThrows() throws Exception {
        doThrow(RuntimeException.class).when(mockDataAccessService).save(any());

        Assertions.assertThrows(InternalServerErrorException.class, () -> genericParameterlessJsonEndpoint.post(EMPTY_MAP, SOME_REQUEST_BODY, mock(Headers.class)));
    }
}