package com.stardust.autojs.execution

import android.os.Parcel
import android.os.Parcelable
import com.stardust.autojs.project.ScriptConfig
import java.util.*

/**
 * Created by Stardust on 2017/2/1.
 */
data class ExecutionConfig(var workingDirectory: String = "",
                           var path: Array<out String> = emptyArray(),
                           var intentFlags: Int = 0,
                           var delay: Long = 0,
                           var interval: Long = 0,
                           var loopTimes: Int = 1,
                           var scriptConfig: ScriptConfig = ScriptConfig()) : Parcelable {


    private val mArguments = HashMap<String, Any>()

    val arguments: Map<String, Any>
        get() = mArguments

    constructor(parcel: Parcel) : this(
            parcel.readString().orEmpty(),
            parcel.createStringArray().orEmpty(),
            parcel.readInt(),
            parcel.readLong(),
            parcel.readLong(),
            parcel.readInt())

    fun setArgument(key: String, `object`: Any) {
        mArguments[key] = `object`
    }

    fun getArgument(key: String): Any? {
        return mArguments[key]
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ExecutionConfig

        if (workingDirectory != other.workingDirectory) return false
        if (!path.contentEquals(other.path)) return false
        if (intentFlags != other.intentFlags) return false
        if (delay != other.delay) return false
        if (interval != other.interval) return false
        if (loopTimes != other.loopTimes) return false
        if (mArguments != other.mArguments) return false

        return true
    }

    override fun hashCode(): Int {
        var result = workingDirectory.hashCode()
        result = 31 * result + path.contentHashCode()
        result = 31 * result + intentFlags
        result = 31 * result + delay.hashCode()
        result = 31 * result + interval.hashCode()
        result = 31 * result + loopTimes
        result = 31 * result + mArguments.hashCode()
        return result
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(workingDirectory)
        parcel.writeStringArray(path)
        parcel.writeInt(intentFlags)
        parcel.writeLong(delay)
        parcel.writeLong(interval)
        parcel.writeInt(loopTimes)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ExecutionConfig> {

        @JvmStatic
        val tag = "execution.config"

        @JvmStatic
        val default: ExecutionConfig
            get() = ExecutionConfig()

        override fun createFromParcel(parcel: Parcel): ExecutionConfig {
            return ExecutionConfig(parcel)
        }

        override fun newArray(size: Int): Array<ExecutionConfig?> {
            return arrayOfNulls(size)
        }
    }
}
