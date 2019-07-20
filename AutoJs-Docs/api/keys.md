# Keys

按键模拟部分提供了一些模拟物理按键的全局函数，包括Home、音量键、照相键等，有的函数依赖于无障碍服务，有的函数依赖于root权限。

一般来说，以大写字母开头的函数都依赖于root权限。执行此类函数时，如果没有root权限，则函数执行后没有效果，并会在控制台输出一个警告。

## back()
* 返回 {boolean}

模拟按下返回键。返回是否执行成功。
此函数依赖于无障碍服务。

## home()
* 返回 {boolean}

模拟按下Home键。返回是否执行成功。
此函数依赖于无障碍服务。

## powerDialog()
* 返回 {boolean}

弹出电源键菜单。返回是否执行成功。
此函数依赖于无障碍服务。

## notifications()
* 返回 {boolean}

拉出通知栏。返回是否执行成功。
此函数依赖于无障碍服务。

## quickSettings()
* 返回 {boolean}

显示快速设置(下拉通知栏到底)。返回是否执行成功。
此函数依赖于无障碍服务。

## recents()
* 返回 {boolean}

显示最近任务。返回是否执行成功。
此函数依赖于无障碍服务。

## splitScreen()
* 返回 {boolean}

分屏。返回是否执行成功。
此函数依赖于无障碍服务, 并且需要系统自身功能的支持。

## Home()
模拟按下Home键。
此函数依赖于root权限。

## Back()
模拟按下返回键。
此函数依赖于root权限。

## Power()
模拟按下电源键。
此函数依赖于root权限。

## Menu()
模拟按下菜单键。
此函数依赖于root权限。

## VolumeUp()
按下音量上键。
此函数依赖于root权限。

## VolumeDown()
按键音量上键。
此函数依赖于root权限。

## Camera()
模拟按下照相键。

## Up()
模拟按下物理按键上。
此函数依赖于root权限。

## Down()
模拟按下物理按键下。
此函数依赖于root权限。

## Left()
模拟按下物理按键左。
此函数依赖于root权限。

## Right()
模拟按下物理按键右。
此函数依赖于root权限。

## OK()
模拟按下物理按键确定。
此函数依赖于root权限。

## Text(text)
* text {string} 要输入的文字，只能为英文或英文符号
输入文字text。例如`Text("aaa");`

## KeyCode(code)
* code {number} | <String> 要按下的按键的数字代码或名称。参见下表。
模拟物理按键。例如`KeyCode(29)`和`KeyCode("KEYCODE_A")`是按下A键。

# 附录: KeyCode对照表

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



