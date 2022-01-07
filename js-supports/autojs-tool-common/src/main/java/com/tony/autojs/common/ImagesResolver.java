package com.tony.autojs.common;

import com.stardust.autojs.core.image.capture.ScreenCapturer;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.autojs.runtime.api.Images;

import java.lang.reflect.Field;

public class ImagesResolver {

    public static void releaseImageCapture(ScriptRuntime scriptRuntime) throws Exception {
        Images images = (Images) scriptRuntime.getImages();
        if (images != null) {
            images.releaseScreenCapturer();
        }
    }

    public static Object requestScreenCapture(ScriptRuntime scriptRuntime) throws Exception {
        Images images = (Images) scriptRuntime.getImages();
        if (images != null) {
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
        }
        return null;
    }

    public static void clearScreenCaptureState(ScriptRuntime scriptRuntime) throws Exception {
        Images images = (Images) scriptRuntime.getImages();
        if (images != null) {
            images.releaseScreenCapturer();
            Field capturerField = Images.class.getDeclaredField("mScreenCapturer");
            capturerField.setAccessible(true);
            ScreenCapturer screenCapturer = (ScreenCapturer) capturerField.get(images);
            if (screenCapturer != null) {
                Field orientationField = ScreenCapturer.class.getDeclaredField("mOrientation");
                orientationField.setAccessible(true);
                capturerField.set(images, null);
            }
        }
    }


}
