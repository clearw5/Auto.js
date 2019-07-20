# Media

> Stability: 2 - Stable

media模块提供多媒体编程的支持。目前仅支持音乐播放和媒体文件扫描。后续会结合UI加入视频播放等功能。

需要注意是，使用该模块播放音乐时是在后台异步播放的，在脚本结束后会自动结束播放，因此可能需要插入诸如`sleep()`的语句来使脚本保持运行。例如：
```
//播放音乐
media.playMusic("/sdcard/1.mp3");
//让音乐播放完
sleep(media.getMusicDuration());
```

## media.scanFile(path)
* `path` {string} 媒体文件路径

扫描路径path的媒体文件，将它加入媒体库中；或者如果该文件以及被删除，则通知媒体库移除该文件。

媒体库包括相册、音乐库等，因此该函数可以用于把某个图片文件加入相册。

```
//请求截图
requestScreenCapture(false);
//截图
var im = captureScreen();
var path = "/sdcard/screenshot.png";
//保存图片
im.saveTo(path);
//把图片加入相册
media.scanFile(path);
```

## media.playMusic(path[, volume, looping])
* `path` {string} 音乐文件路径
* `volume` {number} 播放音量，为0~1的浮点数，默认为1
* `looping` {boolean} 是否循环播放，如果looping为`true`则循环播放，默认为`false`

播放音乐文件path。该函数不会显示任何音乐播放界面。如果文件不存在或者文件不是受支持的音乐格式，则抛出`UncheckedIOException`异常。

```
//播放音乐
media.playMusic("/sdcard/1.mp3");
//让音乐播放完
sleep(media.getMusicDuration());
```

如果要循环播放音乐，则使用looping参数：
```
```
//传递第三个参数为true以循环播放音乐
media.playMusic("/sdcard/1.mp3", 1, true);
//等待三次播放的时间
sleep(media.getMusicDuration() * 3);
```
```

如果要使用音乐播放器播放音乐，调用`app.viewFile(path)`函数。

## media.musicSeekTo(msec)
* `msec` {number} 毫秒数，表示音乐进度

把当前播放进度调整到时间msec的位置。如果当前没有在播放音乐，则调用函数没有任何效果。

例如，要把音乐调到1分钟的位置，为`media.musicSeekTo(60 * 1000)`。

```
//播放音乐
media.playMusic("/sdcard/1.mp3");
//调整到30秒的位置
media.musicSeekTo(30 * 1000);
//等待音乐播放完成
sleep(media.getMusicDuration() - 30 * 1000);
```

## media.pauseMusic()

暂停音乐播放。如果当前没有在播放音乐，则调用函数没有任何效果。

## media.resumeMusic()

继续音乐播放。如果当前没有播放过音乐，则调用该函数没有任何效果。

## media.stopMusic()

停止音乐播放。如果当前没有在播放音乐，则调用函数没有任何效果。

## media.isMusicPlaying()
* 返回 {boolean}

返回当前是否正在播放音乐。

## media.getMusicDuration()
* 返回 {number}

返回当前音乐的时长。单位毫秒。

## media.getMusicCurrentPosition()
* 返回 {number}

返回当前音乐的播放进度(已经播放的时间)，单位毫秒。