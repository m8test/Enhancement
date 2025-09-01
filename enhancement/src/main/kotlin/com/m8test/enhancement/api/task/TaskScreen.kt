package com.m8test.enhancement.api.task

import com.m8test.dokka.annotation.Internal
import com.m8test.dokka.annotation.Keep

/**
 * 表示一个任务的屏幕，例如签到屏幕，评论屏幕等。
 *
 * @date 2025/09/01 16:40:55
 * @author M8Test, contact@m8test.com, https://m8test.com
 */
@Keep
interface TaskScreen {
    /**
     * 获取屏幕匹配器
     *
     * @return
     */
    @Internal
    fun getMatcher(): () -> Boolean

    /**
     * 设置屏幕匹配器，用于判断是不是匹配当前屏幕，如果返回true就表示是当前屏幕，那么就会执行[setAction]设置的动作
     *
     * @param matcher
     * @receiver
     */
    @Keep
    fun setMatcher(matcher: () -> Boolean)

    /**
     * 设置当前屏幕需要执行的动作
     *
     * @param actionConfig
     * @receiver
     */
    @Keep
    fun setAction(actionConfig: ScreenAction.() -> Unit)

    /**
     * 获取需要执行的动作，一般不需要在脚本中调用，由系统自动调用，可以通过 [setAction] 添加
     *
     * @return
     */
    @Internal
    fun getAction(): ScreenAction

    /**
     * 获取当前屏幕所属的 [AutoTask]
     *
     * @return
     */
    @Keep
    fun getTask(): AutoTask

    @Keep
    fun setName(name: String)

    @Internal
    fun getName(): String
}