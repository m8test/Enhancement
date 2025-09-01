package com.m8test.enhancement.impl.task

import com.m8test.enhancement.api.task.AutoTask
import com.m8test.enhancement.api.task.ScreenAction
import com.m8test.enhancement.api.task.TaskScreen

/**
 * Description TODO
 *
 * @date 2025/09/01 17:48:02
 * @author M8Test, contact@m8test.com, https://m8test.com
 */
class TaskScreenImpl(private val task: AutoTask) : TaskScreen {
    private lateinit var matcher: () -> Boolean
    override fun getMatcher(): () -> Boolean = matcher

    override fun setMatcher(matcher: () -> Boolean) {
        this.matcher = matcher
    }

    private lateinit var action: ScreenAction
    override fun setAction(actionConfig: ScreenAction.() -> Unit) {
        // 不能调用两次 setAction 方法
        if (this::action.isInitialized) error("setAction can't call twice")
        this.action = ScreenActionImpl(this)
        this.action.actionConfig()
    }

    override fun getAction(): ScreenAction = action

    override fun getTask(): AutoTask = task
    private lateinit var name: String
    override fun setName(name: String) {
        this.name = name
    }

    override fun getName(): String = name
}