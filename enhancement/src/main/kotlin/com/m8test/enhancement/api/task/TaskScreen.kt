package com.m8test.enhancement.api.task

import com.m8test.dokka.annotation.Internal
import com.m8test.dokka.annotation.Keep
import com.m8test.script.core.api.coroutines.CoroutineScope
import com.m8test.script.core.api.coroutines.Deferred

/**
 * 任务屏幕接口。
 *
 * 表示自动化任务中的一个具体页面或状态。
 *
 * @date 2025/09/01 16:40:55
 * @author M8Test, contact@m8test.com, https://m8test.com
 */
@Keep
interface TaskScreen {
    /**
     * 获取同步屏幕匹配逻辑（内部使用）。
     *
     * @return 匹配函数。
     */
    @Internal
    fun getMatcher(): (Map<String, Any>) -> Boolean

    /**
     * 定义同步屏幕匹配逻辑。
     *
     * @param matcher 匹配函数。
     *      - 参数 `Map<String, Any>`: 当前屏幕属性集合。
     *      - 返回值 `Boolean`: true 表示匹配当前屏幕。
     */
    @Keep
    fun match(matcher: (Map<String, Any>) -> Boolean)

    /**
     * 获取异步屏幕匹配逻辑（内部使用）。
     *
     * @return 异步匹配函数。
     */
    @Internal
    fun getAsyncMatcher(): (CoroutineScope, Map<String, Any>) -> Deferred<Boolean>

    /**
     * 定义异步屏幕匹配逻辑。
     *
     * @param matcher 异步匹配函数。
     *      - 参数 `scope`: 协程作用域。
     *      - 参数 `Map<String, Any>`: 当前屏幕属性集合。
     *      - 返回值 `Deferred<Boolean>`: 异步返回匹配结果。
     */
    @Keep
    fun matchAsync(matcher: (scope: CoroutineScope, Map<String, Any>) -> Deferred<Boolean>)

    /**
     * 配置当前屏幕匹配成功后需要执行的动作。
     *
     * @param actionConfig [ScreenAction] 的配置块。
     */
    @Keep
    fun action(actionConfig: ScreenAction.() -> Unit)

    /**
     * 获取当前屏幕配置的动作对象（内部使用）。
     *
     * @return 配置好的 [ScreenAction] 对象。
     */
    @Internal
    fun getAction(): ScreenAction

    /**
     * 获取当前屏幕所属的 [AutoTask] 实例。
     *
     * @return 所属的任务对象。
     */
    @Keep
    fun getTask(): AutoTask

    /**
     * 设置屏幕名称。
     *
     * @param name 屏幕名称。
     */
    @Keep
    fun setName(name: String)

    /**
     * 获取屏幕名称（内部使用）。
     *
     * @return 屏幕名称。
     */
    @Internal
    fun getName(): String
}