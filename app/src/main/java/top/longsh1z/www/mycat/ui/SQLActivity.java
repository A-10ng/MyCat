package top.longsh1z.www.mycat.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import top.longsh1z.www.mycat.R;

public class SQLActivity extends AppCompatActivity {

    String data;
    ObjectAnimator objectAnimator1;
    ObjectAnimator objectAnimator2;
    ObjectAnimator objectAnimator3;
    private TextView textView;
    private Button btn_top;
    private Button btn_middle;
    private Button btn_bottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sql);

        initViews();
        setAnimation();

        btn_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                objectAnimator1.pause();
                objectAnimator2.pause();
                setMiddleViewAnimation();
                setBottomViewAnimation();
            }
        });
    }

    private void initViews() {
        btn_top = findViewById(R.id.btn_top);
        btn_middle = findViewById(R.id.btn_middle);
        btn_bottom = findViewById(R.id.btn_bottom);
    }

    private void setAnimation() {
        objectAnimator1 = ObjectAnimator.ofFloat(btn_top, "translationY",
                btn_top.getY() + 0, btn_top.getY() - 30);
        objectAnimator1.setDuration(3000);
        objectAnimator1.setRepeatMode(ObjectAnimator.REVERSE);
        objectAnimator1.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator1.start();

        objectAnimator2 = ObjectAnimator.ofFloat(btn_middle, "translationY",
                btn_top.getY() + 0, btn_top.getY() - 30);
        objectAnimator2.setDuration(3000);
        objectAnimator2.setRepeatMode(ObjectAnimator.REVERSE);
        objectAnimator2.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator2.start();

        objectAnimator3 = ObjectAnimator.ofFloat(btn_bottom, "translationY",
                btn_top.getY() + 0, btn_top.getY() - 30);
        objectAnimator3.setDuration(3000);
        objectAnimator3.setRepeatMode(ObjectAnimator.REVERSE);
        objectAnimator3.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator3.start();
    }

    private void setMiddleViewAnimation() {
        AnimatorSet set = new AnimatorSet();

        ObjectAnimator animator = ObjectAnimator.ofFloat(btn_middle, "rotation", 0f, 315f);

        ObjectAnimator animator1 = ObjectAnimator.ofFloat(btn_middle,"translationX",
                0,100);

        ObjectAnimator animator2 = ObjectAnimator.ofFloat(btn_middle,"translationY",
                0,-100);

        set.playTogether(animator,animator1,animator2);
        set.setDuration(1000);
        set.start();
    }

    private void setBottomViewAnimation() {

    }

}
