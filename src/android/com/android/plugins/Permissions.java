package com.android.plugins;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Application;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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

    private static final String dnd = "dnd";

    private static final String KeepScreenWakeOn = "KeepScreenWakeOn";
    private static final String clearScreenWakeOn = "clearScreenWakeOn";

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
        }else if (openAppStart.equals(action)) {
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
        }else if (dnd.equals(action)) {
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    try {

                        Log.d(TAG, "run: dnd" + args.toString());
                        String argString = args.toString();
                        if(argString.contains("on")){
                            DNDon(callbackContext);
                        }
                        if(argString.contains("off")){
                            DNDoff(callbackContext);
                        }
                        if(argString.contains("alarm")){
                            DNDpartial(callbackContext);
                        }
                        if(argString.contains("priority")){
                            DNDpriority(callbackContext);
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
        } else if (isIgnoringBatteryOptimizations.equals(action)) {
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    try {
                        boolean result = isIgnoringBatteryOptimizations();
                        callbackContext.success(result?1:0);
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
        }else if (requestIgnoreBatteryOptimizations.equals(action)) {
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


//    @Override
//    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
//        this.cordova.getActivity().getApplication().registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks(){
//
//            @Override
//            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
//
//            }
//
//            @Override
//            public void onActivityStarted(Activity activity) {
//
//            }
//
//            @Override
//            public void onActivityResumed(Activity activity) {
//
//            }
//
//            @Override
//            public void onActivityPaused(Activity activity) {
//
//            }
//
//            @Override
//            public void onActivityStopped(Activity activity) {
//
//            }
//
//            @Override
//            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
//
//            }
//
//            @Override
//            public void onActivityDestroyed(Activity activity) {
//
//            }
//        });
//        super.initialize(cordova, webView);
//    }


    private void KeepScreenWakeOn(){
        this.cordova.getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void clearScreenWakeOn(){
        this.cordova.getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
    /**
     * Opens the system settings dialog where the user can tweak or turn off any
     * custom app start settings added by the manufacturer if available.
     *
     */
    private void openAppStart()
    {
        Activity activity = cordova.getActivity();
        PackageManager pm = activity.getPackageManager();

        for (Intent intent : getAppStartIntents())
        {
            if (pm.resolveActivity(intent, MATCH_DEFAULT_ONLY) != null)
            {
                // JSONObject spec = (arg instanceof JSONObject) ? (JSONObject) arg : null;

                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);

//                if (arg instanceof Boolean && !((Boolean) arg))
//                {
//
//                    break;
//                }

                //AlertDialog.Builder dialog = new AlertDialog.Builder(activity, Theme_DeviceDefault_Light_Dialog);
//
//                dialog.setPositiveButton(ok, (o, d) -> activity.startActivity(intent));
//                dialog.setNegativeButton(cancel, (o, d) -> {});
//                dialog.setCancelable(true);
//
//                if (spec != null && spec.has("title"))
//                {
//                    dialog.setTitle(spec.optString("title"));
//                }
//
//                if (spec != null && spec.has("text"))
//                {
//                    dialog.setMessage(spec.optString("text"));
//                }
//                else
//                {
//                    dialog.setMessage("missing text");
//                }
//
//                activity.runOnUiThread(dialog::show);

                break;
            }
        }
    }

    private List<Intent> getAppStartIntents()
    {
        return Arrays.asList(
                new Intent().setComponent(new ComponentName("com.miui.securitycenter","com.miui.permcenter.autostart.AutoStartManagementActivity")),
                new Intent().setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity")),
                new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.appcontrol.activity.StartupAppControlActivity")),
                new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")),
                new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity")),
                new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity")),
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



    private void DNDon(CallbackContext callbackContext) throws Exception {
        changeFilter(NotificationManager.INTERRUPTION_FILTER_NONE,callbackContext,5);
    }

    private void DNDoff(CallbackContext callbackContext) throws Exception {
        changeFilter(NotificationManager.INTERRUPTION_FILTER_ALL,callbackContext,2);
    }
    private void DNDpartial(CallbackContext callbackContext) throws Exception {
        changeFilter(NotificationManager.INTERRUPTION_FILTER_ALARMS,callbackContext,3);

    }
    private void DNDpriority(CallbackContext callbackContext) throws Exception {
        changeFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY,callbackContext,4);
    }
    private void changeFilter(int filter, CallbackContext callbackContext,int status) throws Exception {
        NotificationManager notificationManager = (NotificationManager) this.cordova.getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            while (!notificationManager.isNotificationPolicyAccessGranted()){
                if(getForegroundActivity().equals(this.cordova.getActivity().getPackageName())) {
                    showToast(cordova.getActivity(),"工作需要勿扰权限，请在跳转授权！");
                    Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                    this.cordova.getActivity().startActivity(intent);
                }
                Thread.sleep(2000);
            }
            notificationManager.setInterruptionFilter(filter);
        }else{
            callbackContext.error(0);
        }

    }

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



    public static void showToast(final Activity ctx,final String msg){
        // 判断是在子线程，还是主线程
        if("main".equals(Thread.currentThread().getName())){
            Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
        }else{
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
        PowerManager powerManager = (PowerManager)this.cordova.getActivity().getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            isIgnoring = powerManager.isIgnoringBatteryOptimizations(this.cordova.getActivity().getPackageName());
        }
        return isIgnoring;
    }

    public void requestIgnoreBatteryOptimizations(CallbackContext callbackContext, JSONArray permissions) throws Exception {
        while (!isIgnoringBatteryOptimizations()) {
            if(getForegroundActivity().equals(this.cordova.getActivity().getPackageName())) {
                showToast(cordova.getActivity(),"正在跳转授权电池优化,允许后台运行");
                Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + this.cordova.getActivity().getPackageName()));
                this.cordova.getActivity().startActivity(intent);
            }else{
                showToast(cordova.getActivity(),"请授权电池优化,允许后台运行");
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
            if(!cordova.hasPermission(permission)) {
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
