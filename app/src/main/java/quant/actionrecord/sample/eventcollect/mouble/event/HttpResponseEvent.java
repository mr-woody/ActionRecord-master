package quant.actionrecord.sample.eventcollect.mouble.event;

/**
 * Created by woodys on 2017/9/7.
 */

public class HttpResponseEvent extends BaseEvent{
    public long executeTime;//请求执行时间, 当前时间减去request请求时间
    public boolean success;// 请求是否成功
    // 以下只在请求失败时记录
    public String httpCode;//网络请求的code
    public String businessCode;// 业务定义的错误码
    public String msg;// 错误信息
}
