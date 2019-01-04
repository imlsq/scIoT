package cc.snbie.iot.server;

import cc.snbie.iot.server.util.SysUtil;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.util.Date;
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
    private long lastActivityTime=new Date().getTime();
    private String id= SysUtil.createId();

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
        new Thread(new Runnable() {
            public void run() {
                checkHeartbeat();
            }
        }).start();
        final ClientSession session=this;
        for(final SessionListener sessionListener : listeners){
            new Thread(new Runnable() {
                public void run() {
                    sessionListener.onConnect(session);
                }
            }).start();
        }
    }

    private void checkHeartbeat() {
        int deadTime=30*1000;
        while (true){
            try {
                Thread.sleep(3*1000);
            } catch (InterruptedException e) {

            }
            if(new Date().getTime()-lastActivityTime>deadTime){
                break;
            }
        }
        logger.debug("["+Global.LOG_TAG+"] Session dead,id="+id);
        try {
            br.close();
            bw.close();
            cs.close();
        } catch (IOException e) {

        }
    }

    public void run() {
        try {
            String line = "";
            final ClientSession session=this;
            while ((line = br.readLine()) != null) {
                lastActivityTime=new Date().getTime();
//                if(line.toLowerCase().equals("hb")){
//                    continue;
//                }
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
            logger.error("",e);
        }
    }

    public Map<String, Object> getDataMap() {
        return dataMap;
    }

    public void setDataMap(Map<String, Object> dataMap) {
        this.dataMap = dataMap;
    }
}
