/*
 * Copyright (c) 2018 Danny Lamarti.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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


