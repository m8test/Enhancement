package com.m8test.enhancement.api.task

import com.m8test.dokka.annotation.Keep
import com.m8test.script.core.api.component.Variable

/**
 * 自动化任务工厂接口。
 *
 * 用于创建 [AutoTask] 实例的入口点。
 *
 * @date 2025/11/17 16:07:26
 * @author M8Test, contact@m8test.com, https://m8test.com
 */
@Keep
interface AutoTasks : Variable {
    /**
     * 创建一个新的自动化任务。
     *
     * @param builder [AutoTask] 的 DSL 构建器。
     * @return 返回配置好的 [AutoTask] 实例。
     */
    @Keep
    fun create(builder: (AutoTask.() -> Unit)?): AutoTask
}