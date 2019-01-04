package cc.snbie.sciot.web;

import cc.snbie.iot.server.SSLServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ScIotApplication {

	public static void main(String[] args) {
		new Thread(new Runnable() {
			public void run() {
				SSLServer sslServer=new SSLServer();
				sslServer.start(5350);
			}
		}).start();
		SpringApplication.run(ScIotApplication.class, args);
	}

}

