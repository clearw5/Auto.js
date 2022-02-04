importClass(com.baidu.paddle.lite.ocr.Predictor)

console.show()
let path = 'test.jpg'
// 创建检测器
let predictor = new Predictor()
// 初始化模型 首次运行时会比较耗时
let loadSuccess = predictor.init(context)
// 内置的模型只有一个models/ocr_v2_for_cpu，初始化自定义模型请写绝对路径否则无法获取到
// 使用自定义模型时det rec cls三个模型文件名称需要修改为，后续版本开放自定义文件名
//    ch_ppocr_mobile_v2.0_det_opt.nb
//    ch_ppocr_mobile_v2.0_rec_opt.nb
//    ch_ppocr_mobile_v2.0_cls_opt.nb
// predictor.init(context, modelPath, labelPath)
toastLog('加载模型结果：' + loadSuccess)
let start = new Date()
let img = images.read('test.png')
let results = predictor.runOcr(img.getBitmap())
toastLog('识别结束, 耗时：' + (new Date() - start) + 'ms')
log('识别结果：' + JSON.stringify(results))
// 释放模型 用于释放native内存
predictor.releaseModel()
// 回收图片
img.recycle()




