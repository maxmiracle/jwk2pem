import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.crypto.util.PrivateKeyInfoFactory;
import org.bouncycastle.crypto.util.SubjectPublicKeyInfoFactory;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.json.JSONObject;
import org.shredzone.acme4j.util.KeyPairUtils;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Objects;

public class Main {
    public static void main(String[] args) throws FileNotFoundException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException {
        final Base64.Decoder dec = java.util.Base64.getUrlDecoder();
        final String json = new String(Files.readAllBytes(Paths.get(args[0])), StandardCharsets.UTF_8);
        JSONObject obj = new JSONObject(json);
        var keys = obj.keys();
        final BigInteger e = new BigInteger(1, dec.decode(obj.getString("e")));
        final BigInteger d = new BigInteger(1, dec.decode(obj.getString("d")));
        final BigInteger n = new BigInteger(1, dec.decode(obj.getString("n")));
        final BigInteger q = new BigInteger(1, dec.decode(obj.getString("q")));
        final BigInteger p = new BigInteger(1, dec.decode(obj.getString("p")));
        final BigInteger qi = new BigInteger(1, dec.decode(obj.getString("qi")));
        final BigInteger dp = new BigInteger(1, dec.decode(obj.getString("dp")));
        final BigInteger dq = new BigInteger(1, dec.decode(obj.getString("dq")));
        final KeyFactory kf = KeyFactory.getInstance("RSA");
        final RSAPrivateCrtKey s_key = (RSAPrivateCrtKey) kf.generatePrivate(new RSAPrivateCrtKeySpec(n, e, d, p, q, dp, dq, qi));
        final RSAPublicKey publicKey = (RSAPublicKey) kf.generatePublic(new RSAPublicKeySpec(n, e));
        final RSAPrivateKey privateKey = (RSAPrivateKey) kf.generatePrivate(new RSAPrivateKeySpec(n, d));
        final KeyPair keyPair = new KeyPair(publicKey, privateKey);
        PrivateKeyInfo privateKeyInfo = PrivateKeyInfoFactory.createPrivateKeyInfo(new RSAPrivateCrtKeyParameters(n, e, d, p, q, dp, dq, qi));
        SubjectPublicKeyInfo publicKeyInfo = SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(new RSAKeyParameters(false, n, e));
        PEMKeyPair pemKeyPair = new PEMKeyPair(publicKeyInfo, privateKeyInfo);
        ASN1Primitive primitive = privateKeyInfo.toASN1Primitive();
        File outFile = new File(args[1]);
        FileWriter o = new FileWriter(outFile);
        JcaPEMWriter jcaPEMWriter = new JcaPEMWriter(o);
        jcaPEMWriter.writeObject(privateKeyInfo);
        jcaPEMWriter.close();
        //test(outFile);


        PrivateKeyInfo privateKeyInfo2;
        File domainFile = new File(args[2]);
        try (FileReader fileReader = new FileReader(domainFile)) {
            try (PEMParser parser = new PEMParser(fileReader)) {
                privateKeyInfo2 = (PrivateKeyInfo) parser.readObject();
            } catch (PEMException ex) {
                throw new IOException("Invalid PEM file", ex);
            }
        }
        File outFile2 = new File(args[3]);
        FileWriter o2 = new FileWriter(outFile2);
        JcaPEMWriter jcaPEMWriter2 = new JcaPEMWriter(o2);
        jcaPEMWriter2.writeObject(privateKeyInfo2);
        jcaPEMWriter2.close();

        File domainOutFIle = new File(args[3]);
        //test(domainOutFIle);
    }

    private static void test(File outFile) {
        try (FileReader fileReader = new FileReader(outFile)) {
            KeyPair keyPair = KeyPairUtils.readKeyPair(fileReader);
            Objects.requireNonNull(keyPair);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}