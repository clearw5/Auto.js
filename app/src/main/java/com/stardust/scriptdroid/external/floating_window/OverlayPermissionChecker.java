package com.stardust.scriptdroid.external.floating_window;

import android.view.WindowManager;

import com.stardust.enhancedfloaty.FloatyService;
import com.stardust.enhancedfloaty.FloatyWindow;

/**
 * Created by Stardust on 2017/5/9.
 */

public class OverlayPermissionChecker {


    public static void addFloaty() {
        FloatyService.addWindow(new FloatyWindow() {
            @Override
            public void onCreate(FloatyService floatyService, WindowManager windowManager) {

            }

            @Override
            public void onServiceDestroy(FloatyService floatyService) {

            }

            @Override
            public void close() {

            }
        });
    }

    private static class OnePixelWindow implements FloatyWindow {

        @Override
        public void onCreate(FloatyService floatyService, WindowManager windowManager) {
            
        }

        @Override
        public void onServiceDestroy(FloatyService floatyService) {

        }

        @Override
        public void close() {

        }
    }

}
