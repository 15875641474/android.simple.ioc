package laika.ioc.hzc.com.iocforlaika;

import android.app.Activity;

/**
 * Created by huangzongcheng on 2018/5/10.10:30
 * 328854225@qq.com
 */
public class Contract {
    public interface View{

    }

    public interface Presenter{
        Activity getParams1();

        String getParams2();
    }

}
