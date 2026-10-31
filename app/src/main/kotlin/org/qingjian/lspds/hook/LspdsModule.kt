package org.qingjian.lspds.hook

import android.util.Log
import io.github.libxposed.api.XposedModule
import org.qingjian.lspds.core.HostProfile

/** Modern libxposed entry. Fail-closed until the target build is runtime-validated. */
class LspdsModule : XposedModule() {
    override fun onModuleLoaded(param: io.github.libxposed.api.XposedModuleInterface.ModuleLoadedParam) {
        log(
            Log.INFO,
            TAG,
            "Loaded process=${param.getProcessName()}, framework=${getFrameworkName()}, api=${getApiVersion()}"
        )
    }

    override fun onPackageReady(param: io.github.libxposed.api.XposedModuleInterface.PackageReadyParam) {
        if (param.getPackageName() != HostProfile.PACKAGE_NAME) return

        runCatching {
            val appInfo = param.getApplicationInfo()
            // PackageReadyParam exposes ApplicationInfo, not PackageInfo. Defer versionCode
            // resolution until a Context is available; do not guess from an ApplicationInfo.
            HostProfile.inspect(
                path = appInfo.sourceDir,
                code = -1L,
                name = "pending-context"
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
