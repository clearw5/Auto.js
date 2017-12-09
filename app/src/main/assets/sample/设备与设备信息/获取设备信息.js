
console.show();

var str = "";
str += "屏幕宽度:" + device.width;
str += "\n屏幕高度:" + device.height;
str += "\nbuildId:" + device.buildId;
str += "\n主板:" + device.board;
str += "\n制造商:" + device.brand;
str += "\n型号:" + device.model;
str += "\n产品名称:" + device.product;
str += "\nbootloader版本:" + device.bootloader;
str += "\n硬件名称:" + device.hardware;
str += "\n唯一标识码:" + device.fingerprint;
str += "\nIMEI: " + device.getIMEI();
str += "\nAndroidId: " + device.getAndroidId();
str += "\nMac: " + device.getMacAddress();
str += "\nAPI: " + device.sdkInt;
str += "\n电量: " + device.getBattery();

log(str);
