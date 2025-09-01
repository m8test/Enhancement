package com.m8test.enhancement

import com.m8test.dokka.annotation.Keep
import com.m8test.enhancement.impl.task.AutoTaskImpl
import com.m8test.plugin.api.ApkPluginProvider
import com.m8test.script.core.api.component.Variable
import com.m8test.script.core.api.engine.ScriptContext
import com.m8test.script.core.impl.component.AbstractComponent

/**
 * Description TODO
 *
 * @date 2025/01/01 12:14:31
 * @author M8Test, contact@m8test.com, https://m8test.com
 */
@Keep
class EnhancementComponent @Keep constructor(apkPluginProvider: ApkPluginProvider) :
    AbstractComponent(apkPluginProvider) {
    @Keep
    override fun getVariables(scriptContext: ScriptContext): List<Variable> {
        return listOf(AutoTaskImpl(scriptContext))
    }
}