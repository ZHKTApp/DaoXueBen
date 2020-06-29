package com.zwyl.guide.util;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class UtilPermission {

    public static boolean checkPermission(Activity activity, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> pList = new ArrayList<>();
            for (String permission : permissions) {
                if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(activity, permission)) {
                    Log.e("UtilPermission", permission);
                    pList.add(permission);
                }
            }

            if (pList.size() != 0) {
                ActivityCompat.requestPermissions(activity, pList.toArray(new String[pList.size()]), 0);
            } else {
                return true;
            }
        }
        return false;

    }

}
