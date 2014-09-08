Simple server wrapper for [Stanford CoreNLP][corenlp] parser.

This wrapper was written to help interactive applications that use corenlp.
Our main goal is to avoid the overhead of repeatedly starting corenlp and loading its resources.

We attempt to make the experience of using CoreNLP as close to the offline version as possible
and to be agnostic to whatever other tooling you may have around it:

* work as a drop-in substitute for the usual command line interface (accept the same arguments)
* we do not parse/interpret results from corenlp: instead we just return the XML output

Your job as a user would be to write a client communicating with the server in a supported 
protocol (for now, we've fairly arbitrarily chosen [ZeroMQ][zeromq] as a hopefully simple 
starting point); and to parse the Stanford CoreNLP XML.

[corenlp]: http://nlp.stanford.edu/software/index.shtml
[zeromq]: http://zeromq.org