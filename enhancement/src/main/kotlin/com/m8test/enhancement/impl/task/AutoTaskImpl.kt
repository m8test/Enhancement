package com.m8test.enhancement.impl.task

import com.m8test.enhancement.api.task.AutoTask
import com.m8test.enhancement.api.task.TaskScreen
import com.m8test.enhancement.impl.Stack
import com.m8test.script.core.api.coroutines.CoroutineScope
import com.m8test.script.core.api.coroutines.Deferred
import com.m8test.script.core.api.coroutines.Job
import com.m8test.script.core.api.engine.ScriptContext
import com.m8test.script.core.impl.engine.AbstractScriptContextual
import kotlinx.coroutines.async
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

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

    // 默认发出一条警告信息
    private var screenMissingHandler: (Int) -> Unit = {
        scriptContext.getBindings().getConsole().warn("已经完整检测 $it 次都没有匹配任何屏幕")
    }

    override fun onScreenMissing(handler: (Int) -> Unit) {
        this.screenMissingHandler = handler
    }

    private var beforeScreenActionPerformHook: (TaskScreen) -> Unit = {
//        scriptContext.getBindings().getConsole().info("before ${it.getName()} run")
    }

    override fun beforeAction(hook: (TaskScreen) -> Unit) {
        this.beforeScreenActionPerformHook = hook
    }

    private var afterScreenActionPerformHook: (TaskScreen) -> Unit = {
//        scriptContext.getBindings().getConsole().info("after ${it.getName()} run")
    }

    override fun afterAction(hook: (TaskScreen) -> Unit) {
        this.afterScreenActionPerformHook = hook
    }

    private val stack by lazy { Stack<TaskScreen>() }

    private var missingTimes = 0
    private var previousScreen: TaskScreen? = null
    private lateinit var properties: () -> Map<String, Any>
    private var isLogEnabled = false
    override fun setLogEnabled(enabled: Boolean) {
        this.isLogEnabled = enabled
    }

    override fun provideProperties(properties: () -> Map<String, Any>) {
        this.properties = properties
    }

    override fun startAsync(scope: CoroutineScope, interval: Long): Deferred<Unit> {
        return scriptContext.getBindings().getCoroutines().wrapDeferred(
            scope = scope.getOrigin(),
            deferred = scope.getOrigin().async { runAction(interval, true) }
        )
    }

    private var initializer: (() -> Boolean)? = null
    override fun onInitialize(initializer: () -> Boolean) {
        this.initializer = initializer
    }

    private var asyncInitializer: ((CoroutineScope) -> Deferred<Boolean>)? = null
    override fun onInitializeAsync(initializer: (CoroutineScope) -> Deferred<Boolean>) {
        this.asyncInitializer = initializer
    }

    private var stopped: (() -> Boolean)? = null
    override fun stopWhen(stopped: () -> Boolean) {
        this.stopped = stopped
    }

    private var asyncStopped: ((CoroutineScope) -> Deferred<Boolean>)? = null
    override fun stopWhenAsync(stopped: (CoroutineScope) -> Deferred<Boolean>) {
        this.asyncStopped = stopped
    }

    private var asyncFinalizer: ((CoroutineScope) -> Job)? = null
    override fun onFinishAsync(finalizer: (CoroutineScope) -> Job) {
        this.asyncFinalizer = finalizer
    }

    private var finalizer: (() -> Unit)? = null
    override fun onFinish(finalizer: () -> Unit) {
        this.finalizer = finalizer
    }

    private suspend fun kotlinx.coroutines.CoroutineScope.runAction(
        interval: Long,
        isAsync: Boolean,
    ) {
        if (!isAsync) {
            // 如果任务正在运行的话那么直接返回
            if (isRunning()) {
                scriptContext.getBindings().getConsole().info("同步任务已经在运行")
                return
            }
        } else {
            // 如果任务正在运行的话那么直接返回
            if (isRunningAsync()) {
                scriptContext.getBindings().getConsole().info("异步任务已经在运行")
                return
            }
        }
        if (!this@AutoTaskImpl::properties.isInitialized) {
            scriptContext.getBindings().getConsole().info("provideProperties 方法未调用")
            return
        }
        val scope = scriptContext.getBindings().getCoroutines().wrapScope(
            kotlinx.coroutines.CoroutineScope(currentCoroutineContext())
        )
        if (isAsync) {
            asyncInitializer?.let {
                val r = it.invoke(scope).getOrigin().await()
                if (!r) {
                    scriptContext.getBindings().getConsole().info("初始化失败, 退出异步任务")
                    return
                }
            }
        } else {
            initializer?.let {
                val r = it.invoke()
                if (!r) {
                    scriptContext.getBindings().getConsole().info("初始化失败, 退出同步任务")
                    return
                }
            }
        }
        while (isActive) {
            checkScreens(isAsync, scope)
            delay(interval)
            // 如果检查到了停止条件那么就退出
            if (isAsync) {
                val r = asyncStopped?.invoke(scope)?.getOrigin()?.await() ?: false
                if (r) break
            } else {
                val r = stopped?.invoke() ?: false
                if (r) break
            }
        }
        if (isAsync) {
            asyncFinalizer?.invoke(scope)?.getOrigin()?.join()
        } else {
            finalizer?.invoke()
        }
    }

    private fun log(msg: String) {
        val console = scriptContext.getBindings().getConsole()
        if (isLogEnabled) {
            console.log(msg)
        }
    }

    private fun warn(msg: String) {
        val console = scriptContext.getBindings().getConsole()
        if (isLogEnabled) {
            console.warn(msg)
        }
    }

    private suspend fun checkScreens(isAsync: Boolean, scope: CoroutineScope) {
        // 把所有屏幕都添加到栈中，然后从栈中判断是不是某个屏幕
        for (i in screens.lastIndex downTo 0) {
            stack.push(screens[i])
        }
        var hasScreenMatched = false
        var screen: TaskScreen? = stack.pop()
        val p = this.properties()
        while (screen != null) {
            log("正在检查屏幕 ${screen.getName()}")
            val isMatch = runCatching {
                if (!isAsync) screen.getMatcher().invoke(p) else screen.getAsyncMatcher()
                    .invoke(scope, p).getOrigin().await()
            }.also { it.exceptionOrNull()?.stackTraceToString()?.let { msg -> warn(msg) } }
                .getOrNull() ?: false
            // 如果屏幕匹配，那么就需要执行里面的动作
            if (isMatch) {
                log("屏幕 ${screen.getName()} 匹配")
                beforeScreenActionPerformHook.invoke(screen)
                val performResult = runCatching {
                    if (!isAsync) screen.getAction().getPerform().invoke(previousScreen)
                    else screen.getAction().getAsyncPerform().invoke(scope, previousScreen)
                        .getOrigin().await()
                }.also { it.exceptionOrNull()?.stackTraceToString()?.let { msg -> warn(msg) } }
                    .getOrNull()
                performResult?.let { nextScreenName ->
                    log(
                        "执行动作 ${
                            screen.getAction().getName()
                        } 成功, 上一个屏幕是 ${previousScreen?.getName()}, 准备检查屏幕 $nextScreenName"
                    )
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
    }

    override fun start(interval: Long) {
        val scope = scriptContext.getBindings().getCoroutines()
            .newScope { setDispatcher { it.getScriptMain() } }
        scope.getOrigin().async { runAction(interval, false) }
    }

    override fun stop() {
        if (!isRunning()) {
            scriptContext.getBindings().getConsole().info("同步任务还没有运行，不需要停止")
            return
        }
        // 清除定时任务
        this.deferredSync?.cancel("用户取消任务")
        this.deferredSync = null
    }

    override fun stopAsync() {
        if (!isRunningAsync()) {
            scriptContext.getBindings().getConsole().info("异步任务还没有运行，不需要停止")
            return
        }
        this.deferredAsync?.cancel("用户取消任务")
        this.deferredAsync = null
    }

    private var deferredSync: Deferred<Unit>? = null
    private var deferredAsync: Deferred<Unit>? = null

    override fun isRunning(): Boolean = this.deferredSync != null
    override fun isRunningAsync(): Boolean = this.deferredAsync != null
}