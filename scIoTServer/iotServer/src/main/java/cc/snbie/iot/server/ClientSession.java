package cc.snbie.iot.server;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientSession extends Thread{
    final static Logger logger = Logger.getLogger(ClientSession.class);
    private Socket cs;
    private InputStreamReader isr;
    private OutputStreamWriter osw;
    private BufferedReader br;
    private BufferedWriter bw;
    private LinkedBlockingQueue<SessionListener> listeners=new LinkedBlockingQueue<SessionListener>();
    Map<String,Object> dataMap=new HashMap<String, Object>();

    public ClientSession(Socket socket,LinkedBlockingQueue<SessionListener> listeners){
        cs = socket;
        this.listeners=listeners;
        try {
            isr = new InputStreamReader(cs.getInputStream());
            br = new BufferedReader(isr);
            osw = new OutputStreamWriter(cs.getOutputStream());
            bw = new BufferedWriter(osw);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        final ClientSession session=this;
        for(final SessionListener sessionListener : listeners){
            new Thread(new Runnable() {
                public void run() {
                    sessionListener.onConnect(session);
                }
            }).start();
        }
    }

    public void run() {
        try {
            String line = "";
            final ClientSession session=this;
            while ((line = br.readLine()) != null) {
                logger.debug("[" + Global.LOG_TAG + "] Received message:" + line);
                for(final SessionListener sessionListener : listeners){
                    final String finalLine = line;
                    new Thread(new Runnable() {
                        public void run() {
                            sessionListener.onReceived(session, finalLine);
                        }
                    }).start();
                }

            }
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    public Map<String, Object> getDataMap() {
        return dataMap;
    }

    public void setDataMap(Map<String, Object> dataMap) {
        this.dataMap = dataMap;
    }
}
