package fr.irit.corenlp.protocol;

import lombok.Data;
import lombok.NonNull;

/**
 * A 'process' command
 */
@Data
public class Process extends Message {
    @NonNull private final String text;
}
