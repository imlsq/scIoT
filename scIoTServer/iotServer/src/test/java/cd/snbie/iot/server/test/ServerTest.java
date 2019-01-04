package cd.snbie.iot.server.test;

import cc.snbie.iot.server.ClientSession;
import cc.snbie.iot.server.SSLServer;
import cc.snbie.iot.server.SessionListener;
import org.junit.Assert;
import org.junit.Test;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

public class ServerTest {
    @Test
    public void helloTest() throws Exception {
        final int port=5350;
        final SSLServer sslServer=new SSLServer();
        new Thread(new Runnable() {
            public void run() {
                sslServer.start(port);
            }
        }).start();
        Thread.sleep(1000);
        Socket client = sslSocketFactory().createSocket("127.0.0.1", port);
        System.out.println("Connected to server " + client.getInetAddress() + ": " + client.getPort());
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
        final String[] ack=new String[]{""};
        sslServer.addListener(new SessionListener() {
            public void onReceived(ClientSession session, String message) {
                ack[0]=message;
            }

            public void onConnect(ClientSession session) {

            }

            public void onClosed(ClientSession session) {

            }
        });
        String message="Hello";
        writer.write(message+"\r\n");
        writer.flush();
        Thread.sleep(1000);
        Assert.assertEquals(message,ack[0]);

        // Close connection
        //client.close();
    }

    private SSLSocketFactory sslSocketFactory() throws Exception {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }};
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new SecureRandom());
        return sc.getSocketFactory();
    }
}
