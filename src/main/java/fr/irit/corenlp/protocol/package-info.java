/**
 * A simple protocol for interacting with the server.
 * Roughly speaking, messages take the form <pre>COMMAND [SP PAYLOAD]</pre>,
 * either:
 *
 * * <pre>{stop, ping}</pre> or
 * * <pre>process SP TEXT</pre>
 *
 * (The text can have newlines in it too)
 */
package fr.irit.corenlp.protocol;