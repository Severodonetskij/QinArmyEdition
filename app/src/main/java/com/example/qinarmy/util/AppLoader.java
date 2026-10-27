package com.example.qinarmy.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import com.example.qinarmy.model.AppInfo;
import java.util.ArrayList;
import java.util.List;

public class AppLoader {
    public static List<AppInfo> loadLaunchableApps(Context ctx) {
        PackageManager pm = ctx.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolved = pm.queryIntentActivities(intent, 0);
        List<AppInfo> result = new ArrayList<>();
        String self = ctx.getPackageName();
        for (ResolveInfo ri : resolved) {
            String pkg = ri.activityInfo.packageName;
            if (pkg.equals(self)) continue;
            result.add(new AppInfo(
                    ri.loadLabel(pm).toString(),
                    pkg,
                    ri.loadIcon(pm)));
        }
        return result;
    }
}
