package quant.actionrecord.sample.eventcollect.mouble.event;

/**
 * Created by woodys on 2017/9/7.
 */

public class HttpRequestEvent extends BaseEvent{
    public String uri;// 请求地址
    public String body;// 请求参数
}
