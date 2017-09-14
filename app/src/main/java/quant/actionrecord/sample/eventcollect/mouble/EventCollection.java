package quant.actionrecord.sample.eventcollect.mouble;

import java.util.ArrayList;

/**
 * Created by woodys on 2017/9/7.
 */

public class EventCollection {
    public String phoneNo;
    public int width;// 屏幕宽
    public int height;// 屏幕高
    public String appVersion;// 版本
    public String deviceId;// 设备唯一id
    public String mobilemodel;// android6.0 iphone10 ..
    public String mobiletype;// 小米1 iphone6s ...
    public String operator;// 运营商
    public String latitude;// 纬度
    public String longitude;//经度
    public String network;// 3G WIFI ...
    public ArrayList<UserEvent> list;//对应的点击事件

}
