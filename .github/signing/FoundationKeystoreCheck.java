import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.Security;
import java.security.Signature;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HexFormat;

/** Read-only PKCS#12 check. No key creation, export, conversion or APK signing. */
class FoundationKeystoreCheck {
    public static void main(String[] args) {
        boolean failed = false;
        char[] storePassword = null;
        char[] keyPassword = null;
        try {
            if (args.length != 2) throw new IllegalArgumentException();
            storePassword = System.getenv("SUPERNOVA_STORE_PASSWORD").toCharArray();
            // Disable JDK's legacy JKS auto-detection: Foundation accepts PKCS#12 only.
            Security.setProperty("keystore.type.compat", "false");
            KeyStore store = KeyStore.getInstance("PKCS12");
            try (InputStream input = Files.newInputStream(Path.of(args[1]))) {
                store.load(input, storePassword);
            }
            ArrayList<String> aliases = new ArrayList<>();
            for (String alias : Collections.list(store.aliases())) {
                if (store.entryInstanceOf(alias, KeyStore.PrivateKeyEntry.class)) aliases.add(alias);
            }
            Collections.sort(aliases);
            if (aliases.isEmpty()) throw new IllegalArgumentException();
            if (args[0].equals("inspect")) {
                // Aliases and certificate hashes are public metadata, never key bytes.
                for (String alias : aliases) {
                    X509Certificate cert = (X509Certificate) store.getCertificate(alias);
                    System.out.println(Base64.getEncoder().encodeToString(alias.getBytes(java.nio.charset.StandardCharsets.UTF_8))
                        + " " + fingerprint(cert));
                }
            } else if (args[0].equals("validate")) {
                String alias = System.getenv("SUPERNOVA_KEY_ALIAS");
                if (!store.entryInstanceOf(alias, KeyStore.PrivateKeyEntry.class)) throw new IllegalArgumentException();
                keyPassword = System.getenv("SUPERNOVA_KEY_PASSWORD").toCharArray();
                PrivateKey key = (PrivateKey) store.getKey(alias, keyPassword);
                X509Certificate cert = (X509Certificate) store.getCertificate(alias);
                cert.checkValidity();
                String algorithm = switch (key.getAlgorithm()) {
                    case "RSA" -> "SHA256withRSA";
                    case "EC" -> "SHA256withECDSA";
                    case "DSA" -> "SHA256withDSA";
                    default -> throw new IllegalArgumentException();
                };
                // Prove password unlocks the private key and it matches the certificate.
                // This in-memory challenge is not an APK or a replacement identity.
                byte[] challenge = "Supernova Foundation keystore verification".getBytes(java.nio.charset.StandardCharsets.UTF_8);
                Signature signer = Signature.getInstance(algorithm);
                signer.initSign(key); signer.update(challenge);
                byte[] proof = signer.sign();
                signer.initVerify(cert.getPublicKey()); signer.update(challenge);
                if (!signer.verify(proof)) throw new IllegalArgumentException();
                System.out.println(fingerprint(cert));
            } else throw new IllegalArgumentException();
        } catch (Exception failure) {
            // Exception messages may contain aliases/paths/provider diagnostics: suppress.
            System.err.println("Keystore verification failed. Check PKCS#12 format, alias and passwords privately.");
            failed = true;
        } finally {
            if (storePassword != null) Arrays.fill(storePassword, '\0');
            if (keyPassword != null) Arrays.fill(keyPassword, '\0');
        }
        if (failed) System.exit(1);
    }
    private static String fingerprint(X509Certificate certificate) throws Exception {
        return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(certificate.getEncoded()));
    }
}
