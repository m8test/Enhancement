package com.m8test.enhancement.impl

/**
 * 一个后进先出 (LIFO) 的栈结构，内部由 ArrayDeque 实现。
 * @param T 栈中存储的元素类型。
 */
class Stack<T> {

    // 使用 ArrayDeque 作为内部存储结构
    private val deque = ArrayDeque<T>()

    /**
     * 将一个元素压入栈顶。
     * @param element 要入栈的元素。
     */
    fun push(element: T) {
        deque.addLast(element)
    }

    /**
     * 移除并返回栈顶的元素。
     * @return 如果栈不为空，则返回栈顶元素；如果栈为空，则返回 null。
     */
    fun pop(): T? {
        return deque.removeLastOrNull()
    }

    /**
     * 查看栈顶的元素，但不将其移除。
     * @return 如果栈不为空，则返回栈顶元素；如果栈为空，则返回 null。
     */
    fun peek(): T? {
        return deque.lastOrNull()
    }

    /**
     * 检查栈是否为空。
     * @return 如果栈中没有元素，则返回 true；否则返回 false。
     */
    fun isEmpty(): Boolean {
        return deque.isEmpty()
    }

    /**
     * 返回栈中的元素数量。
     */
    val size: Int
        get() = deque.size

    /**
     * 清空栈元素
     *
     */
    fun clear() = deque.clear()

    /**
     * 提供一个更清晰的字符串表示形式。
     */
    override fun toString(): String {
        return deque.toString()
    }
}