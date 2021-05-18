package ws.thirdPartyServer.features.http;

public enum HttpMethodEnums {
    POST("POST"), GET("GET"),;
    private String method;

    HttpMethodEnums(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }

    public static HttpMethodEnums parse(String method) {
        for (HttpMethodEnums methodEnums : values()) {
            if (methodEnums.method.equals(method)) {
                return methodEnums;
            }
        }
        return null;
    }
}
