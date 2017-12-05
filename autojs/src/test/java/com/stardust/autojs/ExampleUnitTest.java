package com.stardust.autojs;

import android.webkit.MimeTypeMap;

import com.stardust.pio.PFiles;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {


    @Test
    public void test() throws IOException {
        File file = new File("C:\\Users\\Stardust\\Desktop\\1.txt");
        System.out.println(PFiles.read(file));
        String url = "http://posttestserver.com/post.php?dir=example";
        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(MediaType.parse("text/plain"), file))
                .addFormDataPart("other_field", "other_field_value")
                .build();
        Request request = new Request.Builder().url(url).post(formBody).build();
        Response response = client.newCall(request).execute();
        System.out.println(response.body().string());
    }

    public boolean equals(int i, int j) {
        return i == j;
    }
}