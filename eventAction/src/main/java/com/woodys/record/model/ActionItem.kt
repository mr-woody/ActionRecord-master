package com.woodys.record.model

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Administrator on 2016/11/13.
 */

class ActionItem(val token:Int, val type: Type, val clazzName:String?, val value: String?, var arg:Any?=null) {
    companion object {
        val formatter= SimpleDateFormat("yy-MM-dd HH:mm:ss")
    }
    val ct: Long = System.currentTimeMillis()

    override fun toString(): String ="${ActionItem.Companion.formatter.format(Date(ct))} $type $clazzName $value ${if(null!=arg) arg else ""}"
}
