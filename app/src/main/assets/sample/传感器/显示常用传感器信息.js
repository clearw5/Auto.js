"ui";

ui.layout(
    <scroll>
        <vertical>
            <text id="accelerometer" margin="12dp" textSize="16sp" textColor="#000000"/>
            <text id="orientation" margin="12dp" textSize="16sp" textColor="#000000"/>
            <text id="gyroscope" margin="12dp" textSize="16sp" textColor="#000000"/>
            <text id="magnetic_field" margin="12dp" textSize="16sp" textColor="#000000"/>
            <text id="gravity" margin="12dp" textSize="16sp" textColor="#000000"/>
            <text id="linear_acceleration" margin="12dp" textSize="16sp" textColor="#000000"/>
            <text id="ambient_temperature" margin="12dp" textSize="16sp" textColor="#000000"/>
            <text id="light" margin="12dp" textSize="16sp" textColor="#000000"/>
            <text id="pressure" margin="12dp" textSize="16sp" textColor="#000000"/>
            <text id="proximity" margin="12dp" textSize="16sp" textColor="#000000"/>
            <text id="relative_humidity" margin="12dp" textSize="16sp" textColor="#000000"/>
        </vertical>
    </scroll>
);

//忽略不支持的传感器，即使有传感器不支持也不抛出异常
sensors.ignoresUnsupportedSensor = true;

sensors.on("unsupported_sensor", function(sensorName, sensorType){
    log(util.format("不支持的传感器: %s 类型: %d", sensorName, sensorType));
});

//加速度传感器
sensors.register("accelerometer", sensors.delay.ui).on("change", (event, ax, ay, az)=>{
    ui.accelerometer.setText(util.format("x方向加速度: %d\ny方向加速度: %d\nz方向加速度: %d", ax, ay, az));
});
//方向传感器
sensors.register("orientation", sensors.delay.ui).on("change", (event, dx, dy, dz)=>{
    ui.orientation.setText(util.format("绕x轴转过角度: %d\n绕y轴转过角度: %d\n绕z轴转过角度: %d", dx, dy, dz));
});
//陀螺仪传感器
sensors.register("gyroscope", sensors.delay.ui).on("change", (event, wx, wy, wz)=>{
    ui.gyroscope.setText(util.format("绕x轴角速度: %d\n绕y轴角速度: %d\n绕z轴角速度: %d", wx, wy, wz));
});
//磁场传感器
sensors.register("magnetic_field", sensors.delay.ui).on("change", (event, bx, by, bz)=>{
    ui.magnetic_field.setText(util.format("x方向磁场强度: %d\ny方向磁场强度: %d\nz方向磁场强度: %d", bx, by, bz));
});
//重力传感器
sensors.register("gravity", sensors.delay.ui).on("change", (event, gx, gy, gz)=>{
    ui.gravity.setText(util.format("x方向重力: %d\ny方向重力: %d\nz方向重力: %d", gx, gy, gz));
});
//线性加速度传感器
sensors.register("linear_acceleration", sensors.delay.ui).on("change", (event, ax, ay, az)=>{
    ui.linear_acceleration.setText(util.format("x方向线性加速度: %d\ny方向线性加速度: %d\nz方向线性加速度: %d", ax, ay, az));
});
//温度传感器
sensors.register("ambient_temperature", sensors.delay.ui).on("change", (event, t)=>{
    ui.ambient_temperature.setText(util.format("当前温度: %d", t));
});
//光线传感器
sensors.register("light", sensors.delay.ui).on("change", (event, l)=>{
    ui.light.setText(util.format("当前光的强度: %d", l));
});
//压力传感器
sensors.register("pressure", sensors.delay.ui).on("change", (event, p)=>{
    ui.pressure.setText(util.format("当前压力: %d", p));
});
//距离传感器
sensors.register("proximity", sensors.delay.ui).on("change", (event, d)=>{
    ui.proximity.setText(util.format("当前距离: %d", d));
});
//湿度传感器
sensors.register("relative_humidity", sensors.delay.ui).on("change", (event, rh)=>{
    ui.relative_humidity.setText(util.format("当前相对湿度: %d", rh));
});

//30秒后退出程序
setTimeout(exit, 30 * 1000);
