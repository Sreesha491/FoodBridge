import javax.net.ssl.*;
import java.net.*;
import java.security.Security;

public class TLSTest2 {
    public static void main(String[] args) throws Exception {
        // Try restricting signature algorithms to only RSA/ECDSA (no Ed25519/Ed448)
        // which some older TLS implementations can't handle
        String host = "ac-lwrhjw1-shard-00-00.je6qya4.mongodb.net";
        int port = 27017;

        System.out.println("=== Test 1: Default TLS ===");
        testConnection(host, port, null, null);

        System.out.println("\n=== Test 2: TLS 1.2 Only ===");
        testConnection(host, port, new String[]{"TLSv1.2"}, null);

        System.out.println("\n=== Test 3: TLS 1.2 + Explicit ciphers ===");
        testConnection(host, port, new String[]{"TLSv1.2"}, 
            new String[]{
                "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384",
                "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256",
                "TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384",
                "TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256"
            });
    }

    static void testConnection(String host, int port, String[] protocols, String[] ciphers) {
        try {
            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            try (SSLSocket socket = (SSLSocket) factory.createSocket()) {
                if (protocols != null) socket.setEnabledProtocols(protocols);
                if (ciphers != null) socket.setEnabledCipherSuites(ciphers);
                socket.connect(new InetSocketAddress(host, port), 15000);
                socket.startHandshake();
                SSLSession session = socket.getSession();
                System.out.println("SUCCESS! Protocol: " + session.getProtocol() + ", Cipher: " + session.getCipherSuite());
            }
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
        }
    }
}
