package com.tony.autojs.common;

import android.content.res.Configuration;

import com.stardust.autojs.core.image.capture.ScreenCapturer;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.autojs.runtime.api.Images;

import java.lang.reflect.Field;

public class ImagesResolver {

    /**
     * 释放截图权限
     *
     * @param scriptRuntime
     * @throws Exception
     */
    public static void releaseImageCapture(ScriptRuntime scriptRuntime) throws Exception {
        Images images = (Images) scriptRuntime.getImages();
        if (images != null) {
            images.releaseScreenCapturer();
        }
    }

    /**
     * 重新获取截图权限
     *
     * @param scriptRuntime
     * @return
     * @throws Exception
     */
    public static Object requestScreenCapture(ScriptRuntime scriptRuntime) throws Exception {
        Images images = (Images) scriptRuntime.getImages();
        if (images != null) {
            try {
                Field capturerField = Images.class.getDeclaredField("mScreenCapturer");
                capturerField.setAccessible(true);
                ScreenCapturer screenCapturer = (ScreenCapturer) capturerField.get(images);
                if (screenCapturer != null) {
                    Field orientationField = ScreenCapturer.class.getDeclaredField("mOrientation");
                    orientationField.setAccessible(true);
                    int orientation = orientationField.getInt(screenCapturer);
                    capturerField.set(images, null);
                    return images.requestScreenCapture(orientation);
                }
            } catch (NoSuchFieldException e) {
                return images.requestScreenCapture(Configuration.ORIENTATION_UNDEFINED);
            } catch (Exception e) {
                //...
            }
        }
        return null;
    }

    /**
     * 免费版释放截图权限状态
     *
     * @param scriptRuntime
     * @throws Exception
     */
    public static void clearScreenCaptureState(ScriptRuntime scriptRuntime) throws Exception {
        Images images = (Images) scriptRuntime.getImages();
        if (images != null) {
            images.releaseScreenCapturer();
            try {
                Field capturerField = Images.class.getDeclaredField("mScreenCapturer");
                capturerField.setAccessible(true);
                ScreenCapturer screenCapturer = (ScreenCapturer) capturerField.get(images);
                if (screenCapturer != null) {
                    capturerField.set(images, null);
                }
            } catch (Exception e) {
                //
            }
        }
    }


}
