package server.model;

import com.beust.jcommander.Parameter;
import com.google.gson.JsonElement;

public class Request {
    @Parameter(names={"--type", "-t"})
    String type;

    @Parameter(names={"--key", "-k"})
    Object key;

    @Parameter(names={"--value", "-v"})
    JsonElement value;

    public String getType() {
        return type;
    }

    public String getKey() {
        return key.toString();
    }

    public JsonElement getValue() {
        return value;
    }
}
