package cc.snbie.iot.server;

public interface SessionListener {
    public void onReceived(ClientSession session,String message);
    public void onConnect(ClientSession session);
    public void onClosed(ClientSession session);
}
