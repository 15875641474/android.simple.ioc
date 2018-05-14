package laika.ioc.hzc.com.iocforlaika;

import android.app.Activity;

/**
 * Created by huangzongcheng on 2018/5/10.10:30
 * 328854225@qq.com
 */
@com.hzc.annotation.Service
public class Presenter implements Contract.Presenter {
    Activity params1;
    String params2;

    public Presenter(){}

    public Presenter(Activity params1,String params2){
        this.params1 = params1;
        this.params2 = params2;
    }

    public Activity getParams1() {
        return params1;
    }

    public String getParams2() {
        return params2;
    }
}
