importClass(com.baidu.paddle.lite.ocr.Predictor)

console.show()
let path = 'test.jpg'
// 指定是否用精简版模型 速度较快
let useSlim = false
// 创建检测器
let predictor = new Predictor()
// predictor.cpuThreadNum = 4 //可以自定义使用CPU的线程数
// 初始化模型 首次运行时会比较耗时
let loadSuccess = predictor.init(context, useSlim) // predictor.init(context) 为默认不使用精简版
// 内置默认 modelPath 为 models/ocr_v2_for_cpu，初始化自定义模型请写绝对路径否则无法获取到
// 内置默认 labelPath 为 labels/ppocr_keys_v1.txt
// let modelPath = files.path('./models/customize') // 指定自定义模型路径
// let labelPath = files.path('./models/customize') // 指定自定义label路径
// 使用自定义模型时det rec cls三个模型文件名称需要手动指定
// predictor.detModelFilename = 'ch_ppocr_mobile_v2.0_det_opt.nb'
// predictor.recModelFilename = 'ch_ppocr_mobile_v2.0_rec_opt.nb'
// predictor.clsModelFilename = 'ch_ppocr_mobile_v2.0_cls_opt.nb'
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




