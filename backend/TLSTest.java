import javax.net.ssl.*;
import java.net.*;

public class TLSTest {
    public static void main(String[] args) throws Exception {
        System.setProperty("javax.net.debug", "ssl:handshake:verbose");
        String host = "ac-lwrhjw1-shard-00-00.je6qya4.mongodb.net";
        int port = 27017;
        System.out.println("Testing TLS to " + host + ":" + port);
        SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        try (SSLSocket socket = (SSLSocket) factory.createSocket()) {
            socket.setEnabledProtocols(new String[]{"TLSv1.2", "TLSv1.3"});
            socket.connect(new InetSocketAddress(host, port), 15000);
            socket.startHandshake();
            SSLSession session = socket.getSession();
            System.out.println("SUCCESS! Protocol: " + session.getProtocol());
            System.out.println("Cipher: " + session.getCipherSuite());
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
