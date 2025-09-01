package com.m8test.enhancement.api.task

import com.m8test.dokka.annotation.Internal
import com.m8test.dokka.annotation.Keep
import com.m8test.script.core.api.component.Variable
import com.m8test.script.core.api.engine.ScriptContextual

/**
 * 表示自动化任务的接口。
 *
 * @date 2025/09/01 16:30:26
 * @author M8Test, contact@m8test.com, https://m8test.com
 */
@Keep
interface AutoTask : ScriptContextual, Variable {
    /**
     * 开始运行任务
     *
     */
    @Keep
    fun start()

    /**
     * 停止执行任务
     *
     */
    @Keep
    fun stop()

    /**
     * 判断任务是否正在运行
     *
     * @return
     */
    @Keep
    fun isRunning(): Boolean

    @Keep
    fun addScreen(screenConfig: TaskScreen.() -> Unit)

    @Internal
    fun getScreens(): List<TaskScreen>

    /**
     * 当遍历所有屏幕发现没有任何一个屏幕匹配时会调用此方法，也就是说出现了预料之外的屏幕，这是我们需要做一些容错处理，例如可以返回或者杀死app后重启app
     *
     * @param handler 用于处理容错处理的函数，其中的参数为完全没有的次数，您可以通过这个参数来进行一些操作，当这个数值大于某个值时就重启app，而不是返回，因为返回了多次都还是一样。
     * @receiver
     */
    @Keep
    fun setScreenMissingHandler(handler: (times: Int) -> Unit)

    /**
     * 在 [TaskScreen] 执行之前回调，你可以在这里确保app已经打开，如果没有打开的话需要先打开app
     *
     * @param hook
     * @receiver
     */
    @Keep
    fun beforeScreenActionPerform(hook: (TaskScreen) -> Unit)

    /**
     * 在 [TaskScreen] 执行之后回调，你可以在这里执行一些回收资源的操作
     *
     * @param hook
     * @receiver
     */
    @Keep
    fun afterScreenActionPerform(hook: (TaskScreen) -> Unit)
}