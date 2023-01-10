package com.sminev.sfit

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.huawei.hms.hihealth.HuaweiHiHealth
import com.huawei.hms.hihealth.SettingController
import com.huawei.hms.hihealth.data.Scopes
import com.huawei.hms.support.api.entity.auth.Scope
import io.flutter.embedding.android.FlutterActivity


class MainActivity: FlutterActivity() {
    private var mSettingController: SettingController? = null
    private val TAG = "MainActivity"
    private val REQUEST_AUTH = 1002
    /**
     * Initialize SettingController.
     */
     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initHealthService()
        // Call the API each time the authorization process in Step 2 is started.
        requestAuth()
    }
    private fun initHealthService() {
        // Note that this refers to an Activity object.
        mSettingController = HuaweiHiHealth.getSettingController(this)
    }

    private fun requestAuth() {
        // Add scopes to be applied for. The following only shows an example. You need to add scopes according to your specific needs.
        val scopes = arrayOf( // View and store step count data in Health Kit.
            Scopes.HEALTHKIT_SLEEP_READ
        )

        // Obtain the intent of the authorization process. Value true indicates that the authorization process of the Huawei Health app is enabled, and false indicates that the authorization process is disabled.
        val intent = mSettingController!!.requestAuthorizationIntent(scopes, true)

        // Open the authorization process screen.
        Log.i(TAG, "start authorization activity")
        startActivityForResult(intent, REQUEST_AUTH)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Process only the response result of the authorization process.
        if (requestCode == REQUEST_AUTH) {
            // Obtain the authorization response result from the intent.
            val result = mSettingController!!.parseHealthKitAuthResultFromIntent(data)
            if (result == null) {
                Log.w(TAG, "authorization fail")
                return
            }
            if (result.isSuccess) {
                Log.i(TAG, "authorization success")
                // authorizedScopes refers to the scopes granted by users to your app.
                // Note that if the scope information here is different from that set in Step 2, it indicates that users do not select all of the scopes requested during the authorization.
                // You can call the API for sign-in and authorization in Step 2 again, to display the HUAWEI ID authorization screen.
                // You are advised not to display the authorization screen frequently, as this may affect the user experience.
                if (result.authAccount != null && result.authAccount.authorizedScopes != null) {
                    val authorizedScopes: Set<Scope> = result.authAccount.authorizedScopes
                    Log.i(TAG, "authorization scope size " + authorizedScopes.size)
                }
            } else {
                Log.w(TAG, "authorization fail, errorCode:" + result.errorCode)
            }
        }
    }
}
