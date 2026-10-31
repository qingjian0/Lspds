package org.qingjian.lspds.hook

import android.os.Build
import android.util.Log
import io.github.libxposed.api.XposedModule
import org.qingjian.lspds.core.HostProfile

/** Modern libxposed entry. Fail-closed until the target build is runtime-validated. */
class LspdsModule : XposedModule() {
    override fun onModuleLoaded(param: ModuleLoadedParam) {
        log(
            Log.INFO,
            TAG,
            "Loaded process=${param.processName}, framework=${frameworkName}, api=${apiVersion}"
        )
    }

    override fun onPackageReady(param: PackageReadyParam) {
        if (param.packageName != HostProfile.PACKAGE_NAME) return

        runCatching {
            val appInfo = param.applicationInfo
            HostProfile.inspect(
                path = appInfo.sourceDir,
                code = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) appInfo.longVersionCode else appInfo.versionCode.toLong(),
                name = appInfo.versionName
            )
        }.onSuccess { profile ->
            log(Log.INFO, TAG, "Host identified: $profile")
            if (!profile.isKnownStaticBaseline()) {
                log(Log.WARN, TAG, "Unknown DeepSeek build; no hooks installed.")
                return
            }
            log(
                Log.INFO,
                TAG,
                "Static baseline recognized; hooks remain disabled pending runtime validation."
            )
        }.onFailure { error ->
            log(Log.ERROR, TAG, "Host identification failed; module remains inert.", error)
        }
    }

    private companion object {
        const val TAG = "Lspds"
    }
}
