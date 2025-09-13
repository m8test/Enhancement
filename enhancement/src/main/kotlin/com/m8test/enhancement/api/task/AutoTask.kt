package com.m8test.enhancement.api.task

import com.m8test.dokka.annotation.Keep
import com.m8test.script.core.api.coroutines.CoroutineScope
import com.m8test.script.core.api.coroutines.Deferred
import com.m8test.script.core.api.engine.ScriptContextual

/**
 * 自动化任务的核心接口。
 *
 * 该接口定义了一个基于屏幕状态检测的自动化任务流程。它允许开发者定义多个 [TaskScreen]，
 * 并通过轮询的方式检查当前屏幕状态，执行相应的动作。支持同步和异步两种运行模式。
 *
 * @date 2025/09/01 16:30:26
 * @author M8Test, contact@m8test.com, https://m8test.com
 */
@Keep
interface AutoTask : ScriptContextual {
    /**
     * 设置是否开启日志记录。
     *
     * 开启后，任务运行过程中的屏幕匹配、动作执行等信息将被输出到控制台。
     *
     * @param enabled true 表示开启日志，false 表示关闭。
     */
    @Keep
    fun setLogEnabled(enabled: Boolean)

    /**
     * 设置用于屏幕匹配的属性获取器。
     *
     * 该函数会在每次屏幕检查循环中被调用，用于获取当前系统的状态或 UI 属性（如控件文本、ID、颜色等）。
     * 返回的 Map 将被传递给 [TaskScreen.setMatcher] 设置的匹配器中进行判断。
     *
     * @param properties 一个返回 Map 的函数，Map 中的 Key-Value 用于描述当前屏幕特征。
     */
    @Keep
    fun setProperties(properties: () -> Map<String, Any>)

    /**
     * 以同步方式开始运行任务。
     *
     * 任务会开启一个循环，不断获取属性并检查是否匹配已添加的任何 [TaskScreen]。
     *
     * @param interval 轮询间隔时间（毫秒）。即检查完一轮所有屏幕后，等待多久进行下一次检查。
     */
    @Keep
    fun start(interval: Long)

    /**
     * 以异步方式开始运行任务。
     *
     * @param scope 协程作用域，用于启动异步任务。
     * @param interval 轮询间隔时间（毫秒）。
     * @return 返回一个 Deferred 对象，可用于等待任务完成或取消任务。
     */
    @Keep
    fun startAsync(scope: CoroutineScope, interval: Long): Deferred<Unit>

    /**
     * 设置同步初始化器。
     *
     * 在任务循环开始之前执行一次。通常用于检查前置条件（如无障碍服务是否开启、权限是否授予等）。
     *
     * @param initializer 初始化执行函数。如果返回 false，任务将不会启动，直接结束。
     */
    @Keep
    fun setInitializer(initializer: () -> Boolean)

    /**
     * 设置异步初始化器。
     *
     * 在异步任务循环开始之前执行一次。
     *
     * @param initializer 异步初始化执行函数。如果返回 false，任务将不会启动。
     */
    @Keep
    fun setAsyncInitializer(initializer: (CoroutineScope) -> Deferred<Boolean>)

    /**
     * 设置同步终结器。
     *
     * 在任务循环结束（无论是正常停止还是被取消）后执行一次。用于资源释放或清理工作。
     *
     * @param finalizer 任务结束时执行的函数。
     */
    @Keep
    fun setFinalizer(finalizer: () -> Unit)

    /**
     * 设置异步终结器。
     *
     * 在异步任务循环结束（无论是正常停止还是被取消）后执行一次。
     *
     * @param finalizer 任务结束时执行的异步函数。
     */
    @Keep
    fun setAsyncFinalizer(finalizer: (CoroutineScope) -> Deferred<Unit>)

    /**
     * 设置任务停止条件（同步）。
     *
     * 在每一轮屏幕检查完成后调用。如果返回 true，则任务循环将终止。
     *
     * @param stopped 返回 true 表示停止任务，false 表示继续运行。
     */
    @Keep
    fun setStopped(stopped: () -> Boolean)

    /**
     * 设置任务停止条件（异步）。
     *
     * 在每一轮屏幕检查完成后调用。如果返回 true，则任务循环将终止。
     *
     * @param stopped 返回 true 表示停止任务，false 表示继续运行。
     */
    @Keep
    fun setAsyncStopped(stopped: (CoroutineScope) -> Deferred<Boolean>)

    /**
     * 停止正在运行的同步任务。
     *
     * 调用此方法将取消内部的轮询循环。
     */
    @Keep
    fun stop()

    /**
     * 停止正在运行的异步任务。
     *
     * 调用此方法将取消内部的协程任务。
     */
    @Keep
    fun stopAsync()

    /**
     * 判断同步任务是否正在运行。
     *
     * @return true 表示正在运行，false 表示未运行或已停止。
     */
    @Keep
    fun isRunning(): Boolean

    /**
     * 判断异步任务是否正在运行。
     *
     * @return true 表示正在运行，false 表示未运行或已停止。
     */
    @Keep
    fun isRunningAsync(): Boolean

    /**
     * 添加一个屏幕配置到任务中。
     *
     * @param screenConfig [TaskScreen] 的配置块，用于定义屏幕的名称、匹配规则和执行动作。
     */
    @Keep
    fun addScreen(screenConfig: TaskScreen.() -> Unit)

    /**
     * 设置屏幕缺失（未匹配）处理器。
     *
     * 当遍历完所有已添加的 [TaskScreen] 后，如果没有任何一个屏幕匹配当前状态，则会调用此回调。
     * 这通常用于处理异常状态、弹窗干扰或页面加载延迟等情况。
     *
     * @param handler 处理函数。
     *      参数 `times` 表示连续未匹配屏幕的次数。
     *      开发者可以根据 `times` 的大小决定策略（例如：次数较少时忽略，次数较多时重启 App 或执行返回操作）。
     */
    @Keep
    fun setScreenMissingHandler(handler: (times: Int) -> Unit)

    /**
     * 设置动作执行前的钩子函数。
     *
     * 当某个 [TaskScreen] 匹配成功，且准备执行其对应的 [ScreenAction] 之前，会调用此方法。
     * 可用于前置检查（如确保 App 处于前台）或日志埋点。
     *
     * @param hook 钩子函数，参数为即将执行动作的 [TaskScreen] 对象。
     */
    @Keep
    fun beforeScreenActionPerform(hook: (TaskScreen) -> Unit)

    /**
     * 设置动作执行后的钩子函数。
     *
     * 当某个 [TaskScreen] 的 [ScreenAction] 执行完毕后，会调用此方法。
     * 可用于资源回收、状态重置或后续处理。
     *
     * @param hook 钩子函数，参数为刚刚执行完动作的 [TaskScreen] 对象。
     */
    @Keep
    fun afterScreenActionPerform(hook: (TaskScreen) -> Unit)
}