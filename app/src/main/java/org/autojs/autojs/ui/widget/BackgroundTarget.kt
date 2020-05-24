package org.autojs.autojs.ui.widget

import android.graphics.drawable.Drawable
import android.view.View
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition

class BackgroundTarget(view: View) : CustomViewTarget<View, Drawable>(view) {
    override fun onLoadFailed(errorDrawable: Drawable?) {
        errorDrawable?.let {
            view.background = it
        }
    }

    override fun onResourceCleared(placeholder: Drawable?) {
        placeholder?.let {
            view.background = it
        }
    }

    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
        view.background = resource
    }

}