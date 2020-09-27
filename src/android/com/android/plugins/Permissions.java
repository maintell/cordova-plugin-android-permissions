package com.android.plugins;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Application;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import static android.content.pm.PackageManager.MATCH_DEFAULT_ONLY;
import static android.net.ConnectivityManager.RESTRICT_BACKGROUND_STATUS_DISABLED;
import static android.net.ConnectivityManager.RESTRICT_BACKGROUND_STATUS_ENABLED;
import static android.net.ConnectivityManager.RESTRICT_BACKGROUND_STATUS_WHITELISTED;

/**
 * Created by JasonYang on 2016/3/11.
 */
public class Permissions extends CordovaPlugin {

    private static String TAG = "Permissions";

    private static final String ACTION_CHECK_PERMISSION = "checkPermission";
    private static final String ACTION_REQUEST_PERMISSION = "requestPermission";
    private static final String ACTION_REQUEST_PERMISSIONS = "requestPermissions";
    private static final String requestIgnoreBatteryOptimizations = "requestIgnoreBatteryOptimizations";
    private static final String isIgnoringBatteryOptimizations = "isIgnoringBatteryOptimizations";

    private static final String requestIgnoreBackgroudDataRestrictions = "requestIgnoreBackgroudDataRestrictions";
    private static final String isIgnoringBackgroudDataRestrictions = "isIgnoringBackgroudDataRestrictions";

    private static final String requestPowerSavingPolicy = "requestPowerSavingPolicy";
    private static final String getAppDetailSetting = "getAppDetailSetting";

    private static final String requestBatterySettings = "requestBatterySettings";

    private static final String verifyStoragePermissions = "verifyStoragePermissions";


    private static final String requestWriteSettingsPermission = "requestWriteSettingsPermission";
    private static final String getSystemBrightnessMode = "getSystemBrightnessMode";
    private static final String setSystemBrightnessMode = "setSystemBrightnessMode";
    private static final String setSystemBrightness = "setSystemBrightness";
    private static final String getSystemBrightness = "getSystemBrightness";

    private static final String dnd = "dnd";

    private static final String KeepScreenWakeOn = "KeepScreenWakeOn";
    private static final String clearScreenWakeOn = "clearScreenWakeOn";
    private static final String getScreenWakeStatus = "getScreenWakeStatus";

    private static final String openAppStart = "openAppStart";

    private static final int REQUEST_CODE_ENABLE_PERMISSION = 55433;
    private static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 5469; // For SYSTEM_ALERT_WINDOW

    private static final String KEY_ERROR = "error";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_RESULT_PERMISSION = "hasPermission";

    private CallbackContext permissionsCallback;


    @Override
    public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
        if (ACTION_CHECK_PERMISSION.equals(action)) {
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    checkPermissionAction(callbackContext, args);
                }
            });
            return true;
        }else if (requestWriteSettingsPermission.equals(action)) {
            cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        requestWriteSettingsPermission();
                        callbackContext.success("ok");
                    } catch (Exception e) {
                        e.printStackTrace();
                        JSONObject returnObj = new JSONObject();
                        addProperty(returnObj, KEY_ERROR, KeepScreenWakeOn);
                        addProperty(returnObj, KEY_MESSAGE, "requestWriteSettingsPermission failed.");
                        callbackContext.error(returnObj);
                        permissionsCallback = null;
                    }
                }
            });
            return true;
        }else if (setSystemBrightness.equals(action)) {
            cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        Log.d(TAG, "run: setSystemBrightness" + args.toString());
                        String argString = args.toString()
                                .replaceAll("[^\\d.]","");
                        setSystemBrightness(Float.parseFloat(argString));
                        callbackContext.success("ok");
                    } catch (Exception e) {
                        e.printStackTrace();
                        JSONObject returnObj = new JSONObject();
                        addProperty(returnObj, KEY_ERROR, KeepScreenWakeOn);
                        addProperty(returnObj, KEY_MESSAGE, "setSystemBrightness failed.");
                        callbackContext.error(returnObj);
                        permissionsCallback = null;
                    }
                }
            });
            return true;
        } else if (getSystemBrightness.equals(action)) {
            cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        float brightness = getSystemBrightness();
                        callbackContext.success(String.valueOf(brightness));
                    } catch (Exception e) {
                        e.printStackTrace();
                        JSONObject returnObj = new JSONObject();
                        addProperty(returnObj, KEY_ERROR, KeepScreenWakeOn);
                        addProperty(returnObj, KEY_MESSAGE, "getSystemBrightness failed.");
                        callbackContext.error(returnObj);
                        permissionsCallback = null;
                    }
                }
            });
            return true;
        } else if (setSystemBrightnessMode.equals(action)) {
            cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        Log.d(TAG, "run: setSystemBrightnessMode" + args.toString());
                        String argString = args.toString()
                                .replaceAll("[^\\d.]","");
                        if(argString.contains("1")){
                            setSystemBrightnessMode(1);
                        }
                        if(argString.contains("0")){
                            setSystemBrightnessMode(0);
                        }
                        callbackContext.success("ok");
                    } catch (Exception e) {
                        e.printStackTrace();
                        JSONObject returnObj = new JSONObject();
                        addProperty(returnObj, KEY_ERROR, KeepScreenWakeOn);
                        addProperty(returnObj, KEY_MESSAGE, "getSystemBrightnessMode failed.");
                        callbackContext.error(returnObj);
                        permissionsCallback = null;
                    }
                }
            });
            return true;
        } else if (getSystemBrightnessMode.equals(action)) {
            cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        int mode = getSystemBrightnessMode();
                        callbackContext.success(mode);
                    } catch (Exception e) {
                        e.printStackTrace();
                        JSONObject returnObj = new JSONObject();
                        addProperty(returnObj, KEY_ERROR, KeepScreenWakeOn);
                        addProperty(returnObj, KEY_MESSAGE, "getSystemBrightnessMode failed.");
                        callbackContext.error(returnObj);
                        permissionsCallback = null;
                    }
                }
            });
            return true;
        }else if (verifyStoragePermissions.equals(action)) {
            cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        verifyStoragePermissions();
                        callbackContext.success("ok");
                    } catch (Exception e) {
                        e.printStackTrace();
                        JSONObject returnObj = new JSONObject();
                        addProperty(returnObj, KEY_ERROR, KeepScreenWakeOn);
                        addProperty(returnObj, KEY_MESSAGE, "verifyStoragePermissions failed.");
                        callbackContext.error(returnObj);
                        permissionsCallback = null;
                    }
                }
            });
            return true;
        } else if (requestBatterySettings.equals(action)) {
            cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        requestBatterySettings();
                        callbackContext.success("ok");
                    } catch (Exception e) {
                        e.printStackTrace();
                        JSONObject returnObj = new JSONObject();
                        addProperty(returnObj, KEY_ERROR, KeepScreenWakeOn);
                        addProperty(returnObj, KEY_MESSAGE, "requestBatterySettings failed.");
                        callbackContext.error(returnObj);
                        permissionsCallback = null;
                    }
                }
            });
            return true;
        } else if (getAppDetailSetting.equals(action)) {
            cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        getAppDetailSetting();
                        callbackContext.success("ok");
                    } catch (Exception e) {
                        e.printStackTrace();
                        JSONObject returnObj = new JSONObject();
                        addProperty(returnObj, KEY_ERROR, KeepScreenWakeOn);
                        addProperty(returnObj, KEY_MESSAGE, "getScreenWakeStatus failed.");
                        callbackContext.error(returnObj);
                        permissionsCallback = null;
                    }
                }
            });
            return true;
        } else if (getScreenWakeStatus.equals(action)) {
            cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        int flag = getScreenWakeStatus();
                        callbackContext.success(flag);
                    } catch (Exception e) {
                        e.printStackTrace();
                        JSONObject returnObj = new JSONObject();
                        addProperty(returnObj, KEY_ERROR, KeepScreenWakeOn);
                        addProperty(returnObj, KEY_MESSAGE, "getScreenWakeStatus failed.");
                        callbackContext.error(returnObj);
                        permissionsCallback = null;
                    }
                }
            });
            return true;
        } else if (KeepScreenWakeOn.equals(action)) {
            cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        KeepScreenWakeOn();
                        callbackContext.success("ok");
                    } catch (Exception e) {
                        e.printStackTrace();
                        JSONObject returnObj = new JSONObject();
                        addProperty(returnObj, KEY_ERROR, KeepScreenWakeOn);
                        addProperty(returnObj, KEY_MESSAGE, "KeepScreenWakeOn failed.");
                        callbackContext.error(returnObj);
                        permissionsCallback = null;
                    }
                }
            });
            return true;
        } else if (clearScreenWakeOn.equals(action)) {
            cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        clearScreenWakeOn();
                        callbackContext.success("ok");
                    } catch (Exception e) {
                        e.printStackTrace();
                        JSONObject returnObj = new JSONObject();
                        addProperty(returnObj, KEY_ERROR, clearScreenWakeOn);
                        addProperty(returnObj, KEY_MESSAGE, "clearScreenWakeOn failed.");
                        callbackContext.error(returnObj);
                        permissionsCallback = null;
                    }
                }
            });
            return true;
        } else if (requestPowerSavingPolicy.equals(action)) {
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    try {
                        requestPowerSavingPolicy();
                        callbackContext.success("ok");
                    } catch (Exception e) {
                        e.printStackTrace();
                        JSONObject returnObj = new JSONObject();
                        addProperty(returnObj, KEY_ERROR, ACTION_REQUEST_PERMISSION);
                        addProperty(returnObj, KEY_MESSAGE, "requestPowerSavingPolicy denied.");
                        callbackContext.error(returnObj);
                        permissionsCallback = null;
                    }
                }
            });
            return true;
        } else if (openAppStart.equals(action)) {
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    try {
                        openAppStart();
                        callbackContext.success("ok");
                    } catch (Exception e) {
                        e.printStackTrace();
                        JSONObject returnObj = new JSONObject();
                        addProperty(returnObj, KEY_ERROR, ACTION_REQUEST_PERMISSION);
                        addProperty(returnObj, KEY_MESSAGE, "isIgnoringBatteryOptimizations denied.");
                        callbackContext.error(returnObj);
                        permissionsCallback = null;
                    }
                }
            });
            return true;
        } else if (dnd.equals(action)) {
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    try {

                        Log.d(TAG, "run: dnd" + args.toString());
                        String argString = args.toString();
                        if (argString.contains("on")) {
                            DNDon(callbackContext);
                        }
                        if (argString.contains("off")) {
                            DNDoff(callbackContext);
                        }
                        if (argString.contains("alarm")) {
                            DNDpartial(callbackContext);
                        }
                        if (argString.contains("priority")) {
                            DNDpriority(callbackContext);
                        }
                        if (argString.contains("status")) {
                            int status = DNDstatus();
                            callbackContext.success(status);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        JSONObject returnObj = new JSONObject();
                        addProperty(returnObj, KEY_ERROR, ACTION_REQUEST_PERMISSION);
                        addProperty(returnObj, KEY_MESSAGE, "Request permission has been denied.");
                        callbackContext.error(returnObj);
                        permissionsCallback = null;
                    }
                }
            });
            return true;
        } else if (isIgnoringBackgroudDataRestrictions.equals(action)) {
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    try {
                        boolean result = isIgnoringBackgroudDataRestrictions();
                        callbackContext.success(result ? 1 : 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                        JSONObject returnObj = new JSONObject();
                        addProperty(returnObj, KEY_ERROR, ACTION_REQUEST_PERMISSION);
                        addProperty(returnObj, KEY_MESSAGE, "isIgnoringBackgroudDataRestrictions denied.");
                        callbackContext.error(returnObj);
                        permissionsCallback = null;
                    }
                }
            });
            return true;
        } else if (requestIgnoreBackgroudDataRestrictions.equals(action)) {
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    try {
                        requestIgnoreBackgroudDataRestrictions(callbackContext, args);
                    } catch (Exception e) {
                        e.printStackTrace();
                        JSONObject returnObj = new JSONObject();
                        addProperty(returnObj, KEY_ERROR, ACTION_REQUEST_PERMISSION);
                        addProperty(returnObj, KEY_MESSAGE, "requestIgnoreBackgroudDataRestrictions denied.");
                        callbackContext.error(returnObj);
                        permissionsCallback = null;
                    }
                }
            });
            return true;
        } else if (isIgnoringBatteryOptimizations.equals(action)) {
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    try {
                        boolean result = isIgnoringBatteryOptimizations();
                        callbackContext.success(result ? 1 : 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                        JSONObject returnObj = new JSONObject();
                        addProperty(returnObj, KEY_ERROR, ACTION_REQUEST_PERMISSION);
                        addProperty(returnObj, KEY_MESSAGE, "isIgnoringBatteryOptimizations denied.");
                        callbackContext.error(returnObj);
                        permissionsCallback = null;
                    }
                }
            });
            return true;
        } else if (requestIgnoreBatteryOptimizations.equals(action)) {
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    try {
                        requestIgnoreBatteryOptimizations(callbackContext, args);
                    } catch (Exception e) {
                        e.printStackTrace();
                        JSONObject returnObj = new JSONObject();
                        addProperty(returnObj, KEY_ERROR, ACTION_REQUEST_PERMISSION);
                        addProperty(returnObj, KEY_MESSAGE, "requestIgnoreBatteryOptimizations denied.");
                        callbackContext.error(returnObj);
                        permissionsCallback = null;
                    }
                }
            });
            return true;
        } else if (ACTION_REQUEST_PERMISSION.equals(action) || ACTION_REQUEST_PERMISSIONS.equals(action)) {
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    try {
                        requestPermissionAction(callbackContext, args);
                    } catch (Exception e) {
                        e.printStackTrace();
                        JSONObject returnObj = new JSONObject();
                        addProperty(returnObj, KEY_ERROR, ACTION_REQUEST_PERMISSION);
                        addProperty(returnObj, KEY_MESSAGE, "Request permission has been denied.");
                        callbackContext.error(returnObj);
                        permissionsCallback = null;
                    }
                }
            });
            return true;
        }
        return false;
    }


    //region Screen Brightness

    private void requestWriteSettingsPermission() throws InterruptedException {
        while(!Settings.System.canWrite(cordova.getContext())) {
            if (getForegroundActivity().equals(this.cordova.getActivity().getPackageName())) {
                Activity activity = cordova.getActivity();
                PackageManager pm = activity.getPackageManager();
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                if (pm.queryIntentActivities(intent, MATCH_DEFAULT_ONLY).size() > 0) {
                    showToast(cordova.getActivity(), "自动设置亮度需要修改系统设置，请允许修改系统设置");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(Uri.parse("package:" + this.cordova.getActivity().getPackageName()));
                    intent.putExtra("package_name", this.cordova.getActivity().getPackageName());
                    activity.startActivity(intent);
                }
            }else {
                showToast(cordova.getActivity(), "自动设置亮度需要修改系统设置，请允许修改系统设置");
            }
            Thread.sleep(2000);
        }
    }

    private void setSystemBrightness(float brightness) throws InterruptedException {
        requestWriteSettingsPermission();
        setScreenManualMode();
        int brightnessMax = getBrightnessMax();
        ContentResolver contentResolver = this.cordova.getActivity().getContentResolver();
        brightness = brightness < 0.0f ? 0.0f : (brightness > 1.0f ? 1.0f : brightness);
        int value = Math.round(brightness * brightnessMax);
        Settings.System.putInt(contentResolver,
                Settings.System.SCREEN_BRIGHTNESS, value);
    }


    private float getSystemBrightness() {
        int brightnessMax = getBrightnessMax();
        return Settings.System.getInt(this.cordova.getActivity().getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS, brightnessMax) * 1.0f / brightnessMax;
    }

    private int getBrightnessMax() {
        try {
            Resources system = Resources.getSystem();
            int resId = system.getIdentifier("config_screenBrightnessSettingMaximum", "integer", "android");
            if (resId != 0) {
                return system.getInteger(resId);
            }
        }catch (Exception ignore){}
        return 255;
    }


    private boolean setSystemBrightnessMode(int isAutoMaticMode) throws Exception {
        requestWriteSettingsPermission();
        return Settings.System.putInt(this.cordova.getActivity().getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                isAutoMaticMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC ?
                        Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC :
                        Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);

    }

    private int getSystemBrightnessMode() throws Exception {
        return Settings.System.getInt(
                this.cordova.getActivity().getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE);
    }

    private void setScreenManualMode() throws InterruptedException {
        requestWriteSettingsPermission();
        ContentResolver contentResolver = this.cordova.getActivity().getContentResolver();
        try {
            int mode = Settings.System.getInt(contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE);
            if (mode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                Settings.System.putInt(contentResolver,
                        Settings.System.SCREEN_BRIGHTNESS_MODE,
                        Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }



    private int getScreenBrightness() {
        ContentResolver contentResolver = this.cordova.getActivity().getContentResolver();
        int defVal = 125;
        return Settings.System.getInt(contentResolver,
                Settings.System.SCREEN_BRIGHTNESS, defVal);
    }
    //endregion

    //region verifyStoragePermissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public void verifyStoragePermissions() {
        // Check if we have write permission
        int permission = this.cordova.getActivity().
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            showToast(cordova.getActivity(),"请始终允许存储权限");
            this.cordova.getActivity().requestPermissions(PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }
    //endregion

    //region BatterySettings
    private void requestBatterySettings() {
        Activity activity = cordova.getActivity();
        PackageManager pm = activity.getPackageManager();
        for (Intent intent : getBatteryIntents()) {
            if (pm.queryIntentActivities(intent, MATCH_DEFAULT_ONLY).size() > 0) {
                showToast(cordova.getActivity(),"请设置省电策略为无限制");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("package:" + this.cordova.getActivity().getPackageName()));
                intent.putExtra("package_name", this.cordova.getActivity().getPackageName());
                //intent.putExtra("package_label", this.cordova.getActivity().getPackageManager().get);
                activity.startActivity(intent);
                break;
            }
        }
    }

    private List<Intent> getBatteryIntents() {
        return Arrays.asList(
                // 小米
                new Intent().setComponent(ComponentName.unflattenFromString("com.miui.powerkeeper/.ui.HiddenAppsContainerManagementActivity")),
                // 华为
                new Intent().setComponent(ComponentName.unflattenFromString("com.huawei.systemmanager/.power.ui.HwPowerManagerActivity")),

                // 魅族
                new Intent().setComponent(ComponentName.unflattenFromString("com.meizu.safe/.SecurityCenterActivity")),
                // 三星
                new Intent().setComponent(new ComponentName("com.samsung.android.sm_cn", "com.samsung.android.sm.ui.battery.AppSleepListActivity")),
                new Intent().setComponent(new ComponentName("com.samsung.android.sm_cn", "com.samsung.android.sm.ui.battery.BatteryActivity")),
                new Intent().setComponent(new ComponentName("com.samsung.android.sm", "com.samsung.android.sm.ui.battery.AppSleepListActivity")),
                new Intent().setComponent(new ComponentName("com.samsung.android.sm", "com.samsung.android.sm.ui.battery.BatteryActivity")),
                new Intent().setComponent(new ComponentName("com.samsung.android.lool", "com.samsung.android.sm.battery.ui.BatteryActivity")),
                new Intent().setComponent(new ComponentName("com.samsung.android.lool", "com.samsung.android.sm.ui.battery.BatteryActivity")),
                new Intent().setComponent(new ComponentName("com.samsung.android.sm", "com.samsung.android.sm.ui.battery.BatteryActivity")),
                new Intent().setComponent(new ComponentName("com.samsung.android.sm_cn", "com.samsung.android.sm.ui.cstyleboard.SmartManagerDashBoardActivity")),
                // oppo
                new Intent().setComponent(ComponentName.unflattenFromString("com.coloros.safecenter/.appfrozen.activity.AppFrozenSettingsActivity")),
                new Intent().setComponent(new ComponentName("com.coloros.oppoguardelf", "com.coloros.powermanager.fuelgaue.PowerUsageModelActivity")),
                new Intent().setComponent(new ComponentName("com.coloros.oppoguardelf", "com.coloros.powermanager.fuelgaue.PowerSaverModeActivity")),
                new Intent().setComponent(new ComponentName("com.coloros.oppoguardelf", "com.coloros.powermanager.fuelgaue.PowerConsumptionActivity")),
                new Intent().setComponent(ComponentName.unflattenFromString("com.oppo.safe/.SecureSafeMainActivity")),
                // vivo
                new Intent().setComponent(new ComponentName("com.vivo.abe", "com.vivo.applicationbehaviorengine.ui.ExcessivePowerManagerActivity")),
                new Intent().setComponent(ComponentName.unflattenFromString("com.iqoo.powersaving/.PowerSavingManagerActivity"))
        );
    }
    //endregion

    private void getAppDetailSetting() {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", this.cordova.getActivity().getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", this.cordova.getActivity().getPackageName());
        }

        showToast(cordova.getActivity(),"请允许自启动，允许后台运行，设置后台运行无限制！");
        localIntent.setData(Uri.parse("package:" + this.cordova.getActivity().getPackageName()));
        localIntent.putExtra("package_name", this.cordova.getActivity().getPackageName());
        //intent.putExtra("package_label", this.cordova.getActivity().getPackageManager().get);
        this.cordova.getActivity().startActivity(localIntent);

    }

    //region PowerSavingPolicy
    private void requestPowerSavingPolicy() {
        Activity activity = cordova.getActivity();
        PackageManager pm = activity.getPackageManager();
        for (Intent intent : getPowerSavingPolicyIntents()) {
            if (pm.queryIntentActivities(intent, MATCH_DEFAULT_ONLY).size() > 0) {
                showToast(cordova.getActivity(),"请设置省电策略为无限制");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("package:" + this.cordova.getActivity().getPackageName()));
                intent.putExtra("package_name", this.cordova.getActivity().getPackageName());
                //intent.putExtra("package_label", this.cordova.getActivity().getPackageManager().get);
                activity.startActivity(intent);
                break;
            }
        }
    }

    private List<Intent> getPowerSavingPolicyIntents() {

        return Arrays.asList(
                new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity")),
                new Intent().setComponent(new ComponentName("com.miui.powerkeeper", "com.miui.powerkeeper.ui.HiddenAppsConfigActivity"))
        );
    }
    //endregion

    //region IgnoringBackgroudDataRestrictions
    private boolean isIgnoringBackgroudDataRestrictions() {
        boolean isIgnoring = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ConnectivityManager connMgr = (ConnectivityManager)
                    this.cordova.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connMgr.isActiveNetworkMetered()) {
                // Checks user’s Data Saver settings.
                switch (connMgr.getRestrictBackgroundStatus()) {
                    case RESTRICT_BACKGROUND_STATUS_ENABLED:
                        isIgnoring = false;
                        // Background data usage is blocked for this app. Wherever possible,
                        // the app should also use less data in the foreground.
                    case RESTRICT_BACKGROUND_STATUS_WHITELISTED:
                        isIgnoring = true;
                        // The app is whitelisted. Wherever possible,
                        // the app should use less data in the foreground and background.
                    case RESTRICT_BACKGROUND_STATUS_DISABLED:
                        isIgnoring = true;
                        // Data Saver is disabled. Since the device is connected to a
                        // metered network, the app should use less data wherever possible.
                }
            } else {
                isIgnoring = true;
                // The device is not on a metered network.
                // Use data as required to perform syncs, downloads, and updates.
            }
        } else {
            isIgnoring = true;
        }

        return isIgnoring;
    }

    private void requestIgnoreBackgroudDataRestrictions(CallbackContext callbackContext, JSONArray permissions) throws Exception {
        while (!isIgnoringBackgroudDataRestrictions()) {
            if (getForegroundActivity().equals(this.cordova.getActivity().getPackageName())) {
                showToast(cordova.getActivity(), "请允许忽略后台数据限制！");
                Intent intent = new Intent(Settings.ACTION_IGNORE_BACKGROUND_DATA_RESTRICTIONS_SETTINGS);
                intent.setData(Uri.parse("package:" + this.cordova.getActivity().getPackageName()));
                this.cordova.getActivity().startActivity(intent);
            } else {
                showToast(cordova.getActivity(), "请允许忽略后台数据限制！");
            }
            Thread.sleep(2000);
        }
        callbackContext.success("OK");
    }
    //endregion

    //region ScreenWakeOn
    private int getScreenWakeStatus() {
        int flag = this.cordova.getActivity().getWindow().getAttributes().flags;
        //int bitValue = getBit(flag,7);
        return (flag & (1 << 7)) > 0 ? 1 : 0;
    }

    private void KeepScreenWakeOn() {
        this.cordova.getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void clearScreenWakeOn() {
        this.cordova.getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
    //endregion

    /**
     * Opens the system settings dialog where the user can tweak or turn off any
     * custom app start settings added by the manufacturer if available.
     */
    private void openAppStart() {
        Activity activity = cordova.getActivity();
        PackageManager pm = activity.getPackageManager();
        for (Intent intent : getAppStartIntents()) {
            if (pm.resolveActivity(intent, MATCH_DEFAULT_ONLY) != null) {
                showToast(cordova.getActivity(),"请允许自启动权限，锁屏不清理应用！");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
                break;
            }
        }
    }

    private List<Intent> getNetAppINtents() {

        return Arrays.asList(
                new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.netassistant.netapp.ui.NetAppListActivity")),
                new Intent().setComponent(new ComponentName("com.miui.securitycenter", "com.miui.networkassistant.ui.activity.FirewallActivity"))
        );
    }

    private List<Intent> getAppStartIntents() {
        return Arrays.asList(
                new Intent().setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")),
                new Intent().setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity")),
                new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")),
                new Intent().setComponent(new ComponentName("com.huawei.systemmanager", Build.VERSION.SDK_INT >= Build.VERSION_CODES.P ? "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity" : "com.huawei.systemmanager.appcontrol.activity.StartupAppControlActivity")),
                new Intent().setComponent(new ComponentName("com.coloros.oppoguardelf", "com.coloros.powermanager.fuelgaue.PowerUsageModelActivity")),
                new Intent().setComponent(new ComponentName("com.coloros.oppoguardelf", "com.coloros.powermanager.fuelgaue.PowerSaverModeActivity")),
                new Intent().setComponent(new ComponentName("com.coloros.oppoguardelf", "com.coloros.powermanager.fuelgaue.PowerConsumptionActivity")),
                new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity")),
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ? new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity")).setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).setData(Uri.parse("package:" + this.cordova.getContext().getPackageName())) : null,
                new Intent().setComponent(new ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity")),
                new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity")),
                new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager")),
                new Intent().setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity")),
                new Intent().setComponent(new ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.autostart.AutoStartActivity")),
                new Intent().setComponent(new ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.entry.FunctionActivity")).setData(android.net.Uri.parse("mobilemanager://function/entry/AutoStart")),
                new Intent().setAction("com.letv.android.permissionautoboot"),
                new Intent().setComponent(new ComponentName("com.samsung.android.sm_cn", "com.samsung.android.sm.ui.ram.AutoRunActivity")),
                new Intent().setComponent(ComponentName.unflattenFromString("com.iqoo.secure/.MainActivity")),
                new Intent().setComponent(ComponentName.unflattenFromString("com.meizu.safe/.permission.SmartBGActivity")),
                new Intent().setComponent(new ComponentName("com.yulong.android.coolsafe", ".ui.activity.autorun.AutoRunListActivity")),
                new Intent().setComponent(new ComponentName("cn.nubia.security2", "cn.nubia.security.appmanage.selfstart.ui.SelfStartActivity")),
                new Intent().setComponent(new ComponentName("com.zui.safecenter", "com.lenovo.safecenter.MainTab.LeSafeMainActivity"))
        );
    }

    //region Do not disturb
    private int DNDstatus() throws Exception {
        NotificationManager notificationManager = (NotificationManager) this.cordova.getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            while (!notificationManager.isNotificationPolicyAccessGranted()) {
                if (getForegroundActivity().equals(this.cordova.getActivity().getPackageName())) {
                    showToast(cordova.getActivity(), "请允许“勿扰“ 权限！");
                    Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                    this.cordova.getActivity().startActivity(intent);
                }
                Thread.sleep(2000);
            }
            int current = notificationManager.getCurrentInterruptionFilter();
            return current;
        } else {
            return -1;
        }
    }


    private void DNDon(CallbackContext callbackContext) throws Exception {
        changeFilter(NotificationManager.INTERRUPTION_FILTER_NONE, callbackContext, 5);
    }

    private void DNDoff(CallbackContext callbackContext) throws Exception {
        changeFilter(NotificationManager.INTERRUPTION_FILTER_ALL, callbackContext, 2);
    }

    private void DNDpartial(CallbackContext callbackContext) throws Exception {
        changeFilter(NotificationManager.INTERRUPTION_FILTER_ALARMS, callbackContext, 3);

    }

    private void DNDpriority(CallbackContext callbackContext) throws Exception {
        changeFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY, callbackContext, 4);
    }

    private void changeFilter(int filter, CallbackContext callbackContext, int status) throws Exception {
        NotificationManager notificationManager = (NotificationManager) this.cordova.getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            while (!notificationManager.isNotificationPolicyAccessGranted()) {
                if (getForegroundActivity().equals(this.cordova.getActivity().getPackageName())) {
                    showToast(cordova.getActivity(), "请允许“勿扰“ 权限！");
                    Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                    this.cordova.getActivity().startActivity(intent);
                }
                Thread.sleep(2000);
            }
            notificationManager.setInterruptionFilter(filter);
        } else {
            callbackContext.error(0);
        }

    }
    //endregion

    /**
     * 跳转到指定应用的首页
     */
    private void showActivity(String packageName) {
        Intent intent = this.cordova.getActivity().getPackageManager().getLaunchIntentForPackage(packageName);
        this.cordova.getActivity().startActivity(intent);
    }

    /**
     * 跳转到指定应用的指定页面
     */
    private void showActivity(String packageName, String activityDir) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(packageName, activityDir));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.cordova.getActivity().startActivity(intent);
    }


    public static void showToast(final Activity ctx, final String msg) {
        // 判断是在子线程，还是主线程
        if ("main".equals(Thread.currentThread().getName())) {
            Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
        } else {
            // 子线程
            ctx.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    // @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isIgnoringBatteryOptimizations() {
        boolean isIgnoring = false;
        PowerManager powerManager = (PowerManager) this.cordova.getActivity().getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            isIgnoring = powerManager.isIgnoringBatteryOptimizations(this.cordova.getActivity().getPackageName());
        }
        return isIgnoring;
    }

    public void requestIgnoreBatteryOptimizations(CallbackContext callbackContext, JSONArray permissions) throws Exception {
        while (!isIgnoringBatteryOptimizations()) {
            if (getForegroundActivity().equals(this.cordova.getActivity().getPackageName())) {
                showToast(cordova.getActivity(), "请允许忽略电池优化！");
                Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + this.cordova.getActivity().getPackageName()));
                this.cordova.getActivity().startActivity(intent);
            } else {
                showToast(cordova.getActivity(), "请允许忽略电池优化！");
            }
            Thread.sleep(2000);
        }
        callbackContext.success("OK");
    }

    public String getForegroundActivity() {
        ActivityManager mActivityManager =
                (ActivityManager) this.cordova.getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        if (mActivityManager.getRunningTasks(1) == null) {
            Log.e(TAG, "running task is null, ams is abnormal!!!");
            return null;
        }
        ActivityManager.RunningTaskInfo mRunningTask =
                mActivityManager.getRunningTasks(1).get(0);
        if (mRunningTask == null) {
            Log.e(TAG, "failed to get RunningTaskInfo");
            return null;
        }

        String pkgName = mRunningTask.topActivity.getPackageName();
        //String activityName =  mRunningTask.topActivity.getClassName();
        Log.d(TAG, "getForegroundActivity: " + pkgName);
        return pkgName;
    }


    @Override
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
        if (permissionsCallback == null) {
            return;
        }

        JSONObject returnObj = new JSONObject();
        if (permissions != null && permissions.length > 0) {
            //Call checkPermission again to verify
            boolean hasAllPermissions = hasAllPermissions(permissions);
            addProperty(returnObj, KEY_RESULT_PERMISSION, hasAllPermissions);
            permissionsCallback.success(returnObj);
        } else {
            addProperty(returnObj, KEY_ERROR, ACTION_REQUEST_PERMISSION);
            addProperty(returnObj, KEY_MESSAGE, "Unknown error.");
            permissionsCallback.error(returnObj);
        }
        permissionsCallback = null;
    }

    private void checkPermissionAction(CallbackContext callbackContext, JSONArray permission) {
        if (permission == null || permission.length() == 0 || permission.length() > 1) {
            JSONObject returnObj = new JSONObject();
            addProperty(returnObj, KEY_ERROR, ACTION_CHECK_PERMISSION);
            addProperty(returnObj, KEY_MESSAGE, "One time one permission only.");
            callbackContext.error(returnObj);
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            JSONObject returnObj = new JSONObject();
            addProperty(returnObj, KEY_RESULT_PERMISSION, true);
            callbackContext.success(returnObj);
        } else {
            String permission0;
            try {
                permission0 = permission.getString(0);
            } catch (JSONException ex) {
                JSONObject returnObj = new JSONObject();
                addProperty(returnObj, KEY_ERROR, ACTION_REQUEST_PERMISSION);
                addProperty(returnObj, KEY_MESSAGE, "Check permission has been failed." + ex);
                callbackContext.error(returnObj);
                return;
            }
            JSONObject returnObj = new JSONObject();
            if ("android.permission.SYSTEM_ALERT_WINDOW".equals(permission0)) {
                Context context = this.cordova.getActivity().getApplicationContext();
                addProperty(returnObj, KEY_RESULT_PERMISSION, Settings.canDrawOverlays(context));
            } else {
                addProperty(returnObj, KEY_RESULT_PERMISSION, cordova.hasPermission(permission0));
            }
            callbackContext.success(returnObj);
        }
    }

    private void requestPermissionAction(CallbackContext callbackContext, JSONArray permissions) throws Exception {
        if (permissions == null || permissions.length() == 0) {
            JSONObject returnObj = new JSONObject();
            addProperty(returnObj, KEY_ERROR, ACTION_REQUEST_PERMISSION);
            addProperty(returnObj, KEY_MESSAGE, "At least one permission.");
            callbackContext.error(returnObj);
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            JSONObject returnObj = new JSONObject();
            addProperty(returnObj, KEY_RESULT_PERMISSION, true);
            callbackContext.success(returnObj);
        } else if (hasAllPermissions(permissions)) {
            JSONObject returnObj = new JSONObject();
            addProperty(returnObj, KEY_RESULT_PERMISSION, true);
            callbackContext.success(returnObj);
        } else {
            permissionsCallback = callbackContext;
            String[] permissionArray = getPermissions(permissions);
            if (permissionArray.length == 1 && "android.permission.SYSTEM_ALERT_WINDOW".equals(permissionArray[0])) {
                Log.i(TAG, "Request permission SYSTEM_ALERT_WINDOW");

                Activity activity = this.cordova.getActivity();
                Context context = this.cordova.getActivity().getApplicationContext();

                // SYSTEM_ALERT_WINDOW
                // https://stackoverflow.com/questions/40355344/how-to-programmatically-grant-the-draw-over-other-apps-permission-in-android
                // https://www.codeproject.com/Tips/1056871/Android-Marshmallow-Overlay-Permission
                if (!Settings.canDrawOverlays(context)) {
                    Log.w(TAG, "Request permission SYSTEM_ALERT_WINDOW start intent because canDrawOverlays=false");
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + activity.getPackageName()));
                    activity.startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
                    return;
                }
            }
            cordova.requestPermissions(this, REQUEST_CODE_ENABLE_PERMISSION, permissionArray);
        }
    }

    private String[] getPermissions(JSONArray permissions) {
        String[] stringArray = new String[permissions.length()];
        for (int i = 0; i < permissions.length(); i++) {
            try {
                stringArray[i] = permissions.getString(i);
            } catch (JSONException ignored) {
                //Believe exception only occurs when adding duplicate keys, so just ignore it
            }
        }
        return stringArray;
    }

    private boolean hasAllPermissions(JSONArray permissions) throws JSONException {
        return hasAllPermissions(getPermissions(permissions));
    }

    private boolean hasAllPermissions(String[] permissions) throws JSONException {

        for (String permission : permissions) {
            if (!cordova.hasPermission(permission)) {
                return false;
            }
        }

        return true;
    }

    private void addProperty(JSONObject obj, String key, Object value) {
        try {
            if (value == null) {
                obj.put(key, JSONObject.NULL);
            } else {
                obj.put(key, value);
            }
        } catch (JSONException ignored) {
            //Believe exception only occurs when adding duplicate keys, so just ignore it
        }
    }

}
