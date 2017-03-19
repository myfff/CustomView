package AnimaltionUtils;

import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.RelativeLayout;

/**
 * Created by yh on 2016/11/30.
 */

public class AnimaltionUtil {
    public   static  int RunningAnimation=0;
    public  static void rotateOutAnimal(RelativeLayout layout,long delay){
            //由于补间动画执行完毕后
            int childcount=layout.getChildCount();
            //如果隐藏，则找到所有View，禁用子控件的点击事件
            for(int i=0;i<childcount;i++){
                layout.getChildAt(i).setEnabled(false);
            }
            RotateAnimation rotateAnimation=new RotateAnimation(
                    0f,-180f, //逆时针旋转（逆时针变小，顺时针变大）
                    Animation.RELATIVE_TO_SELF,0.5f,//以x轴的中心， y的全部为旋转中心
                    Animation.RELATIVE_TO_SELF,1.0f);
            rotateAnimation.setFillAfter(true);//停留在最终移动的位置
            rotateAnimation.setDuration(500);//设置动画执行的动作
            rotateAnimation.setStartOffset(delay);//设置延时加载动画
            rotateAnimation.setAnimationListener(new MyAnimaltionListener());

            layout.startAnimation(rotateAnimation);

        }


    public static void rotateInAnimal(RelativeLayout layout, int delay) {
        int childcount=layout.getChildCount();
        //如果显示，则所有的子View，让点击可用
        for(int i=0;i<childcount;i++){
            layout.getChildAt(i).setEnabled(true);
        }
        RotateAnimation rotateAnimation=new RotateAnimation(
                -180f,0f, //逆时针旋转（逆时针变小，顺时针变大）(如果顺时针，就是-180,0)
                Animation.RELATIVE_TO_SELF,0.5f,//以x轴的中心， y的全部为旋转中心
                Animation.RELATIVE_TO_SELF,1.0f);
        rotateAnimation.setFillAfter(true);//停留在最终移动的位置
        rotateAnimation.setDuration(500);
        rotateAnimation.setStartOffset(delay);//设置延时加载动画
        rotateAnimation.setAnimationListener(new MyAnimaltionListener());

        layout.startAnimation(rotateAnimation);
    }

    //实现此接口是当我们不停地点击时动画执行小伙不被影响
    static  class  MyAnimaltionListener implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {
            RunningAnimation++;
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            RunningAnimation--;
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }
}
