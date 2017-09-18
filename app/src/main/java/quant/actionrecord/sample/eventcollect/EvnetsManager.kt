package quant.actionrecord.sample.eventcollect

import android.util.Base64
import android.util.Log
import android.view.MotionEvent
import com.woodys.eventcollect.EventCollectsManager
import com.woodys.eventcollect.callback.Action
import com.woodys.eventcollect.database.table.temp.TempEventData
import com.woodys.eventcollect.mouble.EventItem
import com.woodys.record.model.ActionItem
import com.woodys.record.model.Type
import cz.netlibrary.request
import quant.actionrecord.sample.eventcollect.mouble.EventCollection
import quant.actionrecord.sample.eventcollect.mouble.UserEvent
import quant.actionrecord.sample.eventcollect.mouble.event.ClickEvent
import quant.actionrecord.sample.eventcollect.mouble.event.EnterPageEvent
import quant.actionrecord.sample.eventcollect.mouble.event.LeavePageEvent
import quant.actionrecord.sample.utils.JsonUtils
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*
import java.util.zip.GZIPOutputStream

/**
 * Created by woodys on 2017/8/10.
 */
object EvnetsManager{
    private val TAG = EvnetsManager::class.java.name
    /**
     * 用户行为统计
     */
    fun requestUserEvent(tempEventDatas:ArrayList<TempEventData>, retryCount:Int, action: Action<Any, Any>) {
        val nowdatass = JsonUtils.toJson(tempEventDatas)
        val eventCollection= getEventCollection(tempEventDatas) ?: return
        Log.e(TAG,eventCollection?.toString())
        Observable.create(Observable.OnSubscribe<String> { subscriber ->
            try {
                val nowdata = JsonUtils.toJson(eventCollection)
                val data = nowdata.toByteArray(charset("UTF-8"))
                val arr = ByteArrayOutputStream()
                val zipper = GZIPOutputStream(arr)
                zipper.write(data)
                zipper.flush()
                zipper.close()
                val str = Base64.encodeToString(arr.toByteArray(), Base64.URL_SAFE)
                subscriber.onNext(str)
                subscriber.onCompleted()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe({ value ->
            Thread.sleep(1*1000)
            action.call(true)
            Log.e(TAG,value)
        }) { e -> e.printStackTrace() }


    }


    /**
     * 组装数据源
     */
    fun getEventCollection(tempEventDatas:ArrayList<TempEventData>): EventCollection? {
        val eventData = if(null!=tempEventDatas && tempEventDatas.size>0) tempEventDatas[tempEventDatas.size - 1] else null
        var eventCollection: EventCollection? = null
        if (null != eventData) {
            eventCollection = EventCollection()
            eventCollection.phoneNo = eventData.phoneNo
            eventCollection.width = eventData.width
            eventCollection.height = eventData.height
            eventCollection.appVersion = eventData.appVersion
            eventCollection.deviceId = eventData.deviceId
            eventCollection.mobilemodel = eventData.mobilemodel
            eventCollection.mobiletype = eventData.mobiletype
            eventCollection.operator = eventData.operator
            eventCollection.latitude = eventData.latitude
            eventCollection.longitude = eventData.longitude
            eventCollection.network = eventData.network
            eventCollection.list = getUserEvents(tempEventDatas)
        }
        return eventCollection
    }


    fun getUserEvents(tempEventDatas:ArrayList<TempEventData>): ArrayList<UserEvent>? {
        var userEvents:ArrayList<UserEvent> = ArrayList()
        tempEventDatas.map {converter(it)}.forEach { userEvents.add(it) }
        return userEvents
    }

    fun converter(eventData:TempEventData):UserEvent{
        var userEvent:UserEvent = UserEvent()
        userEvent.type = eventData.type
        userEvent.page = eventData.clazz
        userEvent.offsetTime =System.currentTimeMillis() - eventData.ct
        userEvent.extraInfo = when {
            "click".equals( eventData.type) ->  ClickEvent().apply {
                x = eventData.x
                y = eventData.y
                identify = eventData.title
            }
            "enterPage".equals( eventData.type) -> EnterPageEvent().apply {
                pageTitle = eventData.title
            }
            "leavePage".equals( eventData.type) ->  LeavePageEvent().apply {
                pageTitle = eventData.title
                standingTime = eventData.offsetTime
            }
            else -> null
        }
        return userEvent
    }

    fun addAction(actionItem: ActionItem){
        val eventItem: EventItem = EventItem().apply {
            phoneNo = "18511084155"
            latitude= "0.00"
            longitude= "0.00"

            clazz = actionItem.clazzName
            when (actionItem.type){
                Type.ACTIVITY_OPEN->{
                    type = "enterPage"
                    title = actionItem.value
                }
                Type.ACTIVITY_CLOSE->{
                    type = "leavePage"
                    title = actionItem.value
                    if(actionItem.arg is Long) {
                        offsetTime = actionItem.arg as Long
                    }
                }
                Type.LIST_CLICK,Type.CLICK->{
                    type = "click"
                    title = actionItem.value
                    if(actionItem.arg is MotionEvent){
                        val ev:MotionEvent = actionItem.arg as MotionEvent
                        x = ev.x.toInt()
                        y = ev.y.toInt()
                    }
                }
            }
        }
        EventCollectsManager.get().addAction(eventItem)
    }
}