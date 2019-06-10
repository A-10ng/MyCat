package top.longsh1z.www.mycat.viewholder;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

/**
 * Created by zhanglei on 2017/6/16.
 */

public class BackgroundDrawable extends ShapeDrawable {

    private int topColor;
    private int bottomColor;
    private int leftPercent;
    private int rightPercent;
    private Paint topPaint;
    private Paint bottomPaint;

    private BackgroundDrawable(Builder builder) {
        super(builder.shape);

        topColor = builder.topColor;
        bottomColor = builder.bottomColor;
        leftPercent = builder.leftPercent;
        rightPercent = builder.rightPercent;

        topPaint = new Paint();
        topPaint.setStyle(Paint.Style.FILL);
        topPaint.setAntiAlias(true);
        topPaint.setColor(topColor);

        bottomPaint = new Paint();
        bottomPaint.setStyle(Paint.Style.FILL);
        bottomPaint.setAntiAlias(true);
        bottomPaint.setColor(bottomColor);

    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Rect r = getBounds();
        canvas.drawRect(r, topPaint);

        /** 噔噔噔。。。
         *  重头戏来了，下面是绘制斜切背景图的逻辑注意看
         * */

        //背景左侧的bottom值
        int lBottom;
        //背景右侧的bottom值
        int rBottom;
        if (leftPercent > 0 && rightPercent > 0 && leftPercent < 100 && rightPercent < 100) {

            lBottom = (int) (r.bottom * leftPercent * 0.01);
            rBottom = (int) (r.bottom * rightPercent * 0.01);
            //使用path类来绘制不规则图形，形成斜切的效果
            Path path = new Path();
            path.lineTo(r.left, r.top);
            path.lineTo(r.left, lBottom);
            path.lineTo(r.right, rBottom);
            path.lineTo(r.right, r.top);

            //背景下半部分的rect
            Rect bRect = new Rect(r.left, r.top, r.right, r.bottom);

            //开始绘制
            canvas.drawRect(bRect, bottomPaint);
            canvas.drawPath(path, topPaint);


        } else {
            //用户输入的参数有误，我们要给他错误提示
            topPaint.setColor(Color.WHITE);
            canvas.drawRect(r, topPaint);

            TextPaint textPaint = new TextPaint();
            textPaint.setARGB(0xFF, 0xFF, 0, 0);
            textPaint.setColor(Color.BLACK);
            textPaint.setTextSize(40);
            textPaint.setStrokeWidth(5);
            StaticLayout layout = new StaticLayout("大兄弟，你敢不敢把参数给我设置对了呀，不要瞎搞好吗？\n left和right的值是一定要设置的，并且left和right的值要大于0小于100，懂了吧大兄弟。", textPaint, r.right,
                    Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
            canvas.translate(20, r.bottom / 3);//从20，r.bottom / 3开始画
            layout.draw(canvas);
        }

    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder implements IShapeDrawableBuilder {

        private RectShape shape;
        private int topColor;
        private int bottomColor;
        private int leftPercent;
        private int rightPercent;

        private Builder() {
            shape = new RectShape();
            topColor = Color.WHITE;
            bottomColor = Color.WHITE;
            leftPercent = 0;
            rightPercent = 0;
        }

        @Override
        public IShapeDrawableBuilder left(int percent) {
            leftPercent = percent;
            return this;
        }

        @Override
        public IShapeDrawableBuilder right(int percent) {
            rightPercent = percent;
            return this;
        }

        @Override
        public IShapeDrawableBuilder topColor(int topColor) {
            this.topColor = topColor;
            return this;
        }

        @Override
        public IShapeDrawableBuilder bottomColor(int bottomColor) {
            this.bottomColor = bottomColor;
            return this;
        }

        @Override
        public BackgroundDrawable build() {
            return new BackgroundDrawable(this);
        }
    }

    public interface IShapeDrawableBuilder {
        public IShapeDrawableBuilder left(int percent);

        public IShapeDrawableBuilder right(int percent);

        public IShapeDrawableBuilder topColor(int topColor);

        public IShapeDrawableBuilder bottomColor(int bottomColor);

        public BackgroundDrawable build();
    }

}

