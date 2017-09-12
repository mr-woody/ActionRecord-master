package com.woodys.record.receive

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import com.woodys.record.debugLog

/**
 * Created by czz on 2017/7/25
 * 监听home键按下广播,以及时保存用户信息
 */
class HomeEventReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == Intent.ACTION_CLOSE_SYSTEM_DIALOGS) {
            val reason = intent.getStringExtra(HomeEventReceiver.Companion.SYSTEM_REASON)
            if (TextUtils.equals(reason, HomeEventReceiver.Companion.SYSTEM_HOME_KEY)) {
                debugLog("按下Home退出应用")
            } else if (TextUtils.equals(reason, HomeEventReceiver.Companion.SYSTEM_HOME_KEY_LONG)) {
                debugLog("打开任务列表")
            }
        }
    }

    companion object {
        private val SYSTEM_REASON = "reason"
        private val SYSTEM_HOME_KEY = "homekey"
        private val SYSTEM_HOME_KEY_LONG = "recentapps"
    }


}
