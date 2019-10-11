package com.stardust.auojs.inrt

import android.Manifest
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.widget.Toast

import com.stardust.auojs.inrt.autojs.AutoJs
import com.stardust.auojs.inrt.launch.GlobalProjectLauncher

import java.util.ArrayList

import android.content.pm.PackageManager.PERMISSION_DENIED

/**
 * Created by Stardust on 2018/2/2.
 */

class SplashActivity : AppCompatActivity() {

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val slug = findViewById<TextView>(R.id.slug)
        slug.typeface = Typeface.createFromAsset(assets, "roboto_medium.ttf")
        if (!Pref.isFirstUsing) {
            main()
        } else {
            Handler().postDelayed({ this@SplashActivity.main() }, INIT_TIMEOUT)
        }
    }

    private fun main() {
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE)
    }


    private fun runScript() {
        Thread {
            try {
                GlobalProjectLauncher.launch(this)
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@SplashActivity, e.message, Toast.LENGTH_LONG).show()
                    startActivity(Intent(this@SplashActivity, LogActivity::class.java))
                    AutoJs.instance!!.globalConsole.printAllStackTrace(e)
                }
            }
        }.start()
    }

    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {
        runScript()
    }

    private fun checkPermission(vararg permissions: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val requestPermissions = getRequestPermissions(permissions)
            if (requestPermissions.isNotEmpty()) {
                requestPermissions(requestPermissions, PERMISSION_REQUEST_CODE)
            } else {
                runScript()
            }
        } else {
            runScript()
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun getRequestPermissions(permissions: Array<out String>): Array<String> {
        val list = ArrayList<String>()
        for (permission in permissions) {
            if (checkSelfPermission(permission) == PERMISSION_DENIED) {
                list.add(permission)
            }
        }
        return list.toTypedArray()
    }

    companion object {

        private const val PERMISSION_REQUEST_CODE = 11186
        private const val INIT_TIMEOUT: Long = 2500
    }

}

