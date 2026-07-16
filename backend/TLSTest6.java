import javax.net.ssl.*;
import java.net.*;
import java.security.cert.X509Certificate;

public class TLSTest6 {
    public static void main(String[] args) throws Exception {
        String host = "ac-lwrhjw1-shard-00-00.je6qya4.mongodb.net";
        int port = 27017;

        // Standard JVM signature scheme names for SSLParameters.setSignatureSchemes()
        System.out.println("=== Test A: TLS 1.2 + standard ECDSA/RSA sigschemes ===");
        testWithParams(host, port, "TLSv1.2",
            new String[]{
                "ecdsa_secp256r1_sha256", "ecdsa_secp384r1_sha384", "ecdsa_secp521r1_sha512",
                "rsa_pss_rsae_sha256", "rsa_pss_rsae_sha384", "rsa_pss_rsae_sha512",
                "rsa_pkcs1_sha256", "rsa_pkcs1_sha384", "rsa_pkcs1_sha512"
            });

        System.out.println("\n=== Test B: TLS 1.2 only - no sigscheme restriction ===");
        testWithParams(host, port, "TLSv1.2", null);

        System.out.println("\n=== Test C: TLS 1.3 + ECDSA/RSA sigschemes only (no ed25519/ed448) ===");
        testWithParams(host, port, "TLSv1.3",
            new String[]{
                "ecdsa_secp256r1_sha256", "ecdsa_secp384r1_sha384", "ecdsa_secp521r1_sha512",
                "rsa_pss_rsae_sha256", "rsa_pss_rsae_sha384", "rsa_pss_rsae_sha512",
                "rsa_pkcs1_sha256", "rsa_pkcs1_sha384", "rsa_pkcs1_sha512"
            });
    }

    static void testWithParams(String host, int port, String protocol, String[] sigSchemes) {
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
            if (sigSchemes != null) params.setSignatureSchemes(sigSchemes);
            socket.setSSLParameters(params);
            socket.setEnabledProtocols(new String[]{protocol});

            socket.connect(new InetSocketAddress(host, port), 15000);
            socket.startHandshake();
            SSLSession session = socket.getSession();
            System.out.println("SUCCESS! Protocol: " + session.getProtocol() + ", Cipher: " + session.getCipherSuite());
            socket.close();
        } catch (Exception e) {
            System.out.println("FAILED [" + protocol + "]: " + e.getMessage());
        }
    }
}
