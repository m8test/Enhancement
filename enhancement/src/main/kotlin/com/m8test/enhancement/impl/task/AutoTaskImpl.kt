package com.m8test.enhancement.impl.task

import com.m8test.enhancement.api.task.AutoTask
import com.m8test.enhancement.api.task.TaskScreen
import com.m8test.enhancement.impl.Stack
import com.m8test.script.core.api.engine.ScriptContext
import com.m8test.script.core.impl.engine.AbstractScriptContextual
import java.lang.reflect.Type

/**
 * Description TODO
 *
 * @date 2025/09/01 17:12:06
 * @author M8Test, contact@m8test.com, https://m8test.com
 */
class AutoTaskImpl(private val scriptContext: ScriptContext) : AutoTask,
    AbstractScriptContextual(scriptContext) {
    private val screens = mutableListOf<TaskScreen>()
    override fun addScreen(screenConfig: TaskScreen.() -> Unit) {
        val screen = TaskScreenImpl(this)
        screen.screenConfig()
        screens.add(screen)
    }

    override fun getScreens(): List<TaskScreen> = screens

    // 默认发出一条警告信息
    private var screenMissingHandler: (Int) -> Unit = {
        scriptContext.getBindings().getConsole().warn("no screen match $it times")
    }

    override fun setScreenMissingHandler(handler: (Int) -> Unit) {
        this.screenMissingHandler = handler
    }

    private var beforeScreenActionPerformHook: (TaskScreen) -> Unit = {
        scriptContext.getBindings().getConsole().info("before ${it.getName()} run")
    }

    override fun beforeScreenActionPerform(hook: (TaskScreen) -> Unit) {
        this.beforeScreenActionPerformHook = hook
    }

    private var afterScreenActionPerformHook: (TaskScreen) -> Unit = {
        scriptContext.getBindings().getConsole().info("after ${it.getName()} run")
    }

    override fun afterScreenActionPerform(hook: (TaskScreen) -> Unit) {
        this.afterScreenActionPerformHook = hook
    }

    private val stack by lazy { Stack<TaskScreen>() }

    private var intervalId: Int = -1
    private var missingTimes = 0
    private var previousScreen: TaskScreen? = null
    override fun start() {
        // 如果任务正在运行的话那么直接返回
        if (isRunning()) {
            scriptContext.getBindings().getConsole().info("task is already running")
            return
        }
        this.intervalId = scriptContext.getCurrentScript().getThreads()
            .getMain().getTimer().setInterval({
                // 把所有屏幕都添加到栈中，然后从栈中判断是不是某个屏幕
                for (i in screens.lastIndex downTo 0) {
                    stack.push(screens[i])
                }
                var hasScreenMatched = false
                var screen: TaskScreen? = stack.pop()
                while (screen != null) {
                    // 如果屏幕匹配，那么就需要执行里面的动作
                    if (screen.getMatcher().invoke()) {
                        beforeScreenActionPerformHook.invoke(screen)
                        screen.getAction().getPerform().invoke(previousScreen)
                            ?.let { nextScreenName ->
                                previousScreen = screen
                                afterScreenActionPerformHook.invoke(screen)
                                val nextScreen = screens.first { it.getName() == nextScreenName }
                                stack.push(nextScreen)
                            }
                        hasScreenMatched = true
                    }
                    // 继续判断下一个屏幕
                    screen = stack.pop()
                }
                // 如果找不到匹配的屏幕那么需要执行容错处理
                if (!hasScreenMatched) {
                    missingTimes++
                    screenMissingHandler.invoke(missingTimes)
                } else {
                    // 如果匹配了那么重置失败次数
                    missingTimes = 0
                }
            }, 1000)
    }

    override fun stop() {
        if (!isRunning()) {
            scriptContext.getBindings().getConsole().info("task is not running")
            return
        }
        // 清除定时任务
        scriptContext.getCurrentScript().getThreads().getMain().getTimer()
            .clearInterval(this.intervalId)
        this.intervalId = -1
    }

    override fun isRunning(): Boolean = this.intervalId != -1
    override fun getPublicType(): Type = AutoTask::class.java
    override fun getGlobalName(): String = "autoTask"
    override fun isPrefixRequired(): Boolean = true
    override fun isSuffixRequired(): Boolean = true
}