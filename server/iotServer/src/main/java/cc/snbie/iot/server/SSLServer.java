package cc.snbie.iot.server;

import org.apache.log4j.Logger;
import sun.security.x509.*;

import javax.net.ssl.*;
import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;

public class SSLServer {
    final static Logger logger = Logger.getLogger(SSLServer.class);
    private static final String KEY_PASSWORD = "scIoT_key";
    private LinkedBlockingQueue<SessionListener> listeners=new LinkedBlockingQueue<SessionListener>();
    private int port = 5350;

    /**
     * Generate KeyStore with temporary self signed certificate in memory.
     */
    private static KeyStore genKeyStore() throws NoSuchAlgorithmException, IOException, CertificateException, InvalidKeyException, NoSuchProviderException, SignatureException, KeyStoreException, UnrecoverableKeyException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(1024, new SecureRandom());
        KeyPair keypair = keyGen.generateKeyPair();
        PrivateKey privKey = keypair.getPrivate();
        PublicKey pubKey = keypair.getPublic();

        X509CertInfo info = new X509CertInfo();
        Date from = new Date();
        Date to = new Date(from.getTime() + 100*365 * 86400000l); //100year
        CertificateValidity interval = new CertificateValidity(from, to);
        BigInteger sn = new BigInteger(64, new SecureRandom());
        X500Name owner = new X500Name("cn=test");

        info.set(X509CertInfo.VALIDITY, interval);
        info.set(X509CertInfo.SERIAL_NUMBER, new CertificateSerialNumber(sn));
        try {
            // java 8
            info.set(X509CertInfo.SUBJECT, owner);
            info.set(X509CertInfo.ISSUER, owner);
        } catch (Exception e) {
            info.set(X509CertInfo.SUBJECT, new CertificateSubjectName(owner));
            info.set(X509CertInfo.ISSUER, new CertificateIssuerName(owner));
        }
        info.set(X509CertInfo.KEY, new CertificateX509Key(pubKey));
        info.set(X509CertInfo.VERSION, new CertificateVersion(CertificateVersion.V3));
        AlgorithmId algo = new AlgorithmId(AlgorithmId.sha1WithRSAEncryption_oid);
        info.set(X509CertInfo.ALGORITHM_ID, new CertificateAlgorithmId(algo));
        X509CertImpl cert = new X509CertImpl(info);
        cert.sign(privKey, "SHA1withRSA");
        algo = (AlgorithmId) cert.get(X509CertImpl.SIG_ALG);
        info.set(CertificateAlgorithmId.NAME + "." + CertificateAlgorithmId.ALGORITHM, algo);
        cert = new X509CertImpl(info);
        cert.sign(privKey, "SHA1withRSA");
        Certificate[] certs = new X509CertImpl[1];
        certs[0] = cert;
        KeyStore ks = emptyKeystore();
        ks.setKeyEntry("sslkey", privKey, KEY_PASSWORD.toCharArray(), certs);
        return ks;
    }

    /**
     * Create empty in-memory JKS implementation.
     */
    private static KeyStore emptyKeystore() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(null, null);
        return ks;
    }

    public void start(int port) {
        this.port = port;
        try {
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509", "SunJSSE");
            kmf.init(genKeyStore(), KEY_PASSWORD.toCharArray());
            // empty trust manager
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(emptyKeystore());
            TrustManager[] trustManagers = tmf.getTrustManagers();
            SSLContext ctx = SSLContext.getInstance("TLSv1.2");
            ctx.init(kmf.getKeyManagers(), trustManagers, new SecureRandom());
            SSLServerSocketFactory ssf = ctx.getServerSocketFactory();
            SSLServerSocket server = (SSLServerSocket) ssf.createServerSocket(port);
            logger.info("[" + Global.LOG_TAG + "] SSL Server started on port:" + port);
            while (true) {
                SSLSocket sock = (SSLSocket) server.accept();
                ClientSession session=new ClientSession(sock,listeners);
                logger.info("There's a new client,id="+session.getId());
                new Thread(session).start();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addListener(SessionListener sessionListener){
        listeners.add(sessionListener);
    }

    public void removeListener(SessionListener sessionListener){
        listeners.remove(sessionListener);
    }

    public static void main(String[] args){
        new SSLServer().start(5350);
    }
}
