package quant.actionrecord.sample.eventcollect.mouble;

import quant.actionrecord.sample.eventcollect.mouble.event.BaseEvent;

/**
 * Created by woodys on 2017/9/7.
 */

public class UserEvent {
    public String type;// 类型
    public String page;// class
    public long offsetTime;// 当前操作相对于上次操作的偏移时间,后端收到消息后解析时间
    public BaseEvent extraInfo;// 附加信息
}
