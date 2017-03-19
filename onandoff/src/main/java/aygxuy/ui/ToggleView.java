package aygxuy.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by yh on 2016/12/1.
 */

/**
 * toggle 切换键
 * 自定义开关,
 * android 界面绘制流程
 * mersure——》layout——》draw
 * onMersuer  onLayout  ondraw重写这些方法，实现自定义
 *
 * View：
 * onMersuer（此方法里面指定自己的宽高，）     ondraw（此方法绘制自己的内容）
 * ViewGroup：
 * onMersuer（此方法里面指定自己的宽高，孩子的宽高）    onLayout（摆放孩子的布局）  ondraw（此方法绘制自己的内容和孩子的内容）
 *
 */
public class ToggleView extends View {

    private Bitmap switchBackgroundBitmap;
    private Paint paint;
    private Bitmap slideButtonBitmap;
    private boolean mSwitchState=false;
    private float currentX;
    private  OnSwitchStateUpdateLister onSwitchStateUpdateLister;
    private boolean state;

    /**
     * 用于代码创建时使用
     *
     * @param context
     */
    public ToggleView(Context context) {
        super(context);
        init();
    }
    private void init() {
        paint = new Paint();
    }
    /**
     * xml使用，可指定自定义属性
     *
     * @param context
     * @param attrs
     */
    public ToggleView(Context context, AttributeSet attrs) {
        super(context, attrs); init();
    }
    /**
     * xml使用，可指定自定义属性，如果有指定样式，也会走此方法
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public ToggleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr); init();
    }
    /**
     * 设置背景图片
     *
     * @param switchbackground
     */
    public void setSwitchBackgroundResource(int switchbackground) {
        //利用位图工厂转化为Bitmap
        switchBackgroundBitmap = BitmapFactory.decodeResource(getResources(), switchbackground);

    }

    /**
     * 设置滑块图片资源
     *
     * @param slidebutton
     */
    public void setSlideButtonResouce(int slidebutton) {
        slideButtonBitmap = BitmapFactory.decodeResource(getResources(), slidebutton);
    }

    /**
     * 设置开关状态
     */
    public void setSwitchstate(Boolean mSwitchState) {
        this.mSwitchState=mSwitchState;
    }


    //以下三个是在获取焦点(onresume)后才会执行
    /**获取组件的宽高
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //背景图片的宽高
        setMeasuredDimension(switchBackgroundBitmap.getWidth(),switchBackgroundBitmap.getHeight());
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    //canvas画布，画板，在上面绘制的内容都会显示到界面上（此方法会经常执行）
    @Override
    protected void onDraw(Canvas canvas) {
        //1 绘制背景
        canvas.drawBitmap(switchBackgroundBitmap,10,0,paint);

        //2 *****绘制滑块,（并滑动滑块，先通过isTouchMode是否触摸滑动滑块，然后当我们不触摸的时候就根据开关状态）
        if(isTouchMode){
            //*****根据当前的用户触摸的位置滑动滑块
            float newleft= currentX-slideButtonBitmap.getWidth()/2.0f;//让划片移动自身大小一半的距离

            float maxleft=switchBackgroundBitmap.getWidth()-slideButtonBitmap.getWidth();
            //限定滑块左右移动的范围
            if(newleft<0){
                newleft=0;//左边越界（相对左边）
            } else if(newleft>maxleft){
                newleft=maxleft;//右边越界（都是相对左边）
            }
            canvas.drawBitmap(slideButtonBitmap,newleft,0,paint);
        }else {
            //根据开关状态，直接设置图片位置
            if(mSwitchState){//开
                int newLift=switchBackgroundBitmap.getWidth()-slideButtonBitmap.getWidth();
                canvas.drawBitmap(slideButtonBitmap,newLift,0,paint);
            }else {//关
                canvas.drawBitmap(slideButtonBitmap,0,0,paint);
            }
        }
    }

    //重写触摸事件，相应用户的触摸
    boolean isTouchMode=false;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                isTouchMode=true;//当按下为true
                System.out.println("event:ACTION_DOWN"+event.getX());
                currentX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                isTouchMode=false;//当抬起为true
                System.out.println("event:ACTION_UP"+event.getX());
                currentX = event.getX();

                //在松手的时候判断是哪个状态，记录下来，在下次我们没有触摸时显示界面
                float center=switchBackgroundBitmap.getWidth()/2.0f;
                //根据当前抬起的位置，和控件中心位置进行比较，大于返回true则显示开，否则返回flase就显示关
                state = currentX>center;

                //【4】外部/界面就会收到此事件做出相应的响应
                //如果开关状态改变了，通知界面，里面开关状态改变了
                //以上四部为回调
                //(state!=mSwitchState 表示转态发生了改变，改变后的状态为state，将此状态通知给外界)
                if(state!=mSwitchState && onSwitchStateUpdateLister!=null ){
                    onSwitchStateUpdateLister.onStateUpdate(state);
                }
                mSwitchState= state;//再将当前状态给mSwitchState
                break;
            default: break;
        }
        //重绘画面
        invalidate();//会引发ondraw被重新调用，
        return  true;//代表了相应了此事件（消费了此事件），才可以收到其他事件
    }
    //【1】声明接口对象
    public  interface  OnSwitchStateUpdateLister{
        //状态回调，将当前状态传递出去
        void onStateUpdate(boolean state);
    }
    //【2】增加设置接口对象的方法 ，玩不进行调用
    public  void setOnSwitchStateUpdateLister(OnSwitchStateUpdateLister onSwitchStateUpdateLister){
         this.onSwitchStateUpdateLister=onSwitchStateUpdateLister;
    }
}
