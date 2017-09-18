package quant.actionrecord.sample

import android.app.Application
import com.woodys.eventcollect.database.table.temp.TempEventData

import com.woodys.record.ActionRecordManager
import com.woodys.record.model.Type
import quant.actionrecord.sample.bus.RxBus
import quant.actionrecord.sample.eventcollect.EvnetsManager
import java.util.ArrayList

/**
 * Created by cz on 11/11/16.
 */

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        ActionRecordManager.init(this){
            debug=true
            enable=false
            record=true
            actionCallback({
                RxBus.post(it)
                when(it.type){
                    Type.ACTIVITY_OPEN, Type.CLICK, Type.LIST_CLICK, Type.ACTIVITY_CLOSE -> EvnetsManager.addAction(it)
                }
            })
            sendActionCallback({ items, action ->
                EvnetsManager.requestUserEvent(items as ArrayList<TempEventData>, 1,action)
            })
        }
    }

}
