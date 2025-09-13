package com.m8test.enhancement.impl.task

import com.m8test.enhancement.api.task.AutoTask
import com.m8test.enhancement.api.task.AutoTasks
import com.m8test.script.core.api.engine.ScriptContext
import java.lang.reflect.Type

/**
 * Description TODO
 *
 * @date 2025/11/17 16:16:20
 * @author M8Test, contact@m8test.com, https://m8test.com
 */
class AutoTasksImpl(private val scriptContext: ScriptContext) : AutoTasks {
    override fun getPublicType(): Type = AutoTasks::class.java
    override fun getGlobalName(): String = "autoTasks"
    override fun isPrefixRequired(): Boolean = true
    override fun isSuffixRequired(): Boolean = true
    override fun create(builder: (AutoTask.() -> Unit)?): AutoTask {
        val r = AutoTaskImpl(scriptContext)
        builder?.invoke(r)
        return r
    }
}