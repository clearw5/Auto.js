const img = images.read("./test.png");
const cpuThreadNum = 4;
// PaddleOCR 移动端提供了两种模型：ocr_v2_for_cpu与ocr_v2_for_cpu(slim)，此选项用于选择加载的模型,默认true使用v2的slim版(速度更快)，false使用v2的普通版(准确率更高）
var useSlim = true;
// 识别图片中的文字，返回完整识别信息（兼容百度OCR格式）。
const result = ocr.R(img, cpuThreadNum, useSlim);
// 可以使用简化的调用命令，默认参数：cpuThreadNum = 4, useSlim = true;
// const result = ocr.R(img);
toastLog("完整识别信息（兼容百度OCR格式）: " + JSON.stringify(result));

// 识别图片中的文字，只返回文本识别信息（字符串列表）。
const stringList = ocr.T(img, cpuThreadNum, useSlim);
// 可以使用简化的调用命令，默认参数：cpuThreadNum = 4, useSlim = true;
// const stringList = ocr.T(img);
toastLog("文本识别信息（字符串列表）: " + JSON.stringify(stringList));
// 释放模型 用于释放native内存
ocr.release()
// 回收图片
img.recycle()
