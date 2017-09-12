package com.woodys.record.callback

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.woodys.record.ActionRecordManager

/**
 * Created by cz on 11/11/16.
 */

class MyActivityLifecycleCallbacks : Application.ActivityLifecycleCallbacks {
    override fun onActivityCreated(activity: Activity?, bundle: Bundle?) {
        ActionRecordManager.onActivityCreated(activity)
    }

    override fun onActivityStarted(activity: Activity) {

    }

    override fun onActivityResumed(activity: Activity) {

    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity?) {
        ActionRecordManager.onActivityDestroyed(activity)
    }
}
