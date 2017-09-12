package com.woodys.record.prefs

import android.text.TextUtils
import xyqb.library.XmlElement
import xyqb.library.config.Config
import xyqb.library.config.XmlReaderBase

/**
 * Created by cz on 2017/7/25.
 */
@Config("define_activity.xml")
class ActivityReader: XmlReaderBase<MutableMap<String, String?>>() {

    override fun readXmlConfig(rootElement: XmlElement): MutableMap<String, String?> {
        val result=mutableMapOf<String,String?>()
        rootElement.children.forEach {
            val name=it.getAttributeValue("name")
            val info=it.getAttributeValue("info")
            result.put(name,if(TextUtils.isEmpty(info)) null else info)
        }
        return result
    }
}