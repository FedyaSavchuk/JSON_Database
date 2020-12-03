package server.model;

import server.model.Response;

public class BadResponse extends Response {
    String reason;

    public BadResponse(String response, String reason) {
        super(response);
        this.reason = reason;
    }

    public BadResponse(String response) {
        super(response);
    }

    public String getReason() {
        return reason;
    }
}
