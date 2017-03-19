package aygxy.xialaselect06;


import android.graphics.drawable.BitmapDrawable;

import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
//对比手机卫士，checkbox是你点击他所在地方的时候，会往里传他才响应，而在那里面，为了让状态随性和父改变，我们禁用了不让其响应

/*注意，此里,1，首先listView中的ImageButton会抢占焦点，listView的条目点击没有用，
所以我们在主布局中加  android:descendantFocusability="blocksDescendants"
(Button,ImageButton自动按钮的这些控件会抢占全局焦点，即使不点击他们所在地方啊也会抢占，所以为了不让其抢占
，我们就设置了全局的!-- android:descendantFocusability="blocksDescendants"
子孙获取焦点-->
就是全局的子孙获取焦点都是以块的形式)
* 2，popubWindow默认也是不可获取焦点的，通过设置
*/

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private EditText et_input;
    private ImageButton ib_dropDown;
    private ListView listView;
    private PopupWindow popupWindow;
    private ArrayList<String> datas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_input = (EditText) findViewById(R.id.et_input);
        ib_dropDown = (ImageButton) findViewById(R.id.ib_dropDown);
        ib_dropDown.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        showPopuWindow();
    }

    /**
     * 展现出popuWindow
     */
    private void showPopuWindow() {
        initListView();
        // 展现什么
        popupWindow = new PopupWindow(listView, et_input.getWidth(), 600);
        //popuwindow默认不可以获取焦点，
        popupWindow.setFocusable(true);
        //点击popuwidow外部，使得其隐藏,必须同时设置这两个才可以有效果
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        //展示的位置
        popupWindow.showAsDropDown(et_input, 0, -10);
    }

    /**
     * 初始化lisyView
     */
    private void initListView() {
        listView = new ListView(this);
        listView.setDividerHeight(0);//去掉分割线
        listView.setBackgroundResource(R.drawable.listview_background);//设置listView的背景
        //设置listView的条目监听
        listView.setOnItemClickListener(this);
        datas = new ArrayList<String>();
        for (int i = 0; i < 30; i++) {
            datas.add("我是listView的数据" + i);
        }
        listView.setAdapter(new MyAdater());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String data = datas.get(position);//得到数据
        et_input.setText(data);//将选中的条目内容显示到et_textView

        popupWindow.dismiss();//将popuwindow消失
    }

    class MyAdater extends BaseAdapter {

        private TextView tv_number;

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            View view;
            if (convertView == null) {
                view = View.inflate(getApplication(), R.layout.item_number, null);
            } else {
                view = convertView;
            }
            tv_number = (TextView) view.findViewById(R.id.tv_number);
            tv_number.setText(datas.get(position));
            view.findViewById(R.id.ib_delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    datas.remove(position);//删除
                    notifyDataSetChanged();//更新
                    if (datas.size() == 0) {
                        //如果删除最后一行，隐藏popuwindow
                        popupWindow.dismiss();
                    }
                }
            });
            return view;
        }
    }
}
