package com.cango.cat.biker

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainJumpActivity : AppCompatActivity() {

    private val facebookPackageName = "com.google.android.gm"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        launchFacebookAppOrMarket()
    }

    /**
     * Attempts to launch the Facebook app. If the app is not installed, it redirects to the Google Play Store.
     */
    private fun launchFacebookAppOrMarket() {
        if (isAppInstalled(facebookPackageName)) {
            launchApp(facebookPackageName)
        } else {
            openAppInMarket(facebookPackageName)
        }
    }

    /**
     * Checks if the specified app is installed on the device.
     */
    private fun isAppInstalled(packageName: String): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    /**
     * Launches the specified app.
     */
    private fun launchApp(packageName: String) {
        packageManager.getLaunchIntentForPackage(packageName)?.let {
            startActivity(it)
            finish()
        }
    }

    /**
     * Opens the specified app in the Google Play Store.
     */
    private fun openAppInMarket(packageName: String) {
        val uri = Uri.parse("market://details?id=$packageName")
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        try {
            startActivity(goToMarket)
            finish()
        } catch (exception: ActivityNotFoundException) {
            openAppInBrowser(packageName)
        }
    }

    /**
     * Opens the specified app's Google Play Store page in a web browser.
     */
    private fun openAppInBrowser(packageName: String) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
            )
        )
        finish()
    }
}
