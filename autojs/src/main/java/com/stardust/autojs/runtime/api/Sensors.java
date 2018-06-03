package com.stardust.autojs.runtime.api;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;

import com.stardust.autojs.core.eventloop.EventEmitter;
import com.stardust.autojs.core.looper.Loopers;
import com.stardust.autojs.runtime.ScriptBridges;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.util.MapEntries;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by Stardust on 2018/2/5.
 */

public class Sensors extends EventEmitter implements Loopers.LooperQuitHandler {


    public class SensorEventEmitter extends EventEmitter implements SensorEventListener {

        public SensorEventEmitter(ScriptBridges bridges) {
            super(bridges);
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            Object[] args = new Object[event.values.length + 1];
            args[0] = event;
            for (int i = 1; i < args.length; i++) {
                args[i] = event.values[i - 1];
            }
            emit("change", args);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            emit("accuracy_change", accuracy);
        }

        public void unregister() {
            Sensors.this.unregister(this);
        }
    }

    public static class Delay {
        public static final int normal = SensorManager.SENSOR_DELAY_NORMAL;
        public static final int ui = SensorManager.SENSOR_DELAY_UI;
        public static final int game = SensorManager.SENSOR_DELAY_GAME;
        public static final int fastest = SensorManager.SENSOR_DELAY_FASTEST;
    }


    private static final Map<String, Integer> SENSORS = new MapEntries<String, Integer>()
            .entry("ACCELEROMETER", Sensor.TYPE_ACCELEROMETER)
            .entry("MAGNETIC_FIELD", Sensor.TYPE_MAGNETIC_FIELD)
            .entry("ORIENTATION", Sensor.TYPE_ORIENTATION)
            .entry("GYROSCOPE", Sensor.TYPE_GYROSCOPE)
            .entry("LIGHT", Sensor.TYPE_LIGHT)
            .entry("TEMPERATURE", Sensor.TYPE_TEMPERATURE)
            .entry("PRESSURE", Sensor.TYPE_PRESSURE)
            .entry("AMBIENT_TEMPERATURE", Sensor.TYPE_AMBIENT_TEMPERATURE)
            .entry("PROXIMITY", Sensor.TYPE_PROXIMITY)
            .entry("GRAVITY", Sensor.TYPE_GRAVITY)
            .entry("LINEAR_ACCELERATION", Sensor.TYPE_LINEAR_ACCELERATION)
            .entry("RELATIVE_HUMIDITY", Sensor.TYPE_RELATIVE_HUMIDITY)
            .entry("AMBIENT_TEMPERATURE", Sensor.TYPE_AMBIENT_TEMPERATURE)
            .map();

    public boolean ignoresUnsupportedSensor = false;
    public final Delay delay = new Delay();

    private final Set<SensorEventEmitter> mSensorEventEmitters = new HashSet<>();
    private final SensorManager mSensorManager;
    private final ScriptBridges mScriptBridges;
    private final SensorEventEmitter mNoOpSensorEventEmitter;
    private final ScriptRuntime mScriptRuntime;


    public Sensors(Context context, ScriptRuntime runtime) {
        super(runtime.bridges);
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mScriptBridges = runtime.bridges;
        mNoOpSensorEventEmitter = new SensorEventEmitter(runtime.bridges);
        mScriptRuntime = runtime;
        runtime.loopers.addLooperQuiteHandler(this);
    }

    public SensorEventEmitter register(String sensorName) {
        return register(sensorName, Delay.normal);
    }

    public SensorEventEmitter register(String sensorName, int delay) {
        if (sensorName == null)
            throw new NullPointerException("sensorName = null");
        Sensor sensor = getSensor(sensorName);
        if (sensor == null) {
            if (ignoresUnsupportedSensor) {
                emit("unsupported_sensor", sensorName);
                return mNoOpSensorEventEmitter;
            } else {
                return null;
            }
        }
        return register(sensor, delay);
    }

    private SensorEventEmitter register(@NonNull Sensor sensor, int delay) {
        SensorEventEmitter emitter = new SensorEventEmitter(mScriptBridges);
        mSensorManager.registerListener(emitter, sensor, delay);
        synchronized (mSensorEventEmitters) {
            mSensorEventEmitters.add(emitter);
        }
        return emitter;
    }


    @Override
    public boolean shouldQuit() {
        if (mSensorEventEmitters.isEmpty()) {
            return true;
        }
        return false;
    }

    public Sensor getSensor(String sensorName) {
        Integer type = SENSORS.get(sensorName.toUpperCase());
        if (type == null)
            type = getSensorTypeByReflect(sensorName);
        if (type == null)
            return null;
        return mSensorManager.getDefaultSensor(type);
    }

    private Integer getSensorTypeByReflect(String sensorName) {
        sensorName = sensorName.toUpperCase();
        try {
            Field field = Sensor.class.getField("TYPE_" + sensorName);
            return (Integer) field.get(null);
        } catch (Exception e) {
            return null;
        }
    }

    public void unregister(SensorEventEmitter emitter) {
        if (emitter == null)
            return;
        synchronized (mSensorEventEmitters) {
            mSensorEventEmitters.remove(emitter);
        }
        mSensorManager.unregisterListener(emitter);
    }

    public void unregisterAll() {
        synchronized (mSensorEventEmitters) {
            for (SensorEventEmitter emitter : mSensorEventEmitters) {
                mSensorManager.unregisterListener(emitter);
            }
            mSensorEventEmitters.clear();
        }
    }
}
