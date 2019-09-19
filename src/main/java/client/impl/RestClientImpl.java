package client.impl;

import client.RESTServiceException;
import client.RestClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

public class RestClientImpl implements RestClient {

    private static String defaultUrl = "http://localhost:8082/services/operations";

    @Override
    public Map<String, Object> createPickUpOperation(Map<String, Object> params) throws IOException {
        String url = (String) params.getOrDefault("url", defaultUrl);
        HttpPost post = new HttpPost(url);
        String jsonBody = "";
        HttpEntity httpEntity = new StringEntity(jsonBody, ContentType.APPLICATION_JSON);
        post.setEntity(httpEntity);
        return performRequest(post);
    }

    private Map<String, Object> performRequest(HttpUriRequest request) throws IOException {
        HttpClient client = HttpClients.createDefault();

        HttpResponse response = client.execute(request);
        StatusLine statusLine = response.getStatusLine();
        int responseCode = statusLine.getStatusCode();
        HttpEntity respEntity = response.getEntity();
        String responseBody = null;
        String contentType = null;
        if (respEntity != null) {
            responseBody = EntityUtils.toString(respEntity, Charset.defaultCharset());

            if (respEntity.getContentType() != null) {
                contentType = respEntity.getContentType().getValue();
            }
        }
        if (responseCode >= 200 && responseCode < 300) {
            return postProcessResult(responseBody, contentType);
        } else {
            throw new RESTServiceException(responseCode, responseBody, request.getURI().toString());
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> postProcessResult(String result, String contentType) {
        try {
            if (contentType.toLowerCase().contains("application/json" )) {
                ObjectMapper mapper = new ObjectMapper();

                return mapper.readValue(result, Map.class);
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to transform request to object",
                    e);
        }
        throw new IllegalArgumentException("Unable to find transformer for content type '" + contentType + "' to handle data " + result);
    }

}
