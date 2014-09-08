package fr.irit.corenlp.zeromq;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.StringUtils;
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

    private static void runServer(final Properties props,
                                  final int port) throws IOException {
        StanfordCoreNLP pipeline = new Server(props);

        ZMQ.Context context = ZMQ.context(1);

        //  Socket to talk to clients
        ZMQ.Socket responder = context.socket(ZMQ.REP);
        responder.bind("tcp://*:" + port);

        while (!Thread.currentThread().isInterrupted()) {
            // Wait for next request from the client
            byte[] request = responder.recv(0);

            // parse incoming string
            Annotation document = annotate(pipeline, new String(request));
            ByteArrayOutputStream ostream = new ByteArrayOutputStream();
            pipeline.xmlPrint(document, ostream);

            // Send reply back to client
            byte[] reply = ostream.toByteArray();
            responder.send(reply, 0);
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
