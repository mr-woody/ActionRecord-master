package com.woodys.record.widget

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.*
import android.widget.*
import com.woodys.record.debugLog
import java.lang.reflect.Field


/**
 * Created by cz on 11/11/16.
 */
class RecordLayout(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : FrameLayout(context,attrs,defStyleAttr) {
    companion object{
        val listenerInfoField: Field = View::class.java.getDeclaredField("mListenerInfo")
        val itemClickListenerField: Field = AdapterView::class.java.getDeclaredField("mOnItemClickListener")
        init {
            RecordLayout.Companion.listenerInfoField.isAccessible=true
            RecordLayout.Companion.itemClickListenerField.isAccessible=true
        }
    }
    private var itemClickListener:((AdapterView<*>, View, Int, Long, MotionEvent)->Unit)?=null
    private var clickListener:((View, MotionEvent)->Unit)?=null

    constructor(context: Context) : this(context,null,0)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs,0)


    override fun onTouchEvent(ev: MotionEvent): Boolean {
        val action = ev.actionMasked
        if (MotionEvent.ACTION_DOWN == action) {
            val x = ev.x.toInt()
            val y = ev.y.toInt()
            //根据抬起点,检测到用户操作控件.再根据用户手势操作,检测控件是滑动抬起,还是点击抬起.
            var findView = findViewByPoint(rootView, x, y)
            if (null != findView) {
                if(findView is AdapterView<*>){
                    ensureAdapterViewClickListenerWrapper(findView,ev)
                } else {
                    ensureViewClickListenerWrapper(findView,ev)
                }
            }
        }
        return super.onTouchEvent(ev)
    }



    /**
     * 设置控件点击包装器
     */
    private fun ensureViewClickListenerWrapper(findView: View, ev: MotionEvent) {
        if (findView.hasOnClickListeners()) {
            val listenerInfo = listenerInfoField.get(findView)
            if (null != listenerInfo ) {
                val clickListenerField = listenerInfo::class.java.getDeclaredField("mOnClickListener")
                clickListenerField.isAccessible=true
                val clickListener = clickListenerField.get(listenerInfo) as? OnClickListener
                if (null != clickListener&& clickListener !is ViewClickListenerWrapper) {
                    clickListenerField.set(listenerInfo, ViewClickListenerWrapper(clickListener,ev))
                    debugLog("set view click wrapper")
                }
            }
        }
    }

    /**
     * 设置AdapterView控件点击包装器
     */
    private fun ensureAdapterViewClickListenerWrapper(findView: AdapterView<*>, ev: MotionEvent) {
        val itemClickListener = findView.onItemClickListener
        if (null != itemClickListener&& itemClickListener !is ListViewItemClickWrapper) {
            findView.onItemClickListener = ListViewItemClickWrapper(itemClickListener,ev)
            debugLog("set adapter view click wrapper")
        }
    }

    private fun findViewByPoint(parent: View, x: Int, y: Int): View? {
        var findView: View? = null
        if(parent is AdapterView<*>){
            //如果是AdapterView,直接返回,因为adapterView的点击事件与常规控件不同
            findView = parent
        } else if (parent is ViewGroup) {
            //遍历的父控件
            val viewGroup = parent
            for (i in 0..viewGroup.childCount - 1) {
                val child = viewGroup.getChildAt(i)
                val childView = findViewByPoint(child, x, y)
                if (null != childView) {
                    findView = childView
                }
            }
        } else {
            //普通控件
            val rect = Rect()
            parent.getGlobalVisibleRect(rect)
            if (rect.contains(x, y)) {
                findView = parent
            }
        }
        return findView
    }

    /**
     * 常规控件的点击事件包装
     */
    inner class ViewClickListenerWrapper(val listener: OnClickListener, val ev: MotionEvent): OnClickListener by listener{
        override fun onClick(v: View) {
            listener.onClick(v)
            clickListener?.invoke(v,ev)
        }
    }

    /**
     * AdapterView的点击事件包装器
     */
    inner class ListViewItemClickWrapper(val listener: AdapterView.OnItemClickListener, val ev: MotionEvent): AdapterView.OnItemClickListener by listener{
        override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            listener.onItemClick(parent,view,position,id)
            itemClickListener?.invoke(parent,view,position,id,ev)
        }
    }

    fun onItemClick(listener:(AdapterView<*>, View, Int, Long, MotionEvent)->Unit){
        this.itemClickListener=listener
    }

    fun onViewClick(listener:(View, MotionEvent)->Unit){
        this.clickListener=listener
    }

}
