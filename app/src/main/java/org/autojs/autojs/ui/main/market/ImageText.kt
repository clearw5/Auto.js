package org.autojs.autojs.ui.main.market

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.image_text.view.*
import org.autojs.autojs.R

class ImageText : LinearLayout {

    var text: CharSequence?
        get() = textView.text
        set(value) {
            textView.text = value
        }

    constructor(context: Context?) : super(context) {
        init(null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        View.inflate(context, R.layout.image_text, this)
        gravity = Gravity.CENTER
        orientation = HORIZONTAL
        if (attrs == null) {
            return
        }
        val a = context.obtainStyledAttributes(attrs, R.styleable.ImageText)
        a.getString(R.styleable.ImageText_text)?.let {
            textView.text = it
        }
        val iconResId = a.getResourceId(R.styleable.ImageText_src, 0)
        if (iconResId != 0) {
            imageView.setImageResource(iconResId)
        }
        val imageWidth = a.getDimensionPixelSize(R.styleable.ImageText_image_width, 0)
        if (imageWidth != 0) {
            imageView.layoutParams.width = imageWidth
        }
        a.recycle()
    }

    fun setColor(color: Int) {
        textView.setTextColor(color)
        imageView.setColorFilter(color)
    }

}