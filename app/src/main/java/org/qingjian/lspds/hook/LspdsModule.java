package org.qingjian.lspds.hook;
import android.os.Build;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import io.github.libxposed.api.XposedModule;
import org.qingjian.lspds.core.HostProfile;

/** Modern libxposed entry. Initial release is observation-only and fails closed. */
public final class LspdsModule extends XposedModule {
    private static final String TAG = "Lspds";
    @Override public void onModuleLoaded(@NonNull ModuleLoadedParam param) {
        log(Log.INFO, TAG, "loaded process=" + param.getProcessName()
            + " framework=" + getFrameworkName() + " api=" + getApiVersion());
    }
    @Override @RequiresApi(Build.VERSION_CODES.Q)
    public void onPackageReady(@NonNull PackageReadyParam param) {
        if (!HostProfile.PACKAGE_NAME.equals(param.getPackageName())) return;
        try {
            HostProfile p = HostProfile.inspect(param.getApplicationInfo().sourceDir,
                param.getApplicationInfo().longVersionCode, param.getApplicationInfo().versionName);
            log(Log.INFO, TAG, "host identified: " + p);
            if (!p.isKnownStaticBaseline()) {
                log(Log.WARN, TAG, "Unknown host build; no hooks installed.");
                return;
            }
            log(Log.INFO, TAG, "Static baseline recognized; runtime hooks remain disabled pending device validation.");
        } catch (Throwable t) {
            log(Log.ERROR, TAG, "Host identification failed; module remains inert.", t);
        }
    }
}
