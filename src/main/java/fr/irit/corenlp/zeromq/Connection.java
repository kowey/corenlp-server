package fr.irit.corenlp.zeromq;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.zeromq.ZMQ;

@RequiredArgsConstructor
class Connection {
    @NonNull private final ZMQ.Context context;
    @NonNull private final ZMQ.Socket socket;
    @Getter private boolean stopRequested = false;

    public void stop() {
        stopRequested = true;
    }

    public void reply(@NonNull final byte[] msg) {
        socket.send(msg, 0);
    }
}
