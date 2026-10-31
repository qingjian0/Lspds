package org.qingjian.lspds.core

import java.io.File

/** Conservative host identity; version metadata alone is not proof of DEX compatibility. */
data class HostProfile private constructor(
    val versionCode: Long,
    val versionName: String,
    val apkLength: Long
) {
    fun isKnownStaticBaseline(): Boolean = versionCode == PLAY_2_7_1_VERSION_CODE

    companion object {
        const val PACKAGE_NAME = "com.deepseek.chat"
        const val PLAY_2_7_1_VERSION_CODE = 288L

        fun inspect(path: String?, code: Long, name: String?): HostProfile {
            val file = path?.let(::File)
            return HostProfile(
                versionCode = code,
                versionName = name ?: "unknown",
                apkLength = if (file?.isFile == true) file.length() else -1L
            )
        }
    }
}
