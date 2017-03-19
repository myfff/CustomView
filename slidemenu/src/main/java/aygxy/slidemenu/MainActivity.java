package aygxy.slidemenu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import UI.SlideMenu;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private SlideMenu sm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题
        setContentView(R.layout.activity_main);
        sm = (SlideMenu) findViewById(R.id.sm);
        //设置主面板的点击事件
        findViewById(R.id.ib_back).setOnClickListener(this);
    }
    //此方法必须实现，否则查询在执行时会出现错误
    public  void onTabClick(View view){
    }
    @Override
    public void onClick(View v) {
        sm.switchstate();
    }
}
