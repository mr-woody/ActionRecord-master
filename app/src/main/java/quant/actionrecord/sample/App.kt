package quant.actionrecord.sample

import android.app.Application
import com.financial.quantgroup.v2.eventcollect.EvnetsManager
import com.woodys.eventcollect.EventCollectsManager
import com.woodys.eventcollect.database.table.temp.TempEventData

import com.woodys.record.ActionRecordManager
import com.woodys.record.model.ActionItem
import com.woodys.record.model.Type
import quant.actionrecord.sample.bus.RxBus
import quant.actionrecord.sample.bus.subscribe
import java.util.ArrayList

/**
 * Created by cz on 11/11/16.
 */

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        ActionRecordManager.init(this){
            debug=true
            record=true
            actionCallback(RxBus::post)
        }

        subscribe(ActionItem::class.java){
            when(it.type){
                Type.ACTIVITY_OPEN, Type.CLICK, Type.LIST_CLICK, Type.ACTIVITY_CLOSE -> EvnetsManager.addAction(it)
            }
        }

        //用户行为统计
        EventCollectsManager.get()
                .init(this)
                .setSendActionCallback { items, action ->
                    EvnetsManager.requestUserEvent(items as ArrayList<TempEventData>, 1,action)
                }
    }

}
