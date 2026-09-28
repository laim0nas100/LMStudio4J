package com.github.laim0nas100.lmstudio4j;

import com.github.mizosoft.methanol.MutableRequest;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Supplier;

/**
 *
 * @author laim0nas100
 */
public class APITemplate {
    public static enum RequestType {
        GET, PUT, POST, DELETE, PATCH
    }

    public static class EndPoint {

        public String routeId;
        public final RequestType requestType;
        public final String parametrizedPath;
        public final Supplier<HttpResponse.BodyHandler> bodyHandler;
        public final Supplier<HttpRequest.BodyPublisher> bodyPublisher;

        public EndPoint(RequestType requestType, String path) {
            this(requestType, path, () -> HttpResponse.BodyHandlers.ofString(), () -> null);
        }

        public EndPoint(RequestType requestType, String parametrizedPath, Supplier<HttpResponse.BodyHandler> bodyHandler, Supplier<HttpRequest.BodyPublisher> bodyPublisher) {
            this.requestType = requestType;
            this.parametrizedPath = parametrizedPath;
            this.bodyHandler = bodyHandler;
            this.bodyPublisher = bodyPublisher;
        }

    }

//    public JSONObject jsonRequest(Methanol client, RequestType requestType, String parametrizedUrl, Map<String, Object> params, HttpResponse.BodyHandler handler, HttpRequest.BodyPublisher publisher) throws Exception {
//
//        String replacedURL = new StringSubstitutor(params).replace(parametrizedUrl);
//        URI uri = new URIBuilder(replacedURL).build();
//
//        MutableRequest request = newHttpRequest(requestType, uri, publisher);
//
//        HttpResponse<String> response = client.send(request, handler);
//        if (response.statusCode() >= 400) {
//            throw new PassableException("Failed to make a request, code:" + response.statusCode(), response.body());
//        }
//        String body = response.body();
//       return JSON.parseObject(body);
//
//    }
    
     protected MutableRequest newHttpRequest(RequestType requestType, URI uri, HttpRequest.BodyPublisher publisher) {
        MutableRequest request = MutableRequest.create(uri);
        switch (requestType) {
            case GET:
                request.GET();
                break;

            case PUT:
                request.PUT(publisher);
                break;

            case POST:
                request.POST(publisher);
                break;

            case DELETE:
                request.DELETE();
                break;

            case PATCH:
                request.PATCH(publisher);
                break;
            default:
                throw new AssertionError();
        }

        return request;
    }
}
