package cc.snbie.iot.server.listener;

import cc.snbie.iot.server.ClientSession;
import cc.snbie.iot.server.SessionListener;

import java.util.Date;

public class SetMcuTimeListener implements SessionListener {

    public void onReceived(ClientSession session, String message) {

    }

    public void onConnect(ClientSession session) {
        String t="AT+TIME="+new Date().getTime()+"\r\n";
        session.send(t);
    }

    public void onClosed(ClientSession session) {

    }
}
