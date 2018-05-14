package laika.ioc.hzc.com.iocforlaika;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.hzc.manage.HzcInjectManage;

public class MainActivity extends Activity {

    @com.hzc.annotation.Autoware()
    public Contract.Presenter presenter;
    public MainActivity b = this;
    public String a = "sssss";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HzcInjectManage.inject(this);
        Log.i("hzc",presenter != null ? "success" : "error");
    }
}
