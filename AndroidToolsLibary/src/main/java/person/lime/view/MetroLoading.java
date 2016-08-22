package person.lime.view;

import android.content.res.TypedArray;
import android.view.View;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.animation.Interpolator;

import person.lime.tools.DensityUtil;
import person.lime.tools.R;

/**
 * 一个加载动画特效.
 * 此代码借鉴于guohuanwen作者，https://github.com/guohuanwen
 * Created by Lime(李振宇) on 2016/08/17.
 */
public class MetroLoading extends View {

    private final int m;
    private final float pi;
    private final String TAG;
    private int r;
    private int n;
    private float circleR;
    private Paint paint;
    private int color;

    private ValueAnimator[] animators;
    private float[] xs, ys;
    private boolean init;
    private float[] circleCentre;
    private float[][] start;

    //非静态变量初始化
    {
        //常量
        m = 2;
        pi = (float) Math.PI;
        TAG = "MetroLoading";
        //变量
        n = 4;
        init = true;
        color = 0xFFFF4444;
    }


    public MetroLoading(Context context, AttributeSet attrs) {
        super(context, attrs);
        r = DensityUtil.dip2px(context,5);
        //获得私有属性
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MetroLoading);
        n = ta.getInteger(R.styleable.MetroLoading_count, n);
        color = ta.getColor(R.styleable.MetroLoading_circle_color, color);
        r = ta.getDimensionPixelOffset(R.styleable.MetroLoading_circle_size,r);
        ta.recycle();
        init();
    }

    public MetroLoading(Context context) {
        super(context);
        init();
    }

    protected void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        start = new float[n][m];
        animators = new ValueAnimator[n];
        xs = new float[n];
        ys = new float[n];
    }

    /**
     * 设置小圆点颜色
     *
     * @param color 颜色值
     */
    public void setCircleColor(int color) {
        paint.setColor(color);
        this.color = color;
        invalidate();
    }

    /**
     * 设置小圆点个数
     *
     * @param n 个数
     */
    public void setCount(int n) {
        init();
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //初始化
        if (init) {
            circleCentre = new float[]{getWidth() / 2, getHeight() / 2};
            start[0] = new float[]{getWidth() / 2, r};
            for (int i = 1; i < n; i++)
                start[i] = onCiecleCoordinate(0.5f, start[i - 1], circleCentre);
            init = false;
            loading();
        }

        for (int i = 0; i < n; i++) {
            //初始化位置
            if (!animators[i].isRunning())
                canvas.drawCircle(start[i][0], start[i][1], r, paint);
            else {
                xs[i] = (float) (circleCentre[0] + circleR * Math.cos((float) animators[i].getAnimatedValue()));
                ys[i] = (float) (circleCentre[1] + circleR * Math.sin((float) animators[i].getAnimatedValue()));
                canvas.drawCircle(xs[i], ys[i], r, paint);
            }
        }
        for (ValueAnimator animator : animators)
            if (animator.isRunning()) {
                invalidate();
                break;
            }
    }


    private void loading() {

        for (int i = 0; i < n; i++) {
            animators[i] = getCircleData(start[i], circleCentre, i * 300);
            animators[i].start();
        }
        postDelayed(new Runnable() {
            @Override
            public void run() {
                loading();
                invalidate();
            }
        }, animators[n - 1].getDuration() + (n - 1) * 300);
    }

    private SlowToQuick slowToQuick = new SlowToQuick();

    private ValueAnimator getCircleData(float[] startCoordinate, float[] RCoordinate, int delay) {
        float x1 = startCoordinate[0];
        float y1 = startCoordinate[1];
        float x0 = RCoordinate[0];
        float y0 = RCoordinate[1];
        //Log.i(TAG, "getCircleData x y: " + x1+"  ,"+y1+"  x0  "+x0+ " y0  "+y0);
        circleR = (float) Math.sqrt((x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0));
        float param = (float) (Math.abs(y1 - y0) / circleR);
        if (param < -1.0) {
            param = -1.0f;
        } else if (param > 1.0) {
            param = 1.0f;
        }
        float a = (float) Math.asin(param);
        if (x1 >= x0 && y1 >= y0) {
            a = a;
        } else if (x1 < x0 && y1 >= y0) {
            a = pi - a;
        } else if (x1 < x0 && y1 < y0) {
            a = a + pi;
        } else {
            a = 2 * pi - a;
        }
        ValueAnimator circleAnimator = ValueAnimator.ofFloat(a, a + 2 * pi);
        circleAnimator.setDuration(1500);
        circleAnimator.setInterpolator(slowToQuick);
        circleAnimator.setStartDelay(delay);
        return circleAnimator;
    }

    //获取同一个圆上，间隔固定角度的点坐标
    private float[] onCiecleCoordinate(float angle, float[] start, float[] center) {
        float x1 = start[0];
        float y1 = start[1];
        float x0 = center[0];
        float y0 = center[1];
        float R = (float) Math.sqrt((x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0));
        float param = (float) (Math.abs(y1 - y0) / R);
        if (param < -1.0) {
            param = -1.0f;
        } else if (param > 1.0) {
            param = 1.0f;
        }
        float a = (float) Math.asin(param);
        if (x1 >= x0 && y1 >= y0) {
            a = a;
        } else if (x1 < x0 && y1 >= y0) {
            a = pi - a;
        } else if (x1 < x0 && y1 < y0) {
            a = a + pi;
        } else {
            a = 2 * pi - a;
        }
        float x = (float) (center[0] + R * Math.cos(a + angle));
        float y = (float) (center[1] + R * Math.sin(a + angle));
        return new float[]{x, y};
    }

    private class SlowToQuick implements Interpolator {
        @Override
        public float getInterpolation(float input) {
            return input * input;
        }
    }

}
