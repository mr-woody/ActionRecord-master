package quant.actionrecord.sample

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.LinearLayout
import android.widget.Toast
import com.woodys.eventcollect.EventCollectsManager
import kotlinx.android.synthetic.main.activity_main.*
import com.woodys.record.model.ActionItem
import quant.actionrecord.sample.bus.subscribe
import quant.actionrecord.sample.eventcollect.EvnetsManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById(R.id.button1).setOnClickListener {
            startActivity(Intent(this@MainActivity, SampleActivity1::class.java))
        }
        findViewById(R.id.button2).setOnClickListener {
            startActivity(Intent(this@MainActivity, SampleActivity2::class.java))
        }
        findViewById(R.id.button3).setOnClickListener { startActivity(Intent(this@MainActivity, SampleActivity3::class.java)) }

        findViewById(R.id.text1).setOnClickListener { Toast.makeText(this@MainActivity, "Click text!", Toast.LENGTH_SHORT).show() }

        val container = findViewById(R.id.ll_container) as LinearLayout
        container.getChildAt(3).setOnClickListener { Toast.makeText(this@MainActivity, "Click button,The button hasn't resource id", Toast.LENGTH_SHORT).show() }

        subscribe(ActionItem::class.java){
            /*when(it.type){
                Type.ACTIVITY_OPEN, Type.CLICK, Type.LIST_CLICK, Type.ACTIVITY_CLOSE -> EvnetsManager.addAction(it)
                else -> ""
            }*/

            content.append("$it\n")
            EvnetsManager.addAction(it)
        }

        //subscribe(ActionItem::class.java){ content.append("$it\n") }

        //发送统计数据
        EventCollectsManager.get().sendAction()
    }

}
