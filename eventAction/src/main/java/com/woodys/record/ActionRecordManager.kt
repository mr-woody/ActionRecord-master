package com.woodys.record

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.SystemClock
import android.util.SparseArray
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import com.woodys.record.callback.MyActivityLifecycleCallbacks
import com.woodys.record.model.ActionItem
import com.woodys.record.model.Type
import com.woodys.record.prefs.ActivityReader
import com.woodys.record.prefs.ViewReader
import com.woodys.record.receive.HomeEventReceiver
import com.woodys.record.widget.RecordLayout
import xyqb.library.config.Config
import xyqb.library.config.XmlReaderBase
import java.util.concurrent.Executors


/**
 * Created by cz on 11/11/16.
 */

object ActionRecordManager {
    private lateinit var actionConfig: ActionConfig
    private val activityItems= mutableMapOf<String,String?>()
    private val viewItems= mutableMapOf<String,String?>()
    private val openActivities= SparseArray<Long>()
    private var homeEventReceiver: HomeEventReceiver? = null
    private var startTime:Long=0L
    lateinit var context: Context
    var debug:Boolean=false
        get() = actionConfig.debug

    var actionCallback:((ActionItem)->Unit)?=null
        get() = actionConfig.actionCallback

    var cacheFolderPath:String?=null
        get() = if(null!= actionConfig.folderPath) actionConfig.folderPath else context.cacheDir.absolutePath


    fun init(application: Application, closure: ActionConfig.()->Unit) {
        application.registerActivityLifecycleCallbacks(MyActivityLifecycleCallbacks())
        context =application.applicationContext
        actionConfig = ActionConfig().apply(closure)
        //初始化出配置
        val activityReader = ActivityReader()
        val viewReader = ViewReader()
        Executors.newSingleThreadExecutor().execute {
            activityItems += readConfig(activityReader)
            viewItems += readConfig(viewReader)
        }
    }

    /**
     * 读取xml配置
     * @param item
     */
    fun readConfig(item: XmlReaderBase<MutableMap<String, String?>>): MutableMap<String,String?> {
        val result = mutableMapOf<String, String?>()
        val config = item::class.java.getAnnotation(Config::class.java)
        if (null != config) {
            val path = config.value
            val type = config.type
            val element = item.readXmlElement(type, path)
            result+=item.readXmlConfig(element)
        }
        return result
    }

    operator fun get(key:String)= viewItems[key]

    fun onActivityCreated(activity: Activity?) {
        val activity=activity?:return
        val name = activity::class.java.name
        val windowId=activity.window.decorView.hashCode()
        //检测是否为主界面
        val launchActivityName = getLaunchActivityName(activity)
        if (name == launchActivityName) {
            openActivities.clear()
            //注册home键广播事件
            homeEventReceiver = HomeEventReceiver()
            activity.registerReceiver(homeEventReceiver, IntentFilter(Intent. ACTION_CLOSE_SYSTEM_DIALOGS))
            //记录打开时间
            startTime = SystemClock.uptimeMillis()
            //记录新的事件
            Recorder.newAction()
            Recorder.addAction(ActionItem(windowId, Type.APP_OPEN, name, activityItems[name]))
        }
        //注入控件
        injectLayout(activity)
        //加入当前打开界面的根控件的hash值,以确定界面与界面之间的唯一标记
        Recorder.addAction(ActionItem(windowId, Type.ACTIVITY_OPEN, name, activityItems[name]))
        openActivities.put(windowId, SystemClock.uptimeMillis())
        debugLog("打开界面:${activityItems[name]} windowToken:$windowId")
    }

    fun onActivityDestroyed(activity: Activity?) {
        val activity=activity?:return
        val name = activity::class.java.name
        val windowId=activity.window.decorView.hashCode()
        val activityCreateTime = openActivities[windowId]?:-1
        val time = SystemClock.uptimeMillis() - activityCreateTime
        Recorder.addAction(ActionItem(windowId, Type.ACTIVITY_CLOSE, name, activityItems[name], time))
        debugLog("关闭界面:${activityItems[name]} 打开时间:$time")
        //检测主信息
        val launchActivityName = getLaunchActivityName(activity)
        if (name == launchActivityName) {
            Recorder.addAction(ActionItem(windowId, Type.APP_CLOSE, name, activityItems[name], SystemClock.uptimeMillis() - startTime))
            debugLog(("应用关闭时间:${SystemClock.uptimeMillis()- startTime}"))
            activity.unregisterReceiver(homeEventReceiver)
            Recorder.exit()
        }
    }

    /**
     * 注入自定义布局
     * @param activity
     */
    private fun injectLayout(activity: Activity) {
        val decorView = activity.window.decorView
        if (null != decorView && decorView is ViewGroup) {
            val decorGroup = decorView
            if (0 < decorGroup.childCount) {
                //加入统计浮层
                val recordLayout = RecordLayout(activity)
                //控件点击
                recordLayout.onViewClick {view,ev -> onViewClick(view,ev) }
                //Adapter列表点击
                recordLayout.onItemClick { adapterView, view, i, l,ev ->  onItemClick(adapterView,view,i,l,ev) }
                recordLayout.tag = activity.javaClass.simpleName
                decorGroup.addView(recordLayout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            }
        }
    }

    fun getLaunchActivityName(context: Context): String {
        val packageManager = context.packageManager
        val launchIntent = packageManager.getLaunchIntentForPackage(context.packageName)
        return launchIntent.component.className
    }


    fun onViewClick(v: View, ev: MotionEvent){
        val windowId=v.rootView.hashCode()
        val contentDescription=v.contentDescription as? String
        if(View.NO_ID!=v.id){
            val entryName = get(v.resources.getResourceEntryName(v.id))
            Recorder.addAction(ActionItem(windowId, Type.CLICK, v::class.java.name, entryName ?: contentDescription, ev))
            debugLog("id:$windowId 点击:${entryName?:contentDescription}")
        } else {
            Recorder.addAction(ActionItem(windowId, Type.CLICK, v::class.java.name, contentDescription ?: "#", ev))
            debugLog("id:$windowId 点击控件无id!")
        }
    }

    fun onItemClick(parent: AdapterView<*>, v: View, position:Int, id:Long, ev: MotionEvent){
        val windowId=parent.rootView.hashCode()
        val contentDescription=v.contentDescription as? String
        val entryName = get(v.resources.getResourceEntryName(parent.id))
        Recorder.addAction(ActionItem(windowId, Type.LIST_CLICK, v::class.java.name, entryName ?: contentDescription, ev))
        debugLog("id:$windowId 列表点击:$contentDescription 位置:$position")
    }


}
