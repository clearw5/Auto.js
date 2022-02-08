const img = images.read("./test.png")
console.show()
let cpuThreadNum = 4
// PaddleOCR 移动端提供了两种模型：ocr_v2_for_cpu与ocr_v2_for_cpu(slim)，此选项用于选择加载的模型,默认true使用v2的slim版(速度更快)，false使用v2的普通版(准确率更高）
let useSlim = true
let start = new Date()
// 识别图片中的文字，返回完整识别信息（兼容百度OCR格式）。
let result = $ocr.detect(img, cpuThreadNum, useSlim)
log('slim识别耗时：' + (new Date() - start) + 'ms')
// 可以使用简化的调用命令，默认参数：cpuThreadNum = 4, useSlim = true
// const result = $ocr.detect(img)
toastLog("完整识别信息: " + JSON.stringify(result))
start = new Date()
// 识别图片中的文字，只返回文本识别信息（字符串列表）。当前版本可能存在文字顺序错乱的问题 建议先使用detect后自行排序
const stringList = $ocr.recognizeText(img, cpuThreadNum, useSlim)
log('slim纯文本识别耗时：' + (new Date() - start) + 'ms')
// 可以使用简化的调用命令，默认参数：cpuThreadNum = 4, useSlim = true
// const stringList = $ocr.recognizeText(img)
toastLog("文本识别信息（字符串列表）: " + JSON.stringify(stringList))
// 增加线程数
cpuThreadNum = 8
start = new Date()
result = $ocr.detect(img, cpuThreadNum, useSlim)
log('8线程识别耗时：' + (new Date() - start) + 'ms')
toastLog("完整识别信息（兼容百度OCR格式）: " + JSON.stringify(result))
// 释放模型 用于释放native内存
$ocr.release()
// 回收图片
img.recycle()
