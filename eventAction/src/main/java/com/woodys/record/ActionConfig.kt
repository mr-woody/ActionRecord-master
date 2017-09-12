package com.woodys.record

import com.woodys.record.model.ActionItem

/**
 * Created by cz on 2016/11/13.
 */

class ActionConfig {
    var record = false
    var debug=false
    var folderPath:String?=null
    internal var actionCallback:((ActionItem)->Unit)?=null

    fun actionCallback(actionCallback:(ActionItem)->Unit){
        this.actionCallback=actionCallback
    }
}
