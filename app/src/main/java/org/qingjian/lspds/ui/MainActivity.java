package org.qingjian.lspds.ui;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
public final class MainActivity extends Activity {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        TextView v=new TextView(this);
        v.setText("Lspds 0.1.0-dev\n\nEnable this module for com.deepseek.chat in LSPosed.\n\nCurrent mode: observation-only. No chat content or credentials are collected.");
        v.setTextSize(16);
        int p=(int)(24*getResources().getDisplayMetrics().density);
        v.setPadding(p,p,p,p); setContentView(v);
    }
}
