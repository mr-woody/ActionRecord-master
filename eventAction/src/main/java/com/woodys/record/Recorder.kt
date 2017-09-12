package com.woodys.record

import android.content.Context
import android.content.pm.PackageManager
import com.woodys.record.model.ActionItem
import com.woodys.record.model.RecordItem
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.UUID.randomUUID

/**
 * Created by czz on 2016/11/13.
 */

object Recorder {
    var recordItem: RecordItem?=null

    fun newAction() {
        restoreRecordItem(recordItem)
        //新的数据
        Recorder.recordItem = RecordItem(randomUUID().toString().replace("-".toRegex(), ""))
    }

    fun addAction(action: ActionItem){
        Recorder.recordItem?.actionItems?.add(action)
        ActionRecordManager.actionCallback?.invoke(action)
    }

    fun exit()= Recorder.restoreRecordItem(recordItem)

    private fun restoreRecordItem(recordItem: RecordItem?){
        val item=recordItem?:return
        //1:这里重新排序所有的事件顺序
        debugLog("开始打印所有操作")
        item.actionItems.forEach {
            debugLog("$it")
        }
        //2:这里选择是否生成本地的日志信息
        //3:回调事件到
        if(ActionRecordManager.debug){
            Recorder.writeActionItem(ActionRecordManager.context, item)
        }
    }

    fun writeActionItem(context: Context, item: RecordItem) {
        if (!Recorder.hasPermission(context, "android.permission.WRITE_EXTERNAL_STORAGE")) {
            throw RuntimeException("not configuration uses-permission android.permission.WRITE_EXTERNAL_STORAGE")
        } else {
            val file = File(ActionRecordManager.cacheFolderPath, item.id)
            var fileWriter: FileWriter? = null
            try {
                fileWriter = FileWriter(file)
                val out=StringBuilder()
                item.actionItems.forEach {
                    out.append("$it\n")
                }
                fileWriter.write(out.toString())
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                if (null != fileWriter) {
                    try {
                        fileWriter.close()
                    } catch (e: java.io.IOException) {
                        e.printStackTrace()
                    }

                }
            }
        }
    }

    /**
     * 判断某个权限是否授权
     * @param permissionName 权限名称，比如：android.permission.READ_PHONE_STATE
     * *
     * @return
     */
    fun hasPermission(context: Context, permissionName: String): Boolean {
        var result = false
        try {
            val pm = context.packageManager
            result = PackageManager.PERMISSION_GRANTED == pm.checkPermission(permissionName, context.packageName)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }
}

inline fun <reified T> T.debugLog(message:String){
    if(ActionRecordManager.debug){
        android.util.Log.e("Recorder",message)
    }
}
