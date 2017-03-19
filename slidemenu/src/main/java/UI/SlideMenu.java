package UI;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;
/*侧边栏和主面板是联系到一起的,滑动的时候是滑动的整体
* move中的时候getScrollX()是滑动开始时的一个左边界,得到的是当前滑动时一开始的位置（），当手指抬起后他得到的就是解释后的一个滚动到的新位置
*  event.getX是为了计算此次滑动的距离dx
*  拿getScrollX()+dy就是此次滑动结束后我们应该让控件所在的left
*Scrollto  滑动到哪
* Scrollby   滑动多少
* */
/*侧滑面板，重要的区分*/
/**
 * Created by yh on 2016/12/3.
 */
/*此处到底怎么了，难道我想错了
*  //getScrollX()获取当前滚动的位置
                if(getScrollX()<leftCenter){
                    //打开，切换成左边的菜单的面板
                    currentState=MENU_STATE;
                    updateCurrentContent();
                }else {
                    //关闭，切换成主面板
                    currentState=MAIN_STATE;
                    updateCurrentContent();
                }*/
public class  SlideMenu extends ViewGroup {
    private int downX;
    private static final int MAIN_STATE = 0;//主面板
    private static final int MENU_STATE = 1;//菜单
    private int currentState = MAIN_STATE;//默认为当前菜单
    private Scroller scroller;
    private int downX1;
    private int downX2;
    private int downY;

    public SlideMenu(Context context) {
        super(context);
        init();
    }
    public SlideMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public SlideMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    /**
     * 初始化滚动器，数值模拟器
     */
    private void init() {
        //初始化滚动器，数值模拟器
        scroller = new Scroller(getContext());
    }


    //一下两个方法必须同时执行
    /**
     * 测量并设置所有子孩子的宽高
     *
     * @param widthMeasureSpec  当前控件的宽度测量规则
     * @param heightMeasureSpec 当前控件的高度测量规则
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //得到它里面的第一个孩子，左面板的宽高
        View leftMenu = getChildAt(0);
        //为一种规则，但不一定最终我么会依照这种方法来摆放控件（宽我设置过可以直接拿到）
        leftMenu.measure(leftMenu.getLayoutParams().width, heightMeasureSpec);
        //指定主面板的宽高
        View mainContent = getChildAt(1);
        mainContent.measure(widthMeasureSpec, heightMeasureSpec);
    }

    /***
     * @param changed 当前控件的尺寸大小，位置是否发生改变
     * @param l                         l 当前的控件左边距 当前的控件
     * @param t                         t 当前的控件 上边距
     * @param r                         r 当前的控件 右边距
     * @param b                         b 当前的控件 下边距
     */
    //继承自ViewGroup的自定义控件必须重写此方法
    //摆放自控件，必须摆放才能显示控件
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //左面板
        View leftMenu = getChildAt(0);
        //宽度一定要用这个-leftMenu.getMeasuredWidth()
        leftMenu.layout(-leftMenu.getMeasuredWidth(), 0, 0, b);


        //主面板
        View mainContent = getChildAt(1);
        mainContent.layout(l, t, r, b);
    }

    /**
     * 【1】处理触摸事件
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) event.getX();
                break;
            case MotionEvent.ACTION_MOVE://跟随滑动
                int moveX = (int) event.getX();
                //滑动的距离，将要发生的偏移量
                int scrollX = downX - moveX;
                //计算将要滚动的位置，判断是否会超出去，超出去了，不执行scrollBy(),而是要将leftMenu要适当调整
                int newscrollPosition = getScrollX() + scrollX;
                if (newscrollPosition < -getChildAt(0).getMeasuredWidth()) {//限定左边界
                    //<-240
                    scrollTo(-getChildAt(0).getMeasuredWidth(), 0);//违反这个右边有空白
                } else if (newscrollPosition > 0) {//限定右边界
                    //>0
                    scrollTo(0, 0);//违反此边界，左边会有空白
                } else {
                    //让变化量生效， //在原有基础上滑动了
                    scrollBy(scrollX, 0);
                }
                //将最终移动的位置重新复制给一开是的位置（否则会出现异常）
                downX = moveX;
                break;
            case MotionEvent.ACTION_UP://最终状态
                //当手抬起，根据当前滚动的距离，和左面板的一般进行比较，决定是显示菜单还是隐藏
                int leftCenter = (int) (-getChildAt(0).getMeasuredWidth() / 2.0f);
                //getScrollX()获取当前滚动的位置
                if (getScrollX() < leftCenter) {
                    //打开菜单
                    currentState = MENU_STATE;
                    //  scrollTo(-getChildAt(0).getMeasuredWidth(),0);
                    updateCurrentContent();
                } else {
                    // 关闭菜单
                    currentState = MAIN_STATE;
                    //  scrollTo(0,0);
                    updateCurrentContent();
                }
                break;
            default:
                break;
        }
        return true;//消费了此事件
    }

    /**
     * 根据当前的状态，打开/关闭菜单左面板动画
     */
    private void updateCurrentContent() {
        int startX = getScrollX();//获取当前的位置（为抬起那一时刻）
        int dx = 0;
        //*****平滑滚动
        if (currentState == MENU_STATE) {
           /* 打开菜单
            scrollTo(-getChildAt(0).getMeasuredWidth(),0);*/
            //现在我们不仅仅要打开菜单，我们还要让打开的同时加载一个动画，而不是将一下子执行完成
            dx = -getChildAt(0).getMeasuredWidth() - startX;
        } else {
           /* //关闭菜单
            scrollTo(0,0);*/
            dx = 0 - startX;
        }

        //在这里开始滚动  再模拟时长里面循坏，超过时常动画执行结束

        //【1】开始平滑的数据模拟
        /*startX ：开始的x值
        startY：开始的y值
        dx：将要发生的水平距离，移动的x的距离
        dy：将要发生的垂直距离，移动的Y距离
        duration：数据模拟持续的时长
        * */
        //【1】开始模拟
        int duration = Math.abs(dx * 2);//根据移动长短来模拟事件
        scroller.startScroll(startX, 0, dx, 0, duration);
        //重绘界面——>drawChild——>computeScroll()
        invalidate();//两次重绘调用界面都不可以省略，重回界面让leftMenu不停地执行一个平滑动画
    }
    /**和上面高度连接
     * 持续动画的持续  与Scroll配合使用
     */
    @Override
    public void computeScroll() {
        super.computeScroll();
        if(scroller.computeScrollOffset()){
            //true 动画还没有结束
            //获取当前模拟的数据，也就是要滚动到的数据
            int currX=scroller.getCurrX();
            scrollTo(currX,0);
            //重绘界面，还要再次计算，一致达到（要循坏）
            invalidate();
        }
    }


    //下面为做侧边栏开关的逻辑
    /**
     * 通过按钮打开leftMenu
     */
    public  void open(){
        currentState=MENU_STATE;//至状态为开
        updateCurrentContent();//调用方法根据状态做平滑的移动
    }
    /**
     * 通过按钮关闭leftMenu
     */
    public  void close(){
        currentState=MAIN_STATE;//至状态为关
        updateCurrentContent();//调用方法根据状态做平滑的移动
    }
    //主activity中通过调用方法先获得状态，在调用次方法传递过来控制leftMenu的开关
    public void switchstate(){
        if(currentState==MAIN_STATE){//应该是反向的，如果现在左面板不在，当点击按钮就让其现在
            //开
           open();
        }else {
            //关
            close ();
        }
    }
    public  int getCurrentState(){
        return currentState;
    }


    /**拦截判断   X轴Y轴
     * @param ev
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case  MotionEvent.ACTION_DOWN:
                //计算一开始x.y轴位置
                downX2 = (int) ev.getX();
                downY = (int) ev.getY();
                break;
            case  MotionEvent.ACTION_MOVE:
                int xOffset= (int) Math.abs(ev.getX()- downX2);
                int yOffset= (int) Math.abs(ev.getY()-downY);
                if(xOffset>yOffset && xOffset>5){
                    return  true;//拦截此次界面的滚动，不让孩子去处理此事件。而是让主界面先处理
                }
                break;
            case   MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        //fouze的话就会返回super的此方法的返回值，为false
        return super.onInterceptTouchEvent(ev);
    }
}
