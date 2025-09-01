package com.m8test.util

/**
 * Description TODO
 *
 * @date 2025/03/13 21:19:08
 * @author M8Test, contact@m8test.com, https://m8test.com
 */
object VersionUtils {
    fun getCode(name: String): Int {
        val vs = name.split(".").map { it.toInt() }
        if (vs.size != 3) error("Illegal version name $name, xxx.xxx.xxx")
        val major = vs[0]
        val minor = vs[1]
        val patch = vs[2]
        return major * 1000000 + minor * 1000 + patch
    }
}