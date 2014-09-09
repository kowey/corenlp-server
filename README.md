Simple server wrapper for [Stanford CoreNLP][corenlp] parser.

This wrapper was written to help interactive applications that use
corenlp.  Our main goal is to avoid the overhead of repeatedly starting
corenlp and loading its resources.

We attempt to make the experience of using CoreNLP as close to the
offline version as possible and to be agnostic to whatever other tooling
you may have around it:

* work as a drop-in substitute for the usual command line interface
  (accept the same arguments)
* we do not parse/interpret results from corenlp: instead we just return
  the XML output

Your job as a user would be to write a client communicating with the
server in a supported protocol (for now, we've fairly arbitrarily chosen
[ZeroMQ][zeromq] as a hopefully simple starting point); and to parse the
Stanford CoreNLP XML.

## Getting started

This package uses Maven for as its build/dependency system.

    mvn package

To run the server and its example client through maven

    mvn exec:java -D server
    mvn exec:java -D client\
        -Dexec.args="ping 'process why hello there' ping stop'"

Otherwise you're on your own for working out the combination of jars
(stanford corenlp, jeromq, this server).

Note also the Python example client:

    cd src/main/python
    pip install pyzmq
    python client.py 'process it may work in Python too'


## Protocol

Note that there is a simple communication protocol between server and
client (on top of what lower-level protocol or layer you may be using).

For now it consists of one word command followed by optional single
space and payload.

Commands:
* stop
* ping
* process SP text...

If the server becomes more complicated than this, we may need to switch
to some sort more formal language

### Ping

The response will just be the word "pong"

### Process

The response will be CoreNLP XML

## License (GPL v2+)

I'm happy for this to public domain in principle; however, I think I
use snippets of corenlp in places so we'll likely have to go with
GPL v2 or later.


[corenlp]: http://nlp.stanford.edu/software/index.shtml
[zeromq]: http://zeromq.org
