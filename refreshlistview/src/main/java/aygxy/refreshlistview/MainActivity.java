package aygxy.refreshlistview;
/*下拉刷新，上拉加载*/

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/***
 * 下拉刷新，上拉加载更多，回调
 * 回调（在一个组件1定义接口，
 * 2，定义一个方法，将接口传进来将其暴露出去
 * 3，在调用者里面注册一个事件，
 * 4，当有某一个事件触发了控件的某一个事件，它就会告知外部（此时接口已经被创建，不为空））
 * 5，外部接收事件  ，根据事件类型作相应的处理
 */
/*刷新（开自行车）要先睡眠，增加数据要通知适配器进行更新（不可再自线程，开 runOnUiThread(），最后要进行界面的恢复*/
public class MainActivity extends AppCompatActivity {
    private RefreshListView listView;
    private ArrayList<String> listdatas;
    private MyAdater myAdater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去标题
        setContentView(R.layout.activity_main);
        listView = (RefreshListView) findViewById(R.id.list_view);

        //要在数据适配前为listview增加头部和脚步
        Button button = new Button(getApplicationContext());
        button.setText("我是listView的第0个条目");
        listView.addHeaderView(button);
        //（在主布局）设置对listView的监听
        listView.setORefreshListener(new RefreshListView.OnRefreshListener() {//此接口为RefreshListView中的
            @Override
            public void onRefresh() {
                //在此里面调用下拉刷新更多的数据，回调，当被调用者通知调用者自己被用户触发了了，调用者就在这里针对重发事件作出相应的处理
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            //睡眠一段时间再做刷新
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        listdatas.add(0, "我是下拉刷新出来的数据aaa");

                        //通知适配器重新刷新数据
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                myAdater.notifyDataSetChanged();
                                //下拉刷新结束时恢复界面的效果
                                listView.onRefreshComplete();
                            }
                        });
                    }
                }.start();
            }

            @Override
            public void onLoadMore() {
                //在此里面调用下拉刷新更多的数据，回调，当被调用者通知调用者自己被用户触发了了，调用者就在这里针对重发事件作出相应的处理
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        listdatas.add("我是上拉加载更多数据aaa111");
                        listdatas.add("我是上拉加载更多数据aaa222");
                        listdatas.add("我是上拉加载更多数据aaa333");
                        //通知适配器重新刷新数据
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                myAdater.notifyDataSetChanged();
                                //下拉刷新结束时恢复界面的效果
                                listView.onRefreshComplete();
                            }
                        });
                    }
                }.start();
            }
        });
        //创建数据适配的数据
        listdatas = new ArrayList<String>();
        for (int i = 0; i < 69; i++) {
            listdatas.add("我是listview的第" + i + 1 + "条目");
        }
        myAdater = new MyAdater();
        listView.setAdapter(myAdater);
    }

    class MyAdater extends BaseAdapter {

        @Override
        public int getCount() {
            return listdatas.size();
        }

        @Override
        public Object getItem(int position) {
            return listdatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = new TextView(getApplicationContext());
            textView.setTextSize(18f);
            textView.setText(listdatas.get(position));
            return textView;
        }
    }
}
