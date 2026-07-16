import javax.net.ssl.*;
import java.net.*;
import java.security.cert.X509Certificate;

public class TLSTest5 {
    public static void main(String[] args) throws Exception {
        String host = "ac-lwrhjw1-shard-00-00.je6qya4.mongodb.net";
        int port = 27017;

        // Try restricting signature algorithms to standard RSA/ECDSA only
        // JDK 25 adds ed25519/ed448 which some servers cannot handle
        System.out.println("=== Test: TLS 1.2 with restricted signature algorithms ===");
        testWithSigAlgs(host, port, 
            new String[]{"TLSv1.2"},
            new String[]{
                "SHA256withRSA", "SHA384withRSA", "SHA512withRSA",
                "SHA256withECDSA", "SHA384withECDSA", "SHA512withECDSA",
                "SHA256withRSAandMGF1"
            });

        System.out.println("\n=== Test: TLS 1.3 with restricted signature algorithms ===");
        testWithSigAlgs(host, port, 
            new String[]{"TLSv1.3"},
            new String[]{
                "SHA256withRSA", "SHA384withRSA", "SHA512withRSA",
                "SHA256withECDSA", "SHA384withECDSA", "SHA512withECDSA",
                "SHA256withRSAandMGF1", "SHA384withRSAandMGF1", "SHA512withRSAandMGF1"
            });
    }

    static void testWithSigAlgs(String host, int port, String[] protocols, String[] sigAlgs) {
        try {
            TrustManager[] trustAll = new TrustManager[] {
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }
            };
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, trustAll, new java.security.SecureRandom());
            SSLSocketFactory factory = ctx.getSocketFactory();
            SSLSocket socket = (SSLSocket) factory.createSocket();

            SSLParameters params = socket.getSSLParameters();
            params.setServerNames(java.util.List.of(new SNIHostName(host)));
            if (sigAlgs != null) params.setSignatureSchemes(sigAlgs);
            socket.setSSLParameters(params);
            socket.setEnabledProtocols(protocols);

            socket.connect(new InetSocketAddress(host, port), 15000);
            socket.startHandshake();
            SSLSession session = socket.getSession();
            System.out.println("SUCCESS! Protocol: " + session.getProtocol() + ", Cipher: " + session.getCipherSuite());
            socket.close();
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
        }
    }
}
