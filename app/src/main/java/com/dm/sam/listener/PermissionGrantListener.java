package com.dm.sam.listener;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;
import com.dm.sam.activity.CarteActivity;
import com.dm.sam.activity.PermissionsActivity;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class PermissionGrantListener implements View.OnClickListener {
    PermissionsActivity permissionsActivity;

    public PermissionGrantListener(PermissionsActivity permissionsActivity) {
        this.permissionsActivity = permissionsActivity;
    }
    /**
     * This method is triggered by the grant permissions button
     * it manages the permissions requests
     *
     */
    @Override
    public void onClick(View view) {
        //Used Dexter library to simplify permissions requests
        Dexter.withActivity(permissionsActivity)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {

                    //When the permission is granted, redirect to the CarteActivity
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        permissionsActivity.startActivity(new Intent(permissionsActivity, CarteActivity.class));
                        permissionsActivity.finish();
                    }

                    //If the permission is denied show dialog
                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if(response.isPermanentlyDenied()){
                            AlertDialog.Builder builder = new AlertDialog.Builder(permissionsActivity);
                            builder.setTitle("Permission Denied")
                                    .setMessage("Permission to access device location is permanently denied. you need to go to setting to allow the permission.")
                                    .setNegativeButton("Cancel", null)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent();
                                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            intent.setData(Uri.fromParts("package", permissionsActivity.getPackageName(), null));
                                        }
                                    })
                                    .show();
                        } else {
                            Toast.makeText(permissionsActivity, "Permission Denied", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .check();
    }
}
