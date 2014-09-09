package fr.irit.corenlp.protocol;

import lombok.NonNull;

/**
 * A message consists of a command (ping, process, stop) and its payload
 */
public class Message {
    public static Message fromString(@NonNull final String msgString)
            throws IllegalArgumentException
    {
        if (msgString.equals("stop")) {
            return Stop.getInstance();
        } else if (msgString.equals("ping")) {
            return Ping.getInstance();
        } else if (msgString.startsWith("process ")) {
            return new Process(msgString.substring(8));
        } else {
            throw new IllegalArgumentException("Could not interpret message: " + msgString);
        }
    }
}
