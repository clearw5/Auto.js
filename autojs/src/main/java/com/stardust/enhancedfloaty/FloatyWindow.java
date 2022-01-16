package com.stardust.enhancedfloaty;

import android.view.View;
import android.view.WindowManager;

import androidx.annotation.CallSuper;

/**
 * Created by Stardust on 2017/5/1.
 */

public abstract class FloatyWindow {
    private WindowManager mWindowManager;
    private FloatyService mFloatyService;

    private WindowBridge mWindowBridge;
    private WindowManager.LayoutParams mWindowLayoutParams;
    private View mWindowView;

    @CallSuper
    public void onCreate(FloatyService service, WindowManager manager) {
        mFloatyService = service;
        mWindowManager = manager;
        onCreateWindow(service, manager);
    }


    protected void onCreateWindow(FloatyService service, WindowManager manager) {
        setWindowLayoutParams(onCreateWindowLayoutParams());
        setWindowView(onCreateView(service));
        setWindowBridge(onCreateWindowBridge(getWindowLayoutParams()));

        onViewCreated(getWindowView());

        //attach to window
        attachToWindow(getWindowView(), getWindowManager());
    }

    protected void onViewCreated(View view) {

    }

    protected void attachToWindow(View view, WindowManager manager){
        getWindowManager().addView(view, getWindowLayoutParams());
        onAttachToWindow(view, manager);
    }

    protected void onAttachToWindow(View view, WindowManager manager) {

    }

    protected abstract View onCreateView(FloatyService service);

    protected WindowBridge onCreateWindowBridge(WindowManager.LayoutParams params) {
        return new WindowBridge.DefaultImpl(params, getWindowManager(), getWindowView());

    }

    protected abstract WindowManager.LayoutParams onCreateWindowLayoutParams();

    public void updateWindowLayoutParams(WindowManager.LayoutParams params) {
        setWindowLayoutParams(params);
        mWindowManager.updateViewLayout(getWindowView(), getWindowLayoutParams());
    }


    protected void setWindowManager(WindowManager windowManager) {
        mWindowManager = windowManager;
    }

    public WindowManager.LayoutParams getWindowLayoutParams() {
        return mWindowLayoutParams;
    }

    protected void setWindowLayoutParams(WindowManager.LayoutParams windowLayoutParams) {
        mWindowLayoutParams = windowLayoutParams;
    }

    public View getWindowView() {
        return mWindowView;
    }

    protected void setWindowView(View windowView) {
        mWindowView = windowView;
    }

    public FloatyService getFloatyService() {
        return mFloatyService;
    }

    public WindowManager getWindowManager() {
        return mWindowManager;
    }

    public WindowBridge getWindowBridge() {
        return mWindowBridge;
    }

    protected void setWindowBridge(WindowBridge windowBridge) {
        mWindowBridge = windowBridge;
    }

    public void onServiceDestroy(FloatyService service) {
        close();
    }


    public void close() {
        try {
            getWindowManager().removeView(getWindowView());
            FloatyService.removeWindow(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
