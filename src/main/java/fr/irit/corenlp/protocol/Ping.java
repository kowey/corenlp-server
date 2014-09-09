package fr.irit.corenlp.protocol;

public class Ping extends Message {
    private static final Ping instance = new Ping();

    public static Ping getInstance() {
        return instance;
    }
}
