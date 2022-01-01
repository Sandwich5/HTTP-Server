package server;


public enum StatusCode {
    OK(200, "OK"),
    NOT_FOUND(404, "NOT FOUND"),
    TEAPOT(418, "I'm a teapot"),
    INTERNAL_SERVER_ERROR(500, "INTERNAL SERVER ERROR"),
    NOT_IMPLEMENTED(501, "NOT IMPLEMENTED"),
    SERVICE_UNAVAILABLE(503, "SERVICE UNAVAILABLE");

    private final Integer statusCode;
    private final String reasonPhrase;

    StatusCode(int status, String str) {
        statusCode = status;
        reasonPhrase = str;
    }

    @Override
    public String toString() {
        return statusCode.toString() + " " + reasonPhrase;
    }
}
