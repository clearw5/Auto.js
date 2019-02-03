package org.autojs.autojs.build;

import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.security.DigestOutputStream;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class TinySign {
    public TinySign() {
    }

    private static byte[] dBase64(String data) throws UnsupportedEncodingException {
        return Base64.decode(data.getBytes("UTF-8"), Base64.NO_WRAP);
    }

    private static void doDir(String prefix, File dir, ZipOutputStream zos, DigestOutputStream dos, Manifest m) throws IOException {
        zos.putNextEntry(new ZipEntry(prefix));
        zos.closeEntry();
        File[] arr$ = dir.listFiles();
        int len$ = arr$.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            File f = arr$[i$];
            if (f.isFile()) {
                doFile(prefix + f.getName(), f, zos, dos, m);
            } else {
                doDir(prefix + f.getName() + "/", f, zos, dos, m);
            }
        }

    }

    private static void doFile(String name, File f, ZipOutputStream zos, DigestOutputStream dos, Manifest m) throws IOException {
        zos.putNextEntry(new ZipEntry(name));
        FileInputStream fis = FileUtils.openInputStream(f);
        IOUtils.copy(fis, dos);
        IOUtils.closeQuietly(fis);
        byte[] digets = dos.getMessageDigest().digest();
        zos.closeEntry();
        Attributes attr = new Attributes();
        attr.putValue("SHA1-Digest", eBase64(digets));
        m.getEntries().put(name, attr);
    }

    private static String eBase64(byte[] data) {
        return Base64.encodeToString(data, Base64.NO_WRAP);
    }

    private static Manifest generateSF(Manifest manifest) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA1");
        PrintStream print = new PrintStream(new DigestOutputStream(new OutputStream() {
            public void write(byte[] arg0) {
            }

            public void write(byte[] arg0, int arg1, int arg2) {
            }

            public void write(int arg0) {
            }
        }, md), true, "UTF-8");
        Manifest sf = new Manifest();
        Map<String, Attributes> entries = manifest.getEntries();
        Iterator<Entry<String, Attributes>> iterator = entries.entrySet().iterator();

        while(iterator.hasNext()) {
            Entry<String, Attributes> entry = iterator.next();
            print.print("Name: " + entry.getKey() + "\r\n");
            Iterator<Entry<Object, Object>> iter = entry.getValue().entrySet().iterator();

            while(iter.hasNext()) {
                Entry<Object, Object> att = iter.next();
                print.print(att.getKey() + ": " + att.getValue() + "\r\n");
            }

            print.print("\r\n");
            print.flush();
            Attributes sfAttr = new Attributes();
            sfAttr.putValue("SHA1-Digest", eBase64(md.digest()));
            sf.getEntries().put(entry.getKey(), sfAttr);
        }

        return sf;
    }

    private static Signature instanceSignature() throws Exception {
        byte[] data = dBase64("MIIBVgIBADANBgkqhkiG9w0BAQEFAASCAUAwggE8AgEAAkEAoiZSqWnFDHA5sXKoDiUUO9JuL7cm/2dCck5MKumVvv+WfSg0jsovnywsFN0pifmdRSLmOdUkh0d0J+tOnSgtsQIDAQABAkEAihag5u3Qhds9BsViIUmqhZebhr8vUuqZR8cuTo1GnbSoOHIPbAgD3J8TDbC/CVqae8NrgwLp325Pem1Tuof/0QIhAN1hqft1K307bsljgw3iYKopGVZBHRXsjRnNL4edV9QrAiEAu4F+XtS1wohGLz5QtfuMFsQNo4l31mCjt6WpBDmSi5MCIQCB++YijxmJ3mueM5+vd0vqnVcTHghF5y6yB5fwuKHpIQIgInnS1Hjj2prX3MPmby+LOHxfzZvvDtnCAHhTNVWonkUCIQCvV8l+SpL6Vh1nQ/2EKFJo2dbZB3wKG/BEYsFkPFbn9w==");
        KeyFactory rSAKeyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = rSAKeyFactory.generatePrivate(new PKCS8EncodedKeySpec(data));
        Signature signature = Signature.getInstance("SHA1withRSA");
        signature.initSign(privateKey);
        return signature;
    }

    public static void sign(File dir, OutputStream out) throws Exception {
        ZipOutputStream zos = new ZipOutputStream(out);
        zos.putNextEntry(new ZipEntry("META-INF/"));
        zos.closeEntry();
        Manifest manifest = new Manifest();
        String sha1Manifest = writeMF(dir, manifest, zos);
        Manifest sf = generateSF(manifest);
        byte[] sign = writeSF(zos, sf, sha1Manifest);
        writeRSA(zos, sign);
        IOUtils.closeQuietly(zos);
    }

    private static String writeMF(File dir, Manifest manifest, ZipOutputStream zos) throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance("SHA1");
        DigestOutputStream dos = new DigestOutputStream(zos, md);
        zipAndSha1(dir, zos, dos, manifest);
        Attributes main = manifest.getMainAttributes();
        main.putValue("Manifest-Version", "1.0");
        main.putValue("Created-By", "Auto.js");
        zos.putNextEntry(new ZipEntry("META-INF/MANIFEST.MF"));
        manifest.write(dos);
        zos.closeEntry();
        return eBase64(md.digest());
    }

    private static void writeRSA(ZipOutputStream zos, byte[] sign) throws IOException {
        zos.putNextEntry(new ZipEntry("META-INF/CERT.RSA"));
        zos.write(dBase64("MIIB5gYJKoZIhvcNAQcCoIIB1zCCAdMCAQExCzAJBgUrDgMCGgUAMAsGCSqGSIb3DQEHAaCCATYwggEyMIHdoAMCAQICBCunMokwDQYJKoZIhvcNAQELBQAwDzENMAsGA1UEAxMEVGVzdDAeFw0xMjA0MjIwODQ1NDdaFw0xMzA0MjIwODQ1NDdaMA8xDTALBgNVBAMTBFRlc3QwXDANBgkqhkiG9w0BAQEFAANLADBIAkEAoiZSqWnFDHA5sXKoDiUUO9JuL7cm/2dCck5MKumVvv+WfSg0jsovnywsFN0pifmdRSLmOdUkh0d0J+tOnSgtsQIDAQABoyEwHzAdBgNVHQ4EFgQUVL2yOinUwpARE1tOPxc1bf4WrTgwDQYJKoZIhvcNAQELBQADQQAnj/eZwhqwb2tgSYNvgRo5bBNNCpJbQ4alEeP/MLSIWf2nZpAix8T3oS9X2affQtAgctPATcKQaiH2B4L7FKlVMXoweAIBATAXMA8xDTALBgNVBAMTBFRlc3QCBCunMokwCQYFKw4DAhoFADANBgkqhkiG9w0BAQEFAARA"));
        zos.write(sign);
        zos.closeEntry();
    }

    private static byte[] writeSF(ZipOutputStream zos, Manifest sf, String sha1Manifest) throws Exception {
        Signature signature = instanceSignature();
        zos.putNextEntry(new ZipEntry("META-INF/CERT.SF"));
        TinySign.SignatureOutputStream out = new TinySign.SignatureOutputStream(zos, signature);
        out.write("Signature-Version: 1.0\r\n".getBytes("UTF-8"));
        out.write(("Created-By: tiny-sign-" + TinySign.class.getPackage().getImplementationVersion() + "\r\n").getBytes("UTF-8"));
        out.write("SHA1-Digest-Manifest: ".getBytes("UTF-8"));
        out.write(sha1Manifest.getBytes("UTF-8"));
        out.write(13);
        out.write(10);
        sf.write(out);
        zos.closeEntry();
        return signature.sign();
    }

    private static void zipAndSha1(File dir, ZipOutputStream zos, DigestOutputStream dos, Manifest m) throws NoSuchAlgorithmException, IOException {
        File[] arr$ = dir.listFiles();
        int len$ = arr$.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            File f = arr$[i$];
            if (!f.getName().startsWith("META-INF")) {
                if (f.isFile()) {
                    doFile(f.getName(), f, zos, dos, m);
                } else {
                    doDir(f.getName() + "/", f, zos, dos, m);
                }
            }
        }

    }

    private static class SignatureOutputStream extends FilterOutputStream {
        private Signature mSignature;

        public SignatureOutputStream(OutputStream out, Signature sig) {
            super(out);
            this.mSignature = sig;
        }

        public void write(byte[] buffer) throws IOException {
            try {
                this.mSignature.update(buffer);
            } catch (SignatureException var3) {
                throw new IOException("SignatureException: " + var3);
            }

            this.out.write(buffer);
        }

        public void write(byte[] b, int off, int len) throws IOException {
            try {
                this.mSignature.update(b, off, len);
            } catch (SignatureException var5) {
                throw new IOException("SignatureException: " + var5);
            }

            this.out.write(b, off, len);
        }

        public void write(int b) throws IOException {
            try {
                this.mSignature.update((byte)b);
            } catch (SignatureException var3) {
                throw new IOException("SignatureException: " + var3);
            }

            this.out.write(b);
        }
    }
}
