package com.kdx.core.net;


public interface CommunicationHandler {
    void msgArrived(SocketCommunication socketCommunication,String msg);
    void connected(SocketCommunication socketCommunication);
    void connectClosed();
}
