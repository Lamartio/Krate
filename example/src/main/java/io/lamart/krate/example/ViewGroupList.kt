package io.lamart.krate.example

import android.view.View
import android.view.ViewGroup

class ViewGroupList(val view: ViewGroup) : List<View> {

    private val ViewGroup.children: List<View>
        get() = 0.until(childCount).map(::getChildAt)

    override val size: Int
        get() = view.childCount

    override fun contains(element: View): Boolean = view.children.contains(element)

    override fun containsAll(elements: Collection<View>): Boolean =
            view.children.containsAll(elements)

    override fun get(index: Int): View = view.children.get(index)

    override fun indexOf(element: View): Int = view.children.indexOf(view)

    override fun isEmpty(): Boolean = view.childCount == 0

    override fun iterator(): Iterator<View> = view.children.iterator()

    override fun lastIndexOf(element: View): Int = view.children.lastIndexOf(element)

    override fun listIterator(): ListIterator<View> = view.children.listIterator()

    override fun listIterator(index: Int): ListIterator<View> = view.children.listIterator(index)

    override fun subList(fromIndex: Int, toIndex: Int): List<View> =
            view.children.subList(fromIndex, toIndex)
}