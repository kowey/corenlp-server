package fr.irit.corenlp.zeromq;

import org.zeromq.ZMQ;

/**
 * Created by kowey on 2014-09-08.
 */
public class ExampleClient {
    public static void main(String[] args) {
        ZMQ.Context context = ZMQ.context(1);

        //  Socket to talk to server
        System.out.println("Connecting to hello world server…");

        ZMQ.Socket requester = context.socket(ZMQ.REQ);
        requester.connect("tcp://localhost:5900");

        for (String string : args) {
            requester.send(string.getBytes(), 0);
            byte[] reply = requester.recv(0);
            System.out.println(new String(reply));
        }
        requester.close();
        context.term();
    }
}
