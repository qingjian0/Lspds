package org.qingjian.lspds.core;
import java.io.File;
/** Conservative host identity; version metadata is not proof of DEX compatibility. */
public final class HostProfile {
    public static final String PACKAGE_NAME = "com.deepseek.chat";
    public static final long PLAY_2_7_1_VERSION_CODE = 288L;
    private final long versionCode, apkLength;
    private final String versionName;
    private HostProfile(long code, String name, long length) {
        versionCode=code; versionName=name == null ? "unknown" : name; apkLength=length;
    }
    public static HostProfile inspect(String path, long code, String name) {
        File f=path == null ? null : new File(path);
        return new HostProfile(code,name,f != null && f.isFile() ? f.length() : -1L);
    }
    public boolean isKnownStaticBaseline() { return versionCode == PLAY_2_7_1_VERSION_CODE; }
    public long getVersionCode() { return versionCode; }
    public String getVersionName() { return versionName; }
    public long getApkLength() { return apkLength; }
    @Override public String toString() {
        return "versionName="+versionName+", versionCode="+versionCode+", apkLength="+apkLength;
    }
}
