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

import android.view.View
import android.view.ViewGroup

class ChildrenList(val parent: ViewGroup) : List<View> {

    private val children: List<View>
        get() = 0.until(parent.childCount).map(parent::getChildAt)

    override val size: Int
        get() = parent.childCount

    override fun contains(element: View): Boolean = children.contains(element)

    override fun containsAll(elements: Collection<View>): Boolean =
            children.containsAll(elements)

    override fun get(index: Int): View = children.get(index)

    override fun indexOf(element: View): Int = children.indexOf(parent)

    override fun isEmpty(): Boolean = size == 0

    override fun iterator(): Iterator<View> = children.iterator()

    override fun lastIndexOf(element: View): Int = children.lastIndexOf(element)

    override fun listIterator(): ListIterator<View> = children.listIterator()

    override fun listIterator(index: Int): ListIterator<View> = children.listIterator(index)

    override fun subList(fromIndex: Int, toIndex: Int): List<View> =
            children.subList(fromIndex, toIndex)
}