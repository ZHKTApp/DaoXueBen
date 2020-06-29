package com.zwyl.guide.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class PermissionMUtil {
	
	public static final int REQUEST_CODE_ASK_PERMISSIONS = 123;
	@SuppressLint("InlinedApi")
	public static final String PER_READ_EXT = Manifest.permission.READ_EXTERNAL_STORAGE;
	public static final String PER_WRITE_EXT = Manifest.permission.WRITE_EXTERNAL_STORAGE;
	public static final String PER_CALL_PHONE = Manifest.permission.CALL_PHONE;
	public static final String PER_ACC_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
	public static final String PER_ACC_READ_PHONE = Manifest.permission.READ_PHONE_STATE;
	public static final String PER_RECORD_AUDIO = Manifest.permission.RECORD_AUDIO;

	/** 检查安卓6.0(M)是否有权限， true表示有权限， false表示失败或无权限 */
	public static boolean checkMPermission(Context ctx, String permName) {
		try {
			int permission = ContextCompat.checkSelfPermission(ctx, permName);
		    return permission == PackageManager.PERMISSION_GRANTED;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/** 获取单个权限 */
	public static void requestMPermission(Activity act, String permName) {
		ActivityCompat.requestPermissions(act, new String[] {permName}, REQUEST_CODE_ASK_PERMISSIONS);
	}

	/** 获取多个权限 */
	public static void requestMPermission(Activity act, String[] permName) {
		ActivityCompat.requestPermissions(act, permName, REQUEST_CODE_ASK_PERMISSIONS);
	}

	public static boolean checkMPermission_ReadExtStorage(Context ctx) {
		return checkMPermission(ctx, PER_READ_EXT);
	}

	public static boolean checkMPermission_WriteExtStorage(Context ctx) {
		return checkMPermission(ctx, PER_WRITE_EXT);
	}
	
	public static boolean checkMPermission_CallPhone(Context ctx) {
		return checkMPermission(ctx, PER_CALL_PHONE);
	}
	
	public static boolean checkMPermission_Location(Context ctx) {
		return checkMPermission(ctx, PER_ACC_LOCATION);
	}

	public static boolean checkMPermission_ReadPhoneState(Context ctx) {
		return checkMPermission(ctx, PER_ACC_READ_PHONE);
	}
	
	public static void requestMPermission_ReadExtStorage(Activity act) {
		requestMPermission(act, PER_READ_EXT);
	}
	
	public static void requestMPermission_WriteExtStorage(Activity act) {
		requestMPermission(act, PER_WRITE_EXT);
	}
	
	public static void requestMPermission_CallPhone(Activity act) {
		requestMPermission(act, PER_CALL_PHONE);
	}
	
	public static void requestMPermission_Location(Activity act) {
		requestMPermission(act, PER_ACC_LOCATION);
	}

	public static void requestMPermission_ReadPhoneState(Activity act) {
		requestMPermission(act, PER_ACC_READ_PHONE);
	}
}
