package server;

public enum RequestType {
    GET,
    POST,
    PUT,
    DELETE;

    public static RequestType fromString(String str) {
        switch (str) {
            case "GET": return GET;
            case "POST": return POST;
            case "PUT": return PUT;
            case "DELETE": return DELETE;
            default: throw new IllegalArgumentException();
        }
    }
}
