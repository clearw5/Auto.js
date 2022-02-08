let currentEngine = engines.myEngine()
let runningEngines = engines.all()
let currentSource = currentEngine.getSource() + ''
if (runningEngines.length > 1) {
  runningEngines.forEach(compareEngine => {
    let compareSource = compareEngine.getSource() + ''
    if (currentEngine.id !== compareEngine.id && compareSource === currentSource) {
      // 强制关闭同名的脚本
      compareEngine.forceStop()
    }
  })
}

if (!requestScreenCapture()) {
  toastLog('请求截图权限失败')
  exit()
}

sleep(1000)

importClass(com.baidu.paddle.lite.ocr.Predictor)

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
if (!loadSuccess) {
  toastLog('初始化ocr失败')
  exit()
}
// 识别结果和截图信息
let result = []
let img = null
let running = true
let capturing = true

/**
 * 截图并识别OCR文本信息
 */
function captureAndOcr() {
  capturing = true
  img && img.recycle()
  img = captureScreen()
  if (!img) {
    toastLog('截图失败')
  }
  let start = new Date()
  result = predictor.runOcr(img.getBitmap())
  toastLog('耗时' + (new Date() - start) + 'ms')
  capturing = false
}

captureAndOcr()

// 获取状态栏高度
let offset = -getStatusBarHeightCompat()

// 绘制识别结果
let window = floaty.rawWindow(
  <canvas id="canvas" layout_weight="1" />
);

// 设置悬浮窗位置
ui.post(() => {
  window.setPosition(0, offset)
  window.setSize(device.width, device.height)
  window.setTouchable(false)
})

// 操作按钮
let clickButtonWindow = floaty.rawWindow(
  <vertical>
    <button id="captureAndOcr" text="截图识别" />
    <button id="closeBtn" text="退出" />
  </vertical>
);
ui.run(function () {
  clickButtonWindow.setPosition(device.width / 2 - ~~(clickButtonWindow.getWidth() / 2), device.height * 0.65)
})

// 点击识别
clickButtonWindow.captureAndOcr.click(function () {
  result = []
  ui.run(function () {
    clickButtonWindow.setPosition(device.width, device.height)
  })
  setTimeout(() => {
    captureAndOcr()
    ui.run(function () {
      clickButtonWindow.setPosition(device.width / 2 - ~~(clickButtonWindow.getWidth() / 2), device.height * 0.65)
    })
  }, 500)
})

// 点击关闭
clickButtonWindow.closeBtn.click(function () {
  exit()
})

let Typeface = android.graphics.Typeface
let paint = new Paint()
paint.setStrokeWidth(1)
paint.setTypeface(Typeface.DEFAULT_BOLD)
paint.setTextAlign(Paint.Align.LEFT)
paint.setAntiAlias(true)
paint.setStrokeJoin(Paint.Join.ROUND)
paint.setDither(true)
window.canvas.on('draw', function (canvas) {
  if (!running || capturing) {
    return
  }
  // 清空内容
  canvas.drawColor(0xFFFFFF, android.graphics.PorterDuff.Mode.CLEAR)
  if (result && result.length > 0) {
    for (let i = 0; i < result.length; i++) {
      let ocrResult = result[i]
      drawRectAndText(ocrResult.label, ocrResult.bounds, '#00ff00', canvas, paint)
    }
  }
})

setInterval(() => { }, 10000)
events.on('exit', () => {
  // 标记停止 避免canvas导致闪退
  running = false
  // 回收图片
  img && img.recycle()
  // 撤销监听
  window.canvas.removeAllListeners()
  // 释放模型
  predictor.releaseModel()
})

/**
 * 绘制文本和方框
 *
 * @param {*} desc
 * @param {*} rect
 * @param {*} colorStr
 * @param {*} canvas
 * @param {*} paint
 */
function drawRectAndText (desc, rect, colorStr, canvas, paint) {
  let color = colors.parseColor(colorStr)

  paint.setStrokeWidth(1)
  paint.setStyle(Paint.Style.STROKE)
  // 反色
  paint.setARGB(255, 255 - (color >> 16 & 0xff), 255 - (color >> 8 & 0xff), 255 - (color & 0xff))
  canvas.drawRect(rect, paint)
  paint.setARGB(255, color >> 16 & 0xff, color >> 8 & 0xff, color & 0xff)
  paint.setStrokeWidth(1)
  paint.setTextSize(20)
  paint.setStyle(Paint.Style.FILL)
  canvas.drawText(desc, rect.left, rect.top, paint)
  paint.setTextSize(10)
  paint.setStrokeWidth(1)
  paint.setARGB(255, 0, 0, 0)
}

/**
 * 获取状态栏高度
 *
 * @returns
 */
function getStatusBarHeightCompat () {
  let result = 0
  let resId = context.getResources().getIdentifier("status_bar_height", "dimen", "android")
  if (resId > 0) {
    result = context.getResources().getDimensionPixelOffset(resId)
  }
  if (result <= 0) {
    result = context.getResources().getDimensionPixelOffset(R.dimen.dimen_25dp)
  }
  return result
}
