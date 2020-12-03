package server.service;

import com.google.gson.*;
import server.model.BadResponse;
import server.model.Response;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class DatabaseService {
    static Gson gson = new Gson();

    public static Response get(String key) {
        try (Reader reader = Files.newBufferedReader(Paths.get("/Users/fedyasavchuk/projects/JSON Database/JSON Database/task/src/server/data/db.json"))) {

            JsonArray jsonArray = gson.fromJson(reader, JsonArray.class);
            String[] keys = gson.fromJson(key, String[].class);

            int elemIndex = findIndex(jsonArray, keys[0]);
            if (elemIndex == -1) { return error(); }

            JsonElement jsonElement = findElement(jsonArray.get(elemIndex).getAsJsonObject().get("value"), keys);
            if (jsonElement == null) { return error(); }

            return new Response("OK", jsonElement);
        } catch (Exception e) {
            System.out.println("Problem with db file (GET request)");
        }
        return error();
    }

    public static JsonObject addProperty(String key, JsonElement value) {
        JsonObject jo = new JsonObject();
        jo.add("key", gson.fromJson(key, JsonElement.class));
        jo.add("value", value);
        return jo;
    }

    public static Response set(String key, JsonElement value) {
        try {
            Reader reader = Files.newBufferedReader(Paths.get("/Users/fedyasavchuk/projects/JSON Database/JSON Database/task/src/server/data/db.json"));
            JsonArray jsonArray;
            reader.mark(1);
            if (reader.read() != '[') {
                reader.reset();
                jsonArray = new JsonArray();
                jsonArray.add(addProperty(key, value));
                Writer writer = Files.newBufferedWriter(Paths.get("/Users/fedyasavchuk/projects/JSON Database/JSON Database/task/src/server/data/db.json"));
                gson.toJson(jsonArray, writer);
                writer.close();
                return new Response("OK");
            } else {
                reader.reset();
                jsonArray = gson.fromJson(reader, JsonArray.class);
            }



            if (!key.startsWith("[")) {
                int elemIndex = findIndex(jsonArray, key);
                if (elemIndex == -1) {
                    jsonArray.add(addProperty(key, value));
                } else {
                    jsonArray.get(0).getAsJsonObject().add("value", value);
                }
                Writer writer = Files.newBufferedWriter(Paths.get("/Users/fedyasavchuk/projects/JSON Database/JSON Database/task/src/server/data/db.json"));
                gson.toJson(jsonArray, writer);
                writer.close();
                return new Response("OK");
            }

            String[] keys = gson.fromJson(key, String[].class);
            int elemIndex = findIndex(jsonArray, keys[0]);
            String lastKey = keys[keys.length - 1];
            keys = Arrays.copyOf(keys, keys.length-1);

            JsonElement jsonElement = findElement(jsonArray.get(elemIndex).getAsJsonObject().get("value"), keys);
            if (jsonElement == null) { return error(); }

            jsonElement.getAsJsonObject().add(lastKey, value);
            Writer writer = Files.newBufferedWriter(Paths.get("/Users/fedyasavchuk/projects/JSON Database/JSON Database/task/src/server/data/db.json"));
            gson.toJson(jsonArray, writer);
            writer.close();

            return new Response("OK");
        } catch (Exception e) {
            System.out.println("Problem with db file (SET request)");
        }
        return error();
    }

    public static Response delete(String key) {
        try (Reader reader = Files.newBufferedReader(Paths.get("/Users/fedyasavchuk/projects/JSON Database/JSON Database/task/src/server/data/db.json"))) {
            JsonArray jsonArray = gson.fromJson(reader, JsonArray.class);
            int elemIndex;

            if (!key.startsWith("[")) {
                elemIndex = findIndex(jsonArray, key);
                if (elemIndex == -1) { return error(); }
            }

            String[] keys = gson.fromJson(key, String[].class);
            elemIndex = findIndex(jsonArray, keys[0]);
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



            reader.close();
            Writer writer = Files.newBufferedWriter(Paths.get("/Users/fedyasavchuk/projects/JSON Database/JSON Database/task/src/server/data/db.json"));
            gson.toJson(jsonArray, writer);
            writer.close();

            return new Response("OK");
        } catch (Exception e) {
            System.out.println("Problem with db file (DELETE request)");
        }
        return error();
    }

    public static BadResponse error() {
        return new BadResponse("ERROR");
    }

    public static int findIndex(JsonArray jsonArray, String targetKey) {
        for (int i = 0; i < jsonArray.size(); i++) {
            if (jsonArray.get(i).isJsonObject()
                    && jsonArray.get(i).getAsJsonObject().get("key").getAsString().equals(targetKey)) {
                return i;
            }
        }
        return -1;
    }

    public static JsonElement findElement(JsonElement jsonElement, String[] keys) {
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
