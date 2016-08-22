package person.lime.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.Button;

import person.lime.tools.DensityUtil;
import person.lime.tools.R;

/**
 * 这是一个左边含有一个色块的Button
 * Created by Lime on 2016-08-18.
 */
public class LeftBlockButton extends Button {

    /**
     * 圆形
     */
    public static final int SHAPE_CIRCLE = 1;

    /**
     * 矩形
     */
    public static final int SHAPE_RECT = 2;

    /**
     * 色块颜色值
     */
    private int color;

    /**
     * 画笔
     */
    private Paint paint;

    /**
     * 绘制色块的形状
     */
    private int shape;

    /**
     * 圆形偏移量
     */
    private int circleOffset;

    /**
     * 矩形偏移量
     */
    private int rectOffset;

    //初始化
    {
        color = 0xFF000000;
        shape = SHAPE_CIRCLE;
    }


    public LeftBlockButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        //获得私有属性
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.LeftBlockButton);
        color = ta.getColor(R.styleable.LeftBlockButton_block_color, color);
        shape = ta.getInteger(R.styleable.LeftBlockButton_block_shape, shape);
        ta.recycle();
        initData();
    }

    public LeftBlockButton(Context context) {
        super(context);
        initData();
    }

    public void initData() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
    }


    /**
     * 设置色块颜色
     *
     * @param color 颜色值
     */
    public void setBlockColor(int color) {
        this.color = color;
        paint.setColor(color);
        invalidate();
    }

    /**
     * 设置色块形状
     *
     * @param shape 形状
     */
    public void setBlockShape(int shape) {
        switch (shape) {
            case SHAPE_CIRCLE:
                this.shape = SHAPE_CIRCLE;
                break;
            case SHAPE_RECT:
                this.shape = SHAPE_RECT;
                break;
            default:
                this.shape = SHAPE_CIRCLE;
                break;
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        circleOffset = getHeight() >> 1;
        rectOffset = circleOffset >> 1;
        drawShape(canvas);
        //位移
        canvas.translate(rectOffset + getHeight(), 0);
        super.onDraw(canvas);
    }

    private void drawShape(Canvas canvas) {
        int h = getHeight();
        switch (shape) {
            case SHAPE_CIRCLE:
                canvas.drawCircle(circleOffset + getHeight() >> 1, h >> 1, h - circleOffset >> 1, paint);
                break;
            case SHAPE_RECT:
                canvas.drawRect(rectOffset, rectOffset, h - rectOffset, h - rectOffset, paint);
                break;
        }
    }
}
