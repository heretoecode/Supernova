import com.android.apksig.ApkVerifier;
import java.io.File;
import java.security.MessageDigest;
import java.util.HexFormat;
public class VerifyApk {
 public static void main(String[] args) throws Exception {
  var r = new ApkVerifier.Builder(new File(args[0])).build().verify();
  if (!r.isVerified()) throw new IllegalStateException("Signature invalid: " + r.getErrors());
  System.out.println("Signature verified: " + r.isVerified());
  for (var c : r.getSignerCertificates()) System.out.println("Certificate SHA256: " + HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(c.getEncoded())));
 }
}
