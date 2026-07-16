import javax.net.ssl.*;
import java.net.*;

public class TLSTest3 {
    public static void main(String[] args) throws Exception {
        String host = "ac-lwrhjw1-shard-00-00.je6qya4.mongodb.net";
        int port = 27017;

        System.out.println("=== Test: TLS with explicit SNI ===");
        try {
            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket socket = (SSLSocket) factory.createSocket();
            
            // Set SNI explicitly
            SSLParameters sslParams = socket.getSSLParameters();
            sslParams.setServerNames(java.util.List.of(new SNIHostName(host)));
            socket.setSSLParameters(sslParams);
            socket.setEnabledProtocols(new String[]{"TLSv1.2", "TLSv1.3"});
            
            System.out.println("Connecting to " + host + ":" + port);
            socket.connect(new InetSocketAddress(host, port), 15000);
            socket.startHandshake();
            SSLSession session = socket.getSession();
            System.out.println("SUCCESS! Protocol: " + session.getProtocol() + ", Cipher: " + session.getCipherSuite());
            socket.close();
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n=== Test: Can we even reach the host? (plain TCP) ===");
        try {
            Socket plain = new Socket();
            plain.connect(new InetSocketAddress(host, port), 10000);
            System.out.println("TCP connected OK: " + plain.isConnected());
            // Read the first bytes (MongoDB banner)
            byte[] buf = new byte[256];
            plain.setSoTimeout(3000);
            try {
                int read = plain.getInputStream().read(buf);
                System.out.println("Got " + read + " bytes from server (raw TCP)");
                System.out.print("Data: ");
                for (int i = 0; i < Math.min(read, 20); i++) System.out.printf("%02X ", buf[i]);
                System.out.println();
            } catch (Exception re) {
                System.out.println("No immediate data (timeout): " + re.getMessage());
            }
            plain.close();
        } catch (Exception e) {
            System.out.println("TCP failed: " + e.getMessage());
        }
    }
}
