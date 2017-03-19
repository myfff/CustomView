package aygxy.refreshlistview;

import android.content.Context;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
/*回调：1 我在一个控件或自定义控件中定义好一个接口（里面有抽象方法）
2，（注册监听）再定义一个方法接收的参数为接口对象（意味着当调用者想当调用此方法肯定要实现此抽象方法（实现内容可以有自己确定）
3，当控件被触发了某个事件后，在判断接口不为空（以上是接口被创建了或者说接口已经被调用者监听）后，
通知调用者可以在接口抽象方法中实现不同的处理*/

/**
 * Created by yh on 2016/12/2.
 */

public class RefreshListView extends ListView implements AbsListView.OnScrollListener {
    private View mListHeaderView;
    private int measuredHeight;
    private float downY;
    private float moveY;

    private static final int PULL_TO_REFRESH = 0;//下拉刷新
    private static final int RELASE_REFRESH = 1;//释放刷新
    private static final int REFRESHING = 2;//刷新中
    private float currentState = 0;//默认的状态为下拉刷新
    private ImageView mArrowView;
    private ProgressBar pb;
    private TextView mlastTimeDesc;
    private TextView mTitleText;
    private float paddingTop;
    private RotateAnimation rotateUpAnimation;
    private RotateAnimation rotateDownAnimation;

    private OnRefreshListener mListener;
    private View mFooterView;
    private int mFooterHinght;

    private int scrollstate = 0;//上拉的状态（监听listview状态）
    private boolean isLoadingMore;//是否加载更多

    public RefreshListView(Context context) {
        super(context);
        init();
    }

    public RefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 初始化头布局，脚布局，监听滚动
     */
    private void init() {
        //【1】加上头布局
        initHrederView();
        //初始化头布局的动画
        initAnimation();
        //【2】加上脚布局
        initFooterView();
        //设置下拉加载更多的事件
        setOnScrollListener(this);
    }

    /**
     * 【2】初始化脚布局
     */
    private void initFooterView() {
        mFooterView = View.inflate(getContext(), R.layout.list_footer_view, null);
        mFooterView.measure(0, 0);

        //获取控件的这是高度
        mFooterHinght = mFooterView.getMeasuredHeight();
        //先设置其不可见
        mFooterView.setPadding(0, -mFooterHinght, 0, 0);
        addFooterView(mFooterView);
    }

    /**
     * 初始化头布局的动画
     */
    private void initAnimation() {
        //向上转，围绕着自己的中心，逆时针旋转0——>-180
        rotateUpAnimation = new RotateAnimation(0f, -180f
                , Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateUpAnimation.setDuration(300);
        rotateUpAnimation.setFillAfter(true);//动画停留在结束位置

        //向下旋转
        //向上转，围绕着自己的中心，逆时针旋转0——>-180
        rotateDownAnimation = new RotateAnimation(-180f, -360
                , Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateDownAnimation.setDuration(300);
        rotateDownAnimation.setFillAfter(true);//动画停留在结束位置


    }

    private void initHrederView() {
        mListHeaderView = View.inflate(getContext(), R.layout.list_header_view, null);
        mArrowView = (ImageView) mListHeaderView.findViewById(R.id.iv_arrow);
        pb = (ProgressBar) mListHeaderView.findViewById(R.id.pb);
        mTitleText = (TextView) mListHeaderView.findViewById(R.id.tv_title);
        mlastTimeDesc = (TextView) mListHeaderView.findViewById(R.id.tv_desc_last_refresh);
        //提前手动测量宽度
        mListHeaderView.measure(0, 0);//按照设置的规则测量（只有这样才可以测出真是高度，否则无法测得高度）
        //一个是显示在界面上的高度，但我们在此时是不可能测出的（因为测量必须在控件获取焦点之后，此时还未获取焦点）
        int height = mListHeaderView.getHeight();
        //(真实的宽度)
        measuredHeight = mListHeaderView.getMeasuredHeight(); //此处我们只需先获得真实高度
        System.out.println("界面上的高度为0" + height + ";header的真实高度位98" + measuredHeight);
        //一开始设置内边距，可以隐藏自身控件，-自身控件的宽高
        mListHeaderView.setPadding(0, -measuredHeight, 0, 0);
        addHeaderView(mListHeaderView);//在设置内边距之前要先添加头布局和脚布局
    }

    //设置触摸事件     下拉
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //判断滑动激距离，给header设置paddingtop
        switch (ev.getAction()) {//此处我们只需要考虑y轴
            case MotionEvent.ACTION_DOWN:
                downY = ev.getY();
                System.out.println("downY" + downY);
                break;
            case MotionEvent.ACTION_MOVE:
                moveY = ev.getY();

                //如果正在刷新，我们就不相应再次下拉事件
                if (currentState == REFRESHING) {
                    return super.onTouchEvent(ev);
                }


                float offset = moveY - downY;//移动的偏移量
                //我们将手指移动的距离设置给header让其下拉出来:下拉的paddingTop=-自身高度+offset
                paddingTop = -measuredHeight + offset;

                //注意此处只有偏移量大于0和第一个条目可见我们才做处理，在此基础上，paddingTop>0做释放刷新，<0做下拉刷新
                // 偏移量>0&& 第一个条目可见才放大头部，否则我们做的都是无用功
                if (offset > 0 && getFirstVisiblePosition() == 0) {
                    mListHeaderView.setPadding(0, (int) paddingTop, 0, 0); //移动过程中重新设置一下header
                    if (paddingTop >= 0 && currentState != RELASE_REFRESH) {//头布局全部显示
                        //切换成释放刷新模式
                        currentState = RELASE_REFRESH;
                        //根据（移动过程中）我们在此处赋值的模式，抽取方法做不同的状态切换（1下拉刷新，2 释放刷新））
                        updateHerder();
                    } else if (paddingTop < 0 && currentState != PULL_TO_REFRESH) {//头布局不完全显示
                        //切换成下拉刷新状态
                        currentState = PULL_TO_REFRESH;
                        updateHerder();
                    }

                    return true;//当前事件被我们处理并消费
                }
                break;
            //当手放开，可能做回复处理，也有可能做正在刷新
            case MotionEvent.ACTION_UP:
                if (paddingTop < 0) {//不完全显示恢复下拉刷新
                    mListHeaderView.setPadding(0, -measuredHeight, 0, 0);
                    //默认为下拉刷新，此时我们无需重新赋值
                } else {
                    //>=0 完全显示，执行正在刷新
                    mListHeaderView.setPadding(0, 0, 0, 0);
                    currentState = REFRESHING;
                    updateHerder();
                }
                break;
        }
        return super.onTouchEvent(ev);
    }
//下拉
    public void updateHerder() {
        switch ((int) currentState) {
            case PULL_TO_REFRESH://切换为下拉刷新
                mArrowView.startAnimation(rotateUpAnimation);
                mTitleText.setText("下拉刷新");
                break;
            case RELASE_REFRESH://切换回释放刷新
                mArrowView.startAnimation(rotateDownAnimation);
                mTitleText.setText("释放刷新");
                break;
            case REFRESHING://刷新中
                mArrowView.clearAnimation();
                mArrowView.setVisibility(View.INVISIBLE);
                pb.setVisibility(View.VISIBLE);
                mTitleText.setText("正在刷新中。。。");
                if (mListener != null) {
                    //通知调用者，让其网络加载更多的数据（当下拉时进行通知回调）
                    mListener.onRefresh();
                }
                break;
        }
    }
    public interface OnRefreshListener {
        void onRefresh();//下拉刷新
        void onLoadMore();//上拉加载更多
    }
    public void setORefreshListener(OnRefreshListener mListener) {
        this.mListener = mListener;
    }

    //下面2个方法为实现方法，为上拉拉加载更多
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        //状态更新的时候调用（空闲，触摸）
        if (isLoadingMore) {//正在加载不响应事件（但一次肯定false不走此逻辑）
            //不在加载
            return;
        }
        // 最新状态为空闲，并且当前界面     显示了所有界面的最后一条，加载更多
        if (SCROLL_STATE_IDLE == scrollstate && getLastVisiblePosition() >= getCount() - 1) {
            isLoadingMore = true;
            //加载更多
            mFooterView.setPadding(0, 0, 0, 0);//让footerView显示出来
            setSelection(getCount());//跳到最后一条（即加载出来的东西
            if (mListener != null) {
                mListener.onLoadMore();
            }
        }
    }
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        //状态滑动的时候调用（滑翔ing）
        System.out.println("滑翔");
    }
    /**
     * 刷新结束，回复界面
     */
    //最后下拉刷新出来数据之后要进行界面的恢复
    public void onRefreshComplete() {
        if (isLoadingMore) {
            //加载更多
            mFooterView.setPadding(0, -mFooterHinght, 0, 0);
            isLoadingMore = false;

        } else {
            //下拉刷新
            currentState = PULL_TO_REFRESH;
            mTitleText.setText("下拉刷新");//重新设置回下拉刷新
            mListHeaderView.setPadding(0, -measuredHeight, 0, 0);
            pb.setVisibility(View.INVISIBLE);//进度框也隐藏
            mArrowView.setVisibility(View.VISIBLE);
            String time = getTime();
            mlastTimeDesc.setText("最后的刷新时间：" + time);
        }
    }

    /**
     * 获取系统当前的时间
     *
     * @return
     */
    private String getTime() {
        long currentTime = System.currentTimeMillis();
        //正则表达式
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy—MM-dd HH:mm:ss");
        //格式化
        return dateFormat.format(currentTime);
    }

}
