package prithvi.lifcarecontacts.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by Prithvi on 3/25/2017.
 */

public class Permissions {

    public static final int PERMISSIONS_REQUEST_READ_PHONE_CONTACTS = 123;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkPermission(final Context context) {

        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_PHONE_CONTACTS);
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }
}
