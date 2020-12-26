package com.tony.autojs.tool.encrypt;


import android.app.Activity;
import android.content.Context;

import com.stardust.autojs.execution.ExecutionConfig;
import com.stardust.autojs.runtime.api.Engines;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

public class DecryptRunner {

    private EnginesDelegate engines;
    private Context context;
    private Activity activity;
    private boolean isPro;
    private SecretKey secretKey;

    /**
     * @param engines
     * @param context
     * @param activity
     */
    public DecryptRunner(Engines engines, Context context, Activity activity) {
        this.engines = new EnginesDelegate(engines);
        this.context = context;
        this.activity = activity;
        this.isPro = this.context.getPackageName().equals("org.autojs.autojspro");
    }

    public void executeDecryptScriptString(String script, String workingDir) {
        ExecutionConfig config = new ExecutionConfig();
        config.setWorkingDirectory(workingDir);
        if (!this.isPro) {
            engines.execScript("tmp", script, config);
        } else {
            engines.execScript(activity, "tmp", script, config);
        }
    }

    public void decryptAndRun(String filePath, String workingDir) {
        executeDecryptScriptString(decryptData(filePath), workingDir);
    }

    public void encryptScript(String originFilePath, String destFilePath) {
        File originFile = new File(originFilePath);
        if (originFile.exists()) {
            try (
                    FileInputStream fileInputStream = new FileInputStream(originFile);
                    FileOutputStream fileOutputStream = new FileOutputStream(destFilePath);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ) {
                byte[] buffer = new byte[8192];
                int len = 0;
                while ((len = fileInputStream.read(buffer)) > 0) {
                    byteArrayOutputStream.write(buffer, 0, len);
                }
                byte[] bytes = AESEncrypt.encrypt(byteArrayOutputStream.toByteArray(), secretKey);
                if (bytes != null) {
                    fileOutputStream.write(bytes);
                }
            } catch (Exception e) {

            }
        }
    }

    private String decryptData(String filePath) {
        try (
                FileInputStream fileInputStream = new FileInputStream(filePath);
                BufferedInputStream reader = new BufferedInputStream(fileInputStream);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ) {
            byte[] buffer = new byte[8192];
            int length = -1;
            while ((length = reader.read(buffer)) > 0) {
                byteArrayOutputStream.write(buffer, 0, length);
            }
            byte[] bytes = AESEncrypt.decrypt(byteArrayOutputStream.toByteArray(), secretKey);
            if (bytes != null) {
                return new String(bytes, StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
        }
        return null;
    }

    public SecretKey getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(SecretKey secretKey) {
        this.secretKey = secretKey;
    }
}
