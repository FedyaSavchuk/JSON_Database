package server.model;

import com.google.gson.JsonElement;

public class Response {
    String response;
    JsonElement value;

    public Response(String response) {
        this.response = response;
    }

    public Response(String response, JsonElement value) {
        this.response = response;
        this.value = value;
    }

    public void setValue(JsonElement value) {
        this.value = value;
    }

    public String getResponse() {
        return response;
    }
}
