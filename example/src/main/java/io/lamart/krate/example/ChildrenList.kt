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