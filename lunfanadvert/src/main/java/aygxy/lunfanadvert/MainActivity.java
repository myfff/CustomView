package aygxy.lunfanadvert;
/*轮番广告*/
/*此处的小白点设置我们是通过双倍的小白点设置，图片选择器的方式，
根据不同的时候，选择不同的状态，让其显示小白点还是小黑点
白点为状态切换*/

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
/*
* 重复循环的时候，要要注意图片和小白点都为5个
* 2中情况，一种向左，一种向右，，，解决向左的办法是让在适配数据时就让页面停留在一个随机大的条目数中(伪轮询)*/


/*1，适配viewpager图片资源，先加载资源id成数组，在创建ImageView对象设置其的图片为我们数组里的图片（根据索引适配）
* 2，适配小白点,为我们自己定义的形状，将它作为每一个 pointView的选择器，当可用时颜色为白色，不可用时颜色为灰色
* 3，将图片，小白点选择器相协调，还有一开始的图片及小白点
* 4，无限轮询，向左向右
* 5，自动轮询，注意要销毁*/
public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private ViewPager viewPager;
    private MyAdater myAdater;
    private List<ImageView> mImageViewList;
    private ArrayList<View> pointViewList;
    private LinearLayout ll_point;
    private int[] imageResTds;
    private TextView tv_des;
    private String[] contentDesIds;
    private int lastEnablePoint = 0;
    private boolean isRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //所谓mvc模式
        //【1】Model,数据
        initData();
        //【2】初始化布局(View)
        initViews();
        //【3】Controller 控制器
        initAdater();
        //【4】开启轮询
        new Thread() {
            @Override
            public void run() {
                isRunning = true;//但要在销毁时不在进行轮询
                while (true) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("viewPager.getCurrentItem()设置当前条目位置" + viewPager.getCurrentItem());
                            //睡眠2秒后就自动像下跳一位
                            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);//但不可以在主线程中更新ui
                        }
                    });
                }
            }
        }.start();
    }

    /**
     * 【1】初始化数据资源
     */
    private void initData() {
        //图片资源id数组
        imageResTds = new int[]{R.drawable.a, R.drawable.b, R.drawable.c, R.drawable.d, R.drawable.e};
        //初始化文字描述
        contentDesIds = new String[]{
                "巩俐不低俗，我就不低俗",
                "朴树又回来了，再唱经典咯个引万人合唱",
                "解密北京电影如何升级",
                "乐视网TV版dapais",
                "热血屌丝的反杀"
        };
    }

    /**
     * 【2】初始化布局
     */
    private void initViews() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        ll_point = (LinearLayout) findViewById(R.id.ll_point);
        tv_des = (TextView) findViewById(R.id.tv_des);

        //设置页面更新监听
        viewPager.setOnPageChangeListener(this);
        //初始化要展示的5个Image（根据id循环遍历将5个图片设置给5个ImageView中，并将其加入到一个lIstanbul中）
        ImageView imageView;
        mImageViewList = new ArrayList<ImageView>();
        //还要加选择器 ，小白点
        View pointView;
        //不需要加入集合，直接加入线性布局中
        for (int i = 0; i < imageResTds.length; i++) {
            imageView = new ImageView(this);
            //根据id将图片设置给imageview对象
            imageView.setBackgroundResource(imageResTds[i]);
            mImageViewList.add(imageView);

            //小白点
            pointView = new View(this);
            pointView.setBackgroundResource(R.drawable.selector_bg_point);//viewd的一个背景资源(为一个 选择器)
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(5, 5);//圆点的大小
            if (i != 0) {//非第一个才需要加边距
                layoutParams.leftMargin = 10;
            }
            //都设置为false初始时都设为1
            pointView.setEnabled(false);
            //将小白点加入到线性布局中
            ll_point.addView(pointView, layoutParams);

        }
    }


    /**
     * 【3】 初始化数据适配器
     */
    private void initAdater() {
        //设置第一个为true，此处必须在第一次适配数据时设置第一个条目的小白点为选中
        ll_point.getChildAt(0).setEnabled(true);
        tv_des.setText(contentDesIds[0]);//同理文字也应该指定
        myAdater = new MyAdater();
        viewPager.setAdapter(myAdater);//匹配数据
        //一开始就让其设置到某个位置，相当于前后都有一定的伪条目可以滑动
        viewPager.setCurrentItem(50000);
    }

    //数据适配器(PagerAdater)
    class MyAdater extends PagerAdapter {
        @Override
        public int getCount() {
            //  return mImageViewList.size();
          //伪无限循环 IndexOutOfBoundsException
            return Integer.MAX_VALUE;
        }

        //指定复用的判断逻辑，固定写法
        @Override
        public boolean isViewFromObject(View view, Object object) {
            //看是否可以复用
            return view == object;
        }

        //1,返回要显示的条目内容
        //此方法比重写，否则异常
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //container:容器:ViewPager
            //position:当前要显示条目的位置
            //正好索引从0开始，计算各个的位置为0,1,2,3,4，
            int newPosition = position % mImageViewList.size();
            ImageView imageView = mImageViewList.get(newPosition);
            //a,把view对象添加到container
            container.addView(imageView);
            //b,把view对象返回给框架，适配器
            return imageView;
        }

        //2销毁条目
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            System.out.println("instantiateItem销毁" + position);
            //object要销毁的对象
            container.removeView((View) object);
        }
    }


//下面的方法都为实现ViewPager的页面监听
    //初始化布局的时候就需要进行监听
    /**
     * 滚动时调用
     *
     * @param position
     * @param positionOffset
     * @param positionOffsetPixels
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //滚动时调用
    }

    /**
     * 当viewPager显示图片的时候，我们需要进行进度和文字的同步（即要做下面文字和小白点的同步）
     *
     * @param position
     */
    @Override
    public void onPageSelected(int position) {
        //    新的条目被选中时调用
        System.out.println("onPageSelected" + position);
        int newPosition = position % mImageViewList.size();
        tv_des.setText(contentDesIds[newPosition]);//新的一页被选中时设置相应的文本
        //新的一页被选中时，将上一次的小白点设置为灰色不可用状态
        ll_point.getChildAt(lastEnablePoint).setEnabled(false);
        ll_point.getChildAt(newPosition).setEnabled(true);//新一页的小白点可用
        //将此时的赋值（现在的付给上一页，当新的一页显示，此页将作为上一页变为不可用）
        lastEnablePoint = newPosition;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //滚动状态变化
    }

    @Override
    protected void onDestroy() {
        isRunning = false;
    }
}


