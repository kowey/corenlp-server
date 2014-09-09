package fr.irit.corenlp.protocol;

public class Stop extends Message {
    private static final Stop instance = new Stop();

    public static Stop getInstance() {
        return instance;
    }
}
