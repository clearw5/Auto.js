
注意：本章节的函数在后续版本很可能有改动！请勿过分依赖本章节函数的副作用。推荐使用RootAutomator（见《需要Root权限的触摸与多点触摸》）代替本章的触摸函数。

以下函数均需要root权限，可以实现任意位置的点击、滑动、长按、模拟物理按键等。

这些函数通常首字母大写以表示其特殊的权限。  
这些函数均不返回任何值。  
并且，这些函数的执行是异步的、非实时的，在不同机型上所用的时间不同。脚本不会等待动作执行完成才继续执行。因此最好在每个函数之后加上适当的sleep来达到期望的效果。


例如:
```
Tap(100, 100);
sleep(500);
```

注意，动作的执行可能无法被停止，例如：
```
for(var i = 0; i < 100; i++){
  Tap(100, 100);
}
```
这段代码执行后可能会出现在任务管理中停止脚本后点击仍然继续的情况。
因此，强烈建议在每个动作后加上延时：
```
for(var i = 0; i < 100; i++){
  Tap(100, 100);
  sleep(500);
}
```


### Tap(x, y)
* x, y \<Number\> 要点击的坐标。

点击位置(x, y), 您可以通过"开发者选项"开启指针位置来确定点击坐标。

### Swipe(x1, y1, x2, y2, \[duration\])
* x1, y1 \<Number\> 滑动起点的坐标
* x2, y2 \<Number\> 滑动终点的坐标
* duration \<Number\> 滑动动作所用的时间

滑动。从(x1, y1)位置滑动到(x2, y2)位置。

### Home()
按下Home键。

### Back()
按下返回键。

### Power()
按下电源键。

### Menu()
按下菜单键。

### Up()
模拟按下物理按键上。

### Down()
模拟按下物理按键下。

### Left()
模拟按下物理按键左。

### Right()
模拟按下物理按键右。

### OK()
模拟按下物理按键确定。

### VolumeUp()
按下音量上键。

### VolumeDown()
按键音量上键。

### Text(text)
* text <String> 要输入的文字
输入文字text。例如`Text("测试");`

### Camera()
模拟按下照相键。

### KeyCode(code)
* code <Number> | <String> 要按下的按键的数字代码或名称。参见下表。
模拟物理按键。例如`KeyCode(29)`和`KeyCode("KEYCODE_A")`是按下A键。

KeyCode             KeyEvent Value  
* KEYCODE_MENU 1  
* KEYCODE_SOFT_RIGHT 2  
* KEYCODE_HOME 3  
* KEYCODE_BACK 4  
* KEYCODE_CALL 5  
* KEYCODE_ENDCALL 6  
* KEYCODE_0 7  
* KEYCODE_1 8  
* KEYCODE_2 9  
* KEYCODE_3 10  
* KEYCODE_4 11  
* KEYCODE_5 12  
* KEYCODE_6 13  
* KEYCODE_7 14  
* KEYCODE_8 15  
* KEYCODE_9 16  
* KEYCODE_STAR 17  
* KEYCODE_POUND 18  
* KEYCODE_DPAD_UP 19  
* KEYCODE_DPAD_DOWN 20  
* KEYCODE_DPAD_LEFT 21  
* KEYCODE_DPAD_RIGHT 22  
* KEYCODE_DPAD_CENTER 23  
* KEYCODE_VOLUME_UP 24  
* KEYCODE_VOLUME_DOWN 25  
* KEYCODE_POWER 26  
* KEYCODE_CAMERA 27  
* KEYCODE_CLEAR 28  
* KEYCODE_A 29  
* KEYCODE_B 30  
* KEYCODE_C 31  
* KEYCODE_D 32  
* KEYCODE_E 33  
* KEYCODE_F 34  
* KEYCODE_G 35  
* KEYCODE_H 36  
* KEYCODE_I 37  
* KEYCODE_J 38  
* KEYCODE_K 39  
* KEYCODE_L 40  
* KEYCODE_M 41  
* KEYCODE_N 42  
* KEYCODE_O 43  
* KEYCODE_P 44  
* KEYCODE_Q 45  
* KEYCODE_R 46  
* KEYCODE_S 47  
* KEYCODE_T 48  
* KEYCODE_U 49  
* KEYCODE_V 50  
* KEYCODE_W 51  
* KEYCODE_X 52  
* KEYCODE_Y 53  
* KEYCODE_Z 54  
* KEYCODE_COMMA 55  
* KEYCODE_PERIOD 56  
* KEYCODE_ALT_LEFT 57  
* KEYCODE_ALT_RIGHT 58  
* KEYCODE_SHIFT_LEFT 59  
* KEYCODE_SHIFT_RIGHT 60  
* KEYCODE_TAB 61  
* KEYCODE_SPACE 62  
* KEYCODE_SYM 63  
* KEYCODE_EXPLORER 64  
* KEYCODE_ENVELOPE 65  
* KEYCODE_ENTER 66  
* KEYCODE_DEL 67  
* KEYCODE_GRAVE 68  
* KEYCODE_MINUS 69  
* KEYCODE_EQUALS 70  
* KEYCODE_LEFT_BRACKET 71  
* KEYCODE_RIGHT_BRACKET 72  
* KEYCODE_BACKSLASH 73  
* KEYCODE_SEMICOLON 74  
* KEYCODE_APOSTROPHE 75  
* KEYCODE_SLASH 76  
* KEYCODE_AT 77  
* KEYCODE_NUM 78  
* KEYCODE_HEADSETHOOK 79  
* KEYCODE_FOCUS 80  
* KEYCODE_PLUS 81  
* KEYCODE_MENU 82  
* KEYCODE_NOTIFICATION 83  
* KEYCODE_SEARCH 84  
* TAG_LAST_ KEYCODE 85  


