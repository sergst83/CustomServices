package client;

import client.impl.RestClientImpl;

import java.io.IOException;
import java.util.Map;

public interface RestClient {
    Map<String, Object> createPickUpOperation(Map<String, Object> params) throws IOException, RESTServiceException;

    Map<String, Object> checkPickUpOperation(Map<String, Object> parameters) throws IOException, RESTServiceException;

    Map<String, Object> createPutWhOperation(Map<String, Object> parameters) throws IOException, RESTServiceException;

    Map<String, Object> checkPutWhOperation(Map<String, Object> parameters) throws IOException, RESTServiceException;
}
