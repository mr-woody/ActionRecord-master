package com.woodys.record.prefs

import android.text.TextUtils
import xyqb.library.XmlElement
import xyqb.library.config.XmlReaderBase

/**
 * Created by cz on 2017/7/25.
 */
@xyqb.library.config.Config("define_view.xml")
class ViewReader: XmlReaderBase<MutableMap<String, String?>>() {

    override fun readXmlConfig(rootElement: XmlElement): MutableMap<String, String?> {
        val result=mutableMapOf<String,String?>()
        rootElement.children.forEach {
            val id=it.getAttributeValue("id")
            val info=it.getAttributeValue("info")
            result.put(id,if(TextUtils.isEmpty(info)) null else info)
        }
        return result
    }
}