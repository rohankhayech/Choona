/*
 * Copyright (c) 2022 Rohan Khayech
 */

package com.rohankhayech.choona.view;

import android.content.pm.PackageManager;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

/**
 * The Permission Handler class provides methods for checking and requesting Android runtime permissions,
 * providing a framework for permissions to be handled in the recommended, best practice way.
 *
 * @author Rohan Khayech
 */
public class PermissionHandler {

    /** Android Activity Context */
    private final AppCompatActivity context;

    /** Callback to handle explanation and denial of permissions. */
    private final PermissionCallback callback;

    /** Permission request launcher. */
    private final ActivityResultLauncher<String> requestPermissionLauncher;

    /** A sensitive action that requires a permission to perform. */
    private Runnable action = null;

    /** The required Android runtime permission. */
    private final String perm;

    /**
     * Constructs a new Permission Handler for the specified permission.
     * This must only be called in the Activity or Fragment's onCreate() method.
     * @param context The Android activity context.
     * @param perm The Android runtime permission to handle.
     * @param callback Callback to handle explanation and denial of permissions.
     */
    public PermissionHandler(AppCompatActivity context, String perm, PermissionCallback callback) {
        this.context = context;
        this.perm = perm;
        this.callback = callback;

        // Register permission request launcher.
        requestPermissionLauncher = context.registerForActivityResult(new RequestPermission(), isGranted -> {
            if (isGranted) {
                action.run();
            } else {
                callback.onPermissionDenied();
            }
        });
    }

    /**
     * Checks if the permission has already been granted, and if not, requests the permission.
     * Once granted the specified sensitive action will be performed.
     * If not granted, this function will trigger the callback to show an in-context UI or downgrade
     * the app experience.
     * @param action The sensitive action to perform.
     */
    public void requestPermAndPerform(Runnable action) {
        // Check if the permission has been granted.
        if (ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED) {
            // Perform the sensitive action.
            action.run();
        } else if (context.shouldShowRequestPermissionRationale(perm)) {
            // Show an in-context UI describing why the permission is required.
            callback.showInContextUI();
        } else {
            // Request the permission.
            requestPermission(action);
        }
    }

    /**
     * Checks for the specified permission and performs the sensitive action if granted.
     * If not granted the action will simply not be performed. The calling class can decide
     * whether to throw an exception or not in this case.
     * This method is intended for checking permissions once they have already been formally requested.
     * @param action The sensitive action to perform.
     * @return {@code true} if the action was performed, {@code false} if the permission was not granted.
     */
    public boolean checkPermAndPerform(Runnable action) {
        boolean granted = ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED;
        if (granted) {
            // You can use the API that requires the permission.
            action.run();
        }
        return granted;
    }

    /**
     * Requests the permission, performing the sensitive action if granted.
     * If not granted the callback is triggered to downgrade the app experience.
     * @param action The sensitive action to perform.
     */
    private void requestPermission(Runnable action) {
        this.action = action;
        requestPermissionLauncher.launch(perm);
    }

    /**
     * Callback to handle showing in-context UI when requesting the permission, and downgrading
     * the app experience when the permission is denied.
     */
    public interface PermissionCallback {
        /**
         * Opens an educational UI, explaining to the user why your app requires this
         * permission for a specific feature to behave as expected.
         * This should call {@code requestPMAndPerform()} to request the permission,
         * or allow the user to cancel/deny the permission.
         */
        void showInContextUI();

        /**
         * Downgrades the app experience and informs the user that the feature is unavailable
         * due to the permission being denied.
         */
        void onPermissionDenied();
    }
}
