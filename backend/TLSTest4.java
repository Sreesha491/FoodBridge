import javax.net.ssl.*;
import java.net.*;
import java.security.cert.X509Certificate;

public class TLSTest4 {
    public static void main(String[] args) throws Exception {
        String host = "ac-lwrhjw1-shard-00-00.je6qya4.mongodb.net";
        int port = 27017;

        System.out.println("=== Test: TLS with trust-all (skip cert validation) ===");
        try {
            // Create a trust-all TrustManager
            TrustManager[] trustAll = new TrustManager[] {
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }
            };
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAll, new java.security.SecureRandom());

            SSLSocketFactory factory = sslContext.getSocketFactory();
            SSLSocket socket = (SSLSocket) factory.createSocket();
            SSLParameters sslParams = socket.getSSLParameters();
            sslParams.setServerNames(java.util.List.of(new SNIHostName(host)));
            socket.setSSLParameters(sslParams);
            socket.setEnabledProtocols(new String[]{"TLSv1.2", "TLSv1.3"});

            System.out.println("Connecting to " + host + ":" + port + " with trust-all...");
            socket.connect(new InetSocketAddress(host, port), 15000);
            socket.startHandshake();
            SSLSession session = socket.getSession();
            System.out.println("SUCCESS! Protocol: " + session.getProtocol());
            System.out.println("Cipher: " + session.getCipherSuite());
            System.out.println("Server cert: " + session.getPeerCertificates()[0]);
            socket.close();
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
        }

        System.out.println("\n=== Test: TLS 1.2 only with trust-all ===");
        try {
            TrustManager[] trustAll = new TrustManager[] {
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }
            };
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, trustAll, new java.security.SecureRandom());

            SSLSocketFactory factory = sslContext.getSocketFactory();
            SSLSocket socket = (SSLSocket) factory.createSocket();
            SSLParameters sslParams = socket.getSSLParameters();
            sslParams.setServerNames(java.util.List.of(new SNIHostName(host)));
            socket.setSSLParameters(sslParams);

            socket.connect(new InetSocketAddress(host, port), 15000);
            socket.startHandshake();
            SSLSession session = socket.getSession();
            System.out.println("SUCCESS! Protocol: " + session.getProtocol());
            System.out.println("Cipher: " + session.getCipherSuite());
            socket.close();
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
        }
    }
}
