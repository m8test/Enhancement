package com.m8test.enhancement.impl.task

import com.m8test.enhancement.api.task.ScreenAction
import com.m8test.enhancement.api.task.TaskScreen

/**
 * Description TODO
 *
 * @date 2025/09/01 18:34:21
 * @author M8Test, contact@m8test.com, https://m8test.com
 */
class ScreenActionImpl(private val screen: TaskScreen) : ScreenAction {
    private lateinit var name: String
    override fun getName(): String = name

    override fun setName(name: String) {
        this.name = name
    }

    override fun getPerformTimes(): Int = times

    private var times = 0
    private lateinit var action: (TaskScreen?) -> String?
    override fun onPerform(action: (TaskScreen?) -> String?) {
        this.action = { screen ->
            val r = action(screen)
            times++
            r
        }
    }

    override fun getPerform(): (TaskScreen?) -> String? = action
    override fun getScreen(): TaskScreen = screen
}