package quant.actionrecord.sample.eventcollect.mouble.event;

/**
 * Created by woodys on 2017/9/7.
 */

public class LeavePageEvent extends BaseEvent {
    public String pageTitle;// 页面title
    public long standingTime;// 页面停留时间，当前时间减去进入的时间
}
