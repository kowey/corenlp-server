import zmq
import sys

context = zmq.Context()

#  Socket to talk to server
print("Connecting to server...")
socket = context.socket(zmq.REQ)
socket.connect("tcp://localhost:5900")

for arg in sys.argv[1:]:
    socket.send(arg)
    #  Get the reply.
    message = socket.recv()
    print("Received reply [ %s ]" % message)
