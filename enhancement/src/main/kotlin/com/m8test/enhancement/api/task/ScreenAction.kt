package com.m8test.enhancement.api.task

import com.m8test.dokka.annotation.Internal
import com.m8test.dokka.annotation.Keep

/**
 * Description TODO
 *
 * @date 2025/09/01 16:52:20
 * @author M8Test, contact@m8test.com, https://m8test.com
 */
@Keep
interface ScreenAction {
    @Internal
    fun getName(): String

    /**
     * 设置动作名称，例如点击搜索按钮，输入评论等。
     *
     * @param name
     */
    @Keep
    fun setName(name: String)

    /**
     * 获取 [onPerform] 设置的函数执行的次数
     *
     * @return
     */
    @Keep
    fun getPerformTimes(): Int

    /**
     * 设置动作被执行时需要执行的函数
     *
     * @param action 需要执行的函数，其参数是上一个处理完成的屏幕, 可以理解为从哪个屏幕跳转到当前屏幕的, 返回值表示下一个需要判断的时哪个屏幕，也就是操作后可能跳转到的屏幕, 这样可以让脚本更高效的运行
     * @receiver
     */
    @Keep
    fun onPerform(action: (previousScreen: TaskScreen?) -> String?)

    @Internal
    fun getPerform(): (TaskScreen?) -> String?

    /**
     * 获取动作所属的 [TaskScreen]
     *
     * @return
     */
    @Keep
    fun getScreen(): TaskScreen
}