package com.m8test.enhancement.api.task

import com.m8test.dokka.annotation.Keep
import com.m8test.script.core.api.component.Variable

/**
 * 自动化任务工厂接口。
 *
 * 用于创建 [AutoTask] 实例的入口点。通常作为全局变量 `autoTasks` 暴露给脚本使用。
 *
 * @date 2025/11/17 16:07:26
 * @author M8Test, contact@m8test.com, https://m8test.com
 */
@Keep
interface AutoTasks : Variable {
    /**
     * 创建一个新的自动化任务。
     *
     * @param builder [AutoTask] 的配置构建器。可以在此 lambda 表达式中配置任务的初始化器、属性获取器、屏幕列表等。
     * @return 返回创建好的 [AutoTask] 实例。
     */
    @Keep
    fun create(builder: (AutoTask.() -> Unit)?): AutoTask
}