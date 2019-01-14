package com.stardust.auojs.inrt.launch

import android.annotation.SuppressLint
import com.stardust.app.GlobalAppContext

/**
 * Created by Stardust on 2018/3/21.
 */

@SuppressLint("StaticFieldLeak")
object GlobalProjectLauncher: AssetsProjectLauncher("project", GlobalAppContext.get())
