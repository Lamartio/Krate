package io.lamart.krate.example

import android.content.ContextWrapper
import android.view.View
import android.view.ViewGroup
import android.view.ViewManager
import android.view.ViewParent


fun <G : ViewGroup> G.children(block: Children<G>.(G) -> Unit): G =
        also { block(Children(it), it) }

class Children<G : ViewGroup>(private val owner: G) : ContextWrapper(owner.context), ViewParent by owner, ViewManager by owner, List<View> by ChildrenList(owner) {

    val context = owner.context

    operator fun <V : View> V.unaryPlus(): V =
            apply { this@Children.owner.addView(this) }

    operator fun <V : View> V.unaryMinus(): V =
            apply { this@Children.owner.removeView(this) }

}


