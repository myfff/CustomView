package aygxy.onandoff;

        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.widget.Toast;

        import aygxuy.ui.ToggleView;
/*自定义开关*/
public class MainActivity extends AppCompatActivity {

    private ToggleView toggleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toggleView = (ToggleView) findViewById(R.id.toggle_view);

        toggleView.setSwitchBackgroundResource(R.drawable.switch_background);
        toggleView.setSlideButtonResouce(R.drawable.slide_button);

        //设置开关更新的监听动作
        //【3】在合适的位置，执行接口的方法
        toggleView.setOnSwitchStateUpdateLister(new ToggleView.OnSwitchStateUpdateLister() {
            @Override
            public void onStateUpdate(boolean state) {
                Toast.makeText(getApplicationContext(), "开关状态为" + state, Toast.LENGTH_LONG).show();
            }
        });
        //设置一开始的状态为flase
        toggleView.setSwitchstate(false);
    }
}