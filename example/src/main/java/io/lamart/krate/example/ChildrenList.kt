package io.lamart.krate.example

import android.view.View
import android.view.ViewGroup

class ChildrenList(val parent: ViewGroup) : List<View> {

    private val ViewGroup.children: List<View>
        get() = 0.until(childCount).map(::getChildAt)

    override val size: Int
        get() = parent.childCount

    override fun contains(element: View): Boolean = parent.children.contains(element)

    override fun containsAll(elements: Collection<View>): Boolean =
            parent.children.containsAll(elements)

    override fun get(index: Int): View = parent.children.get(index)

    override fun indexOf(element: View): Int = parent.children.indexOf(parent)

    override fun isEmpty(): Boolean = parent.childCount == 0

    override fun iterator(): Iterator<View> = parent.children.iterator()

    override fun lastIndexOf(element: View): Int = parent.children.lastIndexOf(element)

    override fun listIterator(): ListIterator<View> = parent.children.listIterator()

    override fun listIterator(index: Int): ListIterator<View> = parent.children.listIterator(index)

    override fun subList(fromIndex: Int, toIndex: Int): List<View> =
            parent.children.subList(fromIndex, toIndex)
}