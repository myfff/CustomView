package aygxy.view;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;

import AnimaltionUtils.AnimaltionUtil;
/*优酷菜单
* 1，（移出时时）二级决定三级，一级决定2,3级 （先判断3级存在，在让2级延迟动画，并最终2,3级都消失：不在直接1决定2）
* 2，（移进时），2决定3，1决定2，
* 3，移动动画时要注意我们肯多次点击，要在util做动画的监听，判断动有动画执行时，就不会响应
* 4，最后重写onKeyDown，注意按下手机的menu时，我们需要消费此事件，移除顺序延迟3,2,1，移进不延迟1，2,3，*/
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private boolean islevel1Display = true;
    private boolean islevel2Display = true;
    private boolean islevel3Display = true;
    private RelativeLayout rl_level1;
    private RelativeLayout rl_level2;
    private RelativeLayout rl_level3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化化View
        initViews();
    }

    private void initViews() {
        findViewById(R.id.ib_home).setOnClickListener(this);
        findViewById(R.id.ib_menu).setOnClickListener(this);
        rl_level1 = (RelativeLayout) findViewById(R.id.rl_level1);
        rl_level2 = (RelativeLayout) findViewById(R.id.rl_level2);
        rl_level3 = (RelativeLayout) findViewById(R.id.rl_level3);
    }
    /*** @param v
     * 按钮的点击事件
     */
    @Override
    public void onClick(View v) {
        if(AnimaltionUtil.RunningAnimation>0){
            return;
        }
        switch (v.getId()) {
            case R.id.ib_menu://二级中的
                if (islevel3Display) {
                    //如果三级菜单显示，则转出去（补间动画，旋转180，抽取工具类）
                    AnimaltionUtil.rotateOutAnimal(rl_level3,0);
                    //islevel3Display = false;
                } else {
                    //如果三级菜单显示，则转出来
                    AnimaltionUtil.rotateInAnimal(rl_level3, 0);
//                    islevel3Display = true;
                }
                //至反
                islevel3Display=!islevel3Display;
                break;
            case R.id.ib_home://一级中
                long delay=0;
                if (islevel2Display) {
                    if (islevel3Display) {
                        //如果三级菜单显示，则转出去（补间动画，旋转180，抽取工具类）
                        AnimaltionUtil.rotateOutAnimal(rl_level3,0);
                        islevel3Display = false;
                        delay+=200;
                    }
                    //如果二级菜单显示，则转出去
                    AnimaltionUtil.rotateOutAnimal(rl_level2,delay);
                  //islevel2Display = false;
                } else {
                    //如果二级菜单显示，则转出来(这里我只做了处理转出来一个)
                    AnimaltionUtil.rotateInAnimal(rl_level2, 0);
                   /*  /*//*下面的2行代码可加可不加
                    AnimaltionUtil.rotateInAnimal(rl_level3,200);
                    islevel3Display=true;*/
                     //islevel2Display = true;
                }
                //置反
                islevel2Display=!islevel2Display;
                break;
        }
    }
    /**
     * @param keyCode
     * @param event
     * @return
     * 菜单按钮的事件
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //keyCode 按钮的事件
        //点击手机menu是要全部隐藏 或者全部显示，所以我们需要进行对一级先判断
        if (keyCode==(event.KEYCODE_MENU)){
            //此处还需要用到判断，当前只能准许一个动画执行
            if(AnimaltionUtil.RunningAnimation>0){
                return true;//返回true，代表了已经响应了按钮的按下事件。就不会再向下执行（消费了当前事件）
            }
            //转出去，大的先开始
            if(islevel1Display){
                int dalay=0;
                //对三级判断
                if(islevel3Display){
                    AnimaltionUtil.rotateOutAnimal(rl_level3,0);
                    islevel3Display=false;
                    dalay+=200;
                }
                if(islevel2Display){
                    AnimaltionUtil.rotateOutAnimal(rl_level2,dalay);
                    islevel2Display=false;
                    dalay+=200;
                }
                AnimaltionUtil.rotateOutAnimal(rl_level1,dalay);
            }else {
                //转进来，从小的开始
                AnimaltionUtil.rotateInAnimal(rl_level1,0);
                AnimaltionUtil.rotateInAnimal(rl_level2,200);
               AnimaltionUtil.rotateInAnimal(rl_level3,400);
                islevel3Display=true;
                islevel2Display=true;}
            islevel1Display=!islevel1Display;
        }
        return true;
    }

}


