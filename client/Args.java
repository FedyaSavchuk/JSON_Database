package client;

import com.beust.jcommander.Parameter;

public class Args {
    @Parameter(names={"--type", "-t"})
    String type;

    @Parameter(names={"--key", "-k"})
    String key;

    @Parameter(names={"--value", "-v"})
    String value;

    @Parameter(names={"-in"})
    String file;

    public String getFile() {
        return file;
    }

    public String getType() {
        return type;
    }

    public String getKey() {
        return value;
    }

    public String getValue() {
        return key;
    }
}
