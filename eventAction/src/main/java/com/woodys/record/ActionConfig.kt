package com.woodys.record

import android.view.View
import android.widget.AdapterView
import com.woodys.eventcollect.EventCollectsManager
import com.woodys.eventcollect.callback.Action
import com.woodys.record.model.ActionItem

/**
 * Created by cz on 2016/11/13.
 */

class ActionConfig {
    var record = false
    var debug=false
    var enable=true
    var folderPath:String?=null
    internal var actionCallback:((ActionItem)->Unit)?=null
    var convertDataCallback:((v: View, parent: AdapterView<*>?)->String)?=null

    fun actionCallback(actionCallback:(ActionItem)->Unit){
        this.actionCallback=actionCallback
    }

    fun convertDataCallback(actionCallback:(v: View, parent: AdapterView<*>?)->String){
        this.convertDataCallback=actionCallback
    }

    fun sendActionCallback(actionCallback:(Any, Action<Any, Any>)->Unit){
        EventCollectsManager.get().setSendActionCallback(actionCallback)
    }
}
