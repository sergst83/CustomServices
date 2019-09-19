package client;

import client.impl.RestClientImpl;

import java.io.IOException;
import java.util.Map;

public interface RestClient {
    Map<String, Object> createPickUpOperation(Map<String, Object> params) throws IOException, RESTServiceException;
}
