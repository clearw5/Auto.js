package com.stardust.view.accessibility;

import android.view.KeyEvent;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Stardust on 2018/2/27.
 */

public interface KeyInterceptor {

    boolean onInterceptKeyEvent(KeyEvent event);

    class Observer implements KeyInterceptor {
        private CopyOnWriteArrayList<KeyInterceptor> mKeyInterceptors = new CopyOnWriteArrayList<>();

        public void addKeyInterrupter(KeyInterceptor interrupter){
            mKeyInterceptors.add(interrupter);
        }

        public boolean removeKeyInterrupter(KeyInterceptor interrupter){
            return mKeyInterceptors.remove(interrupter);
        }


        @Override
        public boolean onInterceptKeyEvent(KeyEvent event) {
            for(KeyInterceptor interrupter : mKeyInterceptors){
                try {
                    if(interrupter.onInterceptKeyEvent(event)){
                        return true;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            return false;
        }
    }
}
