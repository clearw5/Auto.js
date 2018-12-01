package androidx.recyclerview.widget

import android.content.Context
import android.os.Build.VERSION
import android.util.AttributeSet
import android.widget.EdgeEffect
import androidx.annotation.Nullable
import androidx.core.widget.EdgeEffectCompat
import com.stardust.theme.ThemeColor
import com.stardust.theme.ThemeColorManager
import com.stardust.theme.ThemeColorMutable
import java.lang.reflect.Field

open class ThemeColorRecyclerView : RecyclerView, ThemeColorMutable {
    private var mLeftGlowField: Field? = null
    private var mTopGlowField: Field? = null
    private var mRightGlowField: Field? = null
    private var mBottomGlowField: Field? = null
    private var mEdgeEffectField: Field? = null
    private var mColorPrimary: Int = 0
    private var hasAppliedThemeColorLeft: Boolean = false
    private var hasAppliedThemeColorTop: Boolean = false
    private var hasAppliedThemeColorRight: Boolean = false
    private var hasAppliedThemeColorBottom: Boolean = false

    constructor(context: Context) : super(context) {
        this.init()
    }

    constructor(context: Context, @Nullable attrs: AttributeSet) : super(context, attrs) {
        this.init()
    }

    constructor(context: Context, @Nullable attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        this.init()
    }

    private fun applyThemeColor(edgeEffectCompatField: Field?): Boolean {
        try {
            val edgeEffectCompat = edgeEffectCompatField?.get(this) as EdgeEffectCompat?
            if (edgeEffectCompat != null) {
                return this.setEdgeEffectColor(edgeEffectCompat, this.mColorPrimary)
            }
        } catch (var3: Exception) {
        }

        return false
    }

    private fun init() {
        try {
            this.mEdgeEffectField = EdgeEffectCompat::class.java.getDeclaredField("mEdgeEffect")
            this.mEdgeEffectField!!.isAccessible = true
            this.mLeftGlowField = RecyclerView::class.java.getDeclaredField("mLeftGlow")
            this.mLeftGlowField!!.isAccessible = true
            this.mRightGlowField = RecyclerView::class.java.getDeclaredField("mRightGlow")
            this.mRightGlowField!!.isAccessible = true
            this.mTopGlowField = RecyclerView::class.java.getDeclaredField("mTopGlow")
            this.mTopGlowField!!.isAccessible = true
            this.mBottomGlowField = RecyclerView::class.java.getDeclaredField("mBottomGlow")
            this.mBottomGlowField!!.isAccessible = true
        } catch (var2: Exception) {
            var2.printStackTrace()
        }

        ThemeColorManager.add(this)
    }

    private fun setEdgeEffectColor(compat: EdgeEffectCompat?, color: Int): Boolean {
        return if (compat == null) {
            false
        } else {
            try {
                if (VERSION.SDK_INT >= 21) {
                    val edgeEffect = this.mEdgeEffectField!!.get(compat) as EdgeEffect
                    edgeEffect.color = color
                }

                true
            } catch (var4: Exception) {
                true
            }

        }
    }

    override fun setThemeColor(color: ThemeColor) {
        if (color.colorPrimary != this.mColorPrimary) {
            this.mColorPrimary = color.colorPrimary
            this.invalidateGlows()
        }
    }

    internal override fun invalidateGlows() {
        super.invalidateGlows()
        this.hasAppliedThemeColorTop = false
        this.hasAppliedThemeColorRight = this.hasAppliedThemeColorTop
        this.hasAppliedThemeColorLeft = this.hasAppliedThemeColorRight
        this.hasAppliedThemeColorBottom = this.hasAppliedThemeColorLeft
    }

    internal override fun ensureLeftGlow() {
        super.ensureLeftGlow()
        if (!this.hasAppliedThemeColorLeft) {
            this.hasAppliedThemeColorLeft = this.applyThemeColor(this.mLeftGlowField)
        }

    }

    internal override fun ensureRightGlow() {
        super.ensureLeftGlow()
        if (!this.hasAppliedThemeColorRight) {
            this.hasAppliedThemeColorRight = this.applyThemeColor(this.mRightGlowField)
        }

    }

    internal override fun ensureTopGlow() {
        super.ensureTopGlow()
        if (!this.hasAppliedThemeColorTop) {
            this.hasAppliedThemeColorTop = this.applyThemeColor(this.mTopGlowField)
        }

    }

    internal override fun ensureBottomGlow() {
        super.ensureBottomGlow()
        if (!this.hasAppliedThemeColorBottom) {
            this.hasAppliedThemeColorBottom = this.applyThemeColor(this.mBottomGlowField)
        }

    }
}
