package laika.ioc.hzc.com.iocforlaika;

import android.app.Application;

import com.hzc.manage.HzcInjectManage;

/**
 * Created by huangzongcheng on 2018/5/10.10:28
 * 328854225@qq.com
 */
public class MyApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        HzcInjectManage.init(BuildConfig.INCLUDE_PROJECTS.split(","));
    }
}
