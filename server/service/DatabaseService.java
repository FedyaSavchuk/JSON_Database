package server.service;

import com.google.gson.*;
import server.model.BadResponse;
import server.model.Response;
import server.properties.Options;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class DatabaseService {
    int elemIndex;
    String[] keys;
    JsonArray jsonArray = new JsonArray();
    Gson gson = new Gson();

    public Response executor(String key, JsonElement value, String requestType) {
        try (Reader reader = Files.newBufferedReader(Paths.get(Options.DATABASE_PATH))) {
            JsonElement element = gson.fromJson(reader, JsonElement.class);
            if (!key.startsWith("[")) { key = "[" + key + "]"; }
            if (element.isJsonArray()) {
                jsonArray = element.getAsJsonArray();
            } else {
                jsonArray.add(element);
            }
            keys = gson.fromJson(key, String[].class);
            elemIndex = findIndex(keys[0]);

            Response response;
            switch (requestType) {
                case "get":
                    return get();
                case "set":
                    response = set(key, value);
                    break;
                case "delete":
                    response = delete();
                    break;
                default:
                    return error();
            }

            Writer writer = Files.newBufferedWriter(Paths.get(Options.DATABASE_PATH));
            gson.toJson(jsonArray, writer);
            writer.close();
            return response;
        } catch (Exception e) {
            System.out.println("Problem with db file");
            return error();
        }
    }

    Response get() {
        if (elemIndex == -1) { return error(); }
        JsonElement jsonElement = findElement(jsonArray.get(elemIndex).getAsJsonObject().get("value"), keys);
        if (jsonElement == null) { return error(); }
        return new Response("OK", jsonElement);
    }

    JsonObject addProperty(String key, JsonElement value) {
        JsonObject jo = new JsonObject();
        jo.add("key", gson.fromJson(key, JsonElement.class));
        jo.add("value", value);
        return jo;
    }

    Response set(String key, JsonElement value) {
        if (elemIndex == -1) {
            jsonArray.add(addProperty(key, value));
        } else if (keys.length == 1) {
            jsonArray.get(elemIndex).getAsJsonObject().add("value", value);
        } else {
            String lastKey = keys[keys.length - 1];
            keys = Arrays.copyOf(keys, keys.length-1);
            JsonElement jsonElement = findElement(jsonArray.get(elemIndex).getAsJsonObject().get("value"), keys);
            if (jsonElement == null) { return error(); }
            jsonElement.getAsJsonObject().add(lastKey, value);
        }

        return new Response("OK");
    }

    Response delete() {
        if (elemIndex == -1) { return error(); }

        if (keys.length == 1) {
            jsonArray.remove(elemIndex);
        } else {
            String lastKey = keys[keys.length - 1];
            keys = Arrays.copyOf(keys, keys.length-1);
            JsonElement jsonElement = findElement(jsonArray.get(elemIndex).getAsJsonObject().get("value"), keys);
            if (jsonElement == null) { return error(); }

            jsonElement.getAsJsonObject().remove(lastKey);
        }
        return new Response("OK");
    }

    public BadResponse error() {
        return new BadResponse("ERROR");
    }

    int findIndex(String targetKey) {
        if (jsonArray == null) {
            jsonArray = new JsonArray();
            return -1;
        }
        for (int i = 0; i < jsonArray.size(); i++) {
            if (jsonArray.get(i).isJsonObject()
                    && jsonArray.get(i).getAsJsonObject().get("key").getAsString().equals(targetKey)) {
                return i;
            }
        }
        return -1;
    }

    JsonElement findElement(JsonElement jsonElement, String[] keys) {
        for (int i = 1; i < keys.length; i++) {
            if (jsonElement.isJsonObject() && jsonElement.getAsJsonObject().has(keys[i])) {
                jsonElement = jsonElement.getAsJsonObject().get(keys[i]);
            } else {
                return null;
            }
        }
        return jsonElement;
    }
}
