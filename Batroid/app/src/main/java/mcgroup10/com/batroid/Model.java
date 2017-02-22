package mcgroup10.com.batroid;

/**
 * Created by abhishekzambre on 20/11/16.
 */

public class Model {
    String name;
    int value; /* 0 - checkbox disable, 1 - checkbox enable */

    Model(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public int getValue() {
        return this.value;
    }

}