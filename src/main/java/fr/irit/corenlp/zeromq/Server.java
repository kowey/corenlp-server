package fr.irit.corenlp.zeromq;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.StringUtils;
import fr.irit.corenlp.protocol.*;
import fr.irit.corenlp.protocol.Process;
import lombok.NonNull;
import org.zeromq.ZMQ;

/**
 * Crude ZeroMQ server for Stanford CoreNLP.
 * The choice of zeromq is really incidental/arbitrary here.
 * The server accepts strings and emits XML containing their parse.
 *
 * Public domain (Eric Kow)
 */
public class Server extends StanfordCoreNLP {

    static final int DEFAULT_PORT = 5900;
    static private final byte[] PONG = "pong".getBytes();
    static private final byte[] STOPPING = "stopping".getBytes();

    public Server(final Properties props) {
        super(props);
    }

    private static Annotation annotate(final StanfordCoreNLP pipeline,
                                       final String text) {

        // create an empty Annotation just with the given text
        Annotation document = new Annotation(text);
        // run all Annotators on this text
        pipeline.annotate(document);
        return document;
    }

    private static void handleMessage(@NonNull StanfordCoreNLP pipeline,
                                      @NonNull Connection connection,
                                      @NonNull final Message message)
            throws IOException
    {
        if (message instanceof Ping) {
            connection.reply(PONG);
        } else if (message instanceof Stop) {
            connection.reply(STOPPING);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {}
            connection.stop();
        } else if (message instanceof Process) {
            // parse incoming string
            final String incoming = ((Process) message).getText();
            final Annotation document = annotate(pipeline, incoming);
            final ByteArrayOutputStream ostream = new ByteArrayOutputStream();
            pipeline.xmlPrint(document, ostream);
            connection.reply(ostream.toByteArray());
        } else {
            // unreachable if we had a proper language with ADTs
            throw new RuntimeException("Unknown message: " + message);
        }
    }


    private static void runServer(final Properties props,
                                  final int port) throws IOException {
        StanfordCoreNLP pipeline = new Server(props);

        ZMQ.Context context = ZMQ.context(1);

        //  Socket to talk to clients
        ZMQ.Socket responder = context.socket(ZMQ.REP);
        responder.bind("tcp://*:" + port);
        final Connection connection = new Connection(context, responder);

        while (!(Thread.currentThread().isInterrupted()
                || connection.isStopRequested())) {
            // Wait for next request from the client
            byte[] request = responder.recv(0);
            final Message message = Message.fromString(new String(request));
            handleMessage(pipeline, connection, message);
        }
        responder.close();
        context.term();
    }


    public static void main(String[] args) throws IOException {
        Properties props = null;
        int port = DEFAULT_PORT;

        // this was taken directly from edu.stanford.nlp.pipeline.StanfordCoreNLP.main
        if (args.length > 0) {
            props = StringUtils.argsToProperties(args);
            boolean hasH = props.containsKey("h");
            boolean hasHelp = props.containsKey("help");
            if (hasH || hasHelp) {
                String helpValue = hasH ? props.getProperty("h") : props.getProperty("help");
                printHelp(System.err, helpValue);
                return;
            }

            final String portString = props.getProperty("port");
            if (portString != null) {
                port = Integer.parseInt(portString);
            }
        }

        runServer(props, port);
    }
}
