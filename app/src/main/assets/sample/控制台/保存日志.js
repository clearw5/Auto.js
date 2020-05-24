console.setGlobalLogConfig({
    file: "/sdcard/log.txt"
});
console.log(1);
console.log(2);
console.error(3);
app.viewFile("/sdcard/log.txt");
