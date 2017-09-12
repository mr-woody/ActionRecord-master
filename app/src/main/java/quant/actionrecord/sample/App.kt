package quant.actionrecord.sample

import android.app.Application

import com.woodys.record.ActionRecordManager
import quant.actionrecord.sample.bus.RxBus

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
    }
}
