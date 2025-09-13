package com.m8test.enhancement.api.task

import com.m8test.dokka.annotation.Internal
import com.m8test.dokka.annotation.Keep
import com.m8test.script.core.api.coroutines.CoroutineScope
import com.m8test.script.core.api.coroutines.Deferred

/**
 * 任务屏幕接口。
 *
 * 表示自动化任务中的一个具体页面或状态（例如：签到页面、主页、评论区等）。
 * 包含用于识别该屏幕的匹配器（Matcher）和识别成功后执行的动作（Action）。
 *
 * @date 2025/09/01 16:40:55
 * @author M8Test, contact@m8test.com, https://m8test.com
 */
@Keep
interface TaskScreen {
    /**
     * 获取同步屏幕匹配器（内部使用）。
     *
     * @return 匹配函数。
     */
    @Internal
    fun getMatcher(): (Map<String, Any>) -> Boolean

    /**
     * 设置同步屏幕匹配器。
     *
     * 用于判断当前系统状态是否属于此屏幕。
     *
     * @param matcher 匹配函数。
     *      - 参数 `Map<String, Any>`: 由 [AutoTask.setProperties] 提供的当前屏幕属性集合。
     *      - 返回值 `Boolean`: true 表示当前正是此屏幕，随后将执行 [setAction] 设置的动作；false 表示不匹配。
     */
    @Keep
    fun setMatcher(matcher: (Map<String, Any>) -> Boolean)

    /**
     * 获取异步屏幕匹配器（内部使用）。
     *
     * @return 异步匹配函数。
     */
    @Internal
    fun getAsyncMatcher(): (CoroutineScope, Map<String, Any>) -> Deferred<Boolean>

    /**
     * 设置异步屏幕匹配器。
     *
     * @param matcher 异步匹配函数。
     *      - 参数 `scope`: 协程作用域。
     *      - 参数 `Map<String, Any>`: 当前屏幕属性集合。
     *      - 返回值 `Deferred<Boolean>`: 异步返回匹配结果。
     */
    @Keep
    fun setAsyncMatcher(matcher: (scope: CoroutineScope, Map<String, Any>) -> Deferred<Boolean>)

    /**
     * 设置当前屏幕匹配成功后需要执行的动作。
     *
     * @param actionConfig [ScreenAction] 的配置块。
     */
    @Keep
    fun setAction(actionConfig: ScreenAction.() -> Unit)

    /**
     * 获取当前屏幕配置的动作对象（内部使用）。
     *
     * 一般不需要在脚本中直接调用，由系统在匹配成功后自动调用。
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
     * 建议为每个屏幕设置唯一的名称，以便于调试和在 [ScreenAction.onPerform] 中指定跳转目标。
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