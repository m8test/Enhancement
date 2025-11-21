package com.m8test.enhancement.api.task

import com.m8test.dokka.annotation.Internal
import com.m8test.dokka.annotation.Keep
import com.m8test.script.core.api.coroutines.CoroutineScope
import com.m8test.script.core.api.coroutines.Deferred

/**
 * 屏幕动作接口。
 *
 * 定义了当 [TaskScreen] 匹配成功后需要执行的具体操作逻辑。
 *
 * @date 2025/09/01 16:52:20
 * @author M8Test, contact@m8test.com, https://m8test.com
 */
@Keep
interface ScreenAction {
    /**
     * 获取动作名称（内部使用）。
     *
     * @return 动作的名称。
     */
    @Internal
    fun getName(): String

    /**
     * 设置动作名称。
     *
     * 用于标识该动作的用途，便于日志记录和调试。
     *
     * @param name 动作名称。
     */
    @Keep
    fun setName(name: String)

    /**
     * 获取该动作累计被执行的次数。
     *
     * @return 累计执行次数。
     */
    @Keep
    fun getExecutionCount(): Int

    /**
     * 定义同步执行动作的逻辑。
     *
     * @param action 执行函数。
     *      - 参数 `previousScreen`: 上一个匹配并执行过的屏幕对象（可能为 null）。
     *      - 返回值 `String?`: 下一个建议检查的屏幕名称。
     */
    @Keep
    fun perform(action: (previousScreen: TaskScreen?) -> String?)

    /**
     * 获取同步执行动作的函数（内部使用）。
     *
     * @return 执行动作的函数引用。
     */
    @Internal
    fun getPerform(): (TaskScreen?) -> String?

    /**
     * 定义异步执行动作的逻辑。
     *
     * @param action 异步执行函数。
     *      - 参数 `CoroutineScope`: 协程作用域。
     *      - 参数 `previousScreen`: 上一个匹配并执行过的屏幕对象。
     *      - 返回值 `Deferred<String?>`: 包含下一个建议检查屏幕名称的 Deferred 对象。
     */
    @Keep
    fun performAsync(action: (CoroutineScope, previousScreen: TaskScreen?) -> Deferred<String?>)

    /**
     * 获取异步执行动作的函数（内部使用）。
     *
     * @return 异步执行动作的函数引用。
     */
    @Internal
    fun getAsyncPerform(): (CoroutineScope, TaskScreen?) -> Deferred<String?>

    /**
     * 获取该动作所属的 [TaskScreen]。
     *
     * @return 关联的屏幕对象。
     */
    @Keep
    fun getScreen(): TaskScreen
}