package com.dodola.animview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by dodola on 15/7/27.
 */
public class MetaballDebugView extends View {

    private Paint paint = new Paint();


    private float handleLenRate = 2f;
    private final float radius = 60;
    private final float SCALE_RATE = 0.3f;
    private ArrayList<Circle> circlePaths = new ArrayList<>();
    private float mv = 0.6f;
    private float maxDistance = radius * 4;

    public MetaballDebugView(Context context) {
        super(context);
        init();
    }

    public MetaballDebugView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MetaballDebugView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    public float getMv() {
        return mv;
    }

    public void setMv(float mv) {
        this.mv = mv;
        invalidate();
    }

    public float getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(float maxDistance) {
        this.maxDistance = maxDistance;
        invalidate();
    }

    public float getHandleLenRate() {
        return handleLenRate;
    }

    public void setHandleLenRate(float handleLenRate) {
        this.handleLenRate = handleLenRate;
        invalidate();
    }

    private class Circle {
        float[] center;
        float radius;
    }

    public void setPaintMode(int mode) {
        paint.setStyle(mode == 0 ? Paint.Style.STROKE : Paint.Style.FILL);
        invalidate();
    }

    private void init() {
        paint.setColor(0xff4db9ff);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

    }

    private void initMetaballs() {
        Circle circlePath = new Circle();
        circlePath.center = new float[]{(radius), radius};
        circlePath.radius = radius;
        circlePaths.add(circlePath);

        circlePath = new Circle();
        circlePath.center = new float[]{this.getMeasuredWidth() / 2, this.getMeasuredHeight() / 3};
        circlePath.radius = radius;
        circlePaths.add(circlePath);
    }

    private float[] getVector(float radians, float length) {
        float x = (float) (Math.cos(radians) * length);
        float y = (float) (Math.sin(radians) * length);
        return new float[]{
                x, y
        };
    }

    /**
     * @param canvas          画布
     * @param j
     * @param i
     * @param v               控制两个圆连接时候长度，间接控制连接线的粗细，该值为1的时候连接线为直线
     * @param handle_len_rate
     * @param maxDistance
     */
    private void metaball(Canvas canvas, int j, int i, float v, float handle_len_rate, float maxDistance) {
        final Circle circle1 = circlePaths.get(i);
        final Circle circle2 = circlePaths.get(j);

        RectF ball1 = new RectF();
        ball1.left = circle1.center[0] - circle1.radius;
        ball1.top = circle1.center[1] - circle1.radius;
        ball1.right = ball1.left + circle1.radius * 2;
        ball1.bottom = ball1.top + circle1.radius * 2;

        RectF ball2 = new RectF();
        ball2.left = circle2.center[0] - circle2.radius;
        ball2.top = circle2.center[1] - circle2.radius;
        ball2.right = ball2.left + circle2.radius * 2;
        ball2.bottom = ball2.top + circle2.radius * 2;

        float[] center1 = new float[]{
                ball1.centerX(),
                ball1.centerY()
        };
        float[] center2 = new float[]{
                ball2.centerX(),
                ball2.centerY()
        };
        float d = getDistance(center1, center2);

        float radius1 = ball1.width() / 2;
        float radius2 = ball2.width() / 2;
        float pi2 = (float) (Math.PI / 2);
        float u1, u2;


        if (d > maxDistance) {
            canvas.drawCircle(ball2.centerX(), ball2.centerY(), circle2.radius, paint);
        } else {
            float scale2 = 1 + SCALE_RATE * (1 - d / maxDistance);
            radius2 *= scale2;
            canvas.drawCircle(ball2.centerX(), ball2.centerY(), radius2, paint);
        }

        Log.d("Metaball_radius", "radius1:" + radius1 + ",radius2:" + radius2);
        if (radius1 == 0 || radius2 == 0) {
            return;
        }

        if (d > maxDistance || d <= Math.abs(radius1 - radius2)) {
            return;
        } else if (d < radius1 + radius2) {
            u1 = (float) Math.acos((radius1 * radius1 + d * d - radius2 * radius2) /
                    (2 * radius1 * d));
            u2 = (float) Math.acos((radius2 * radius2 + d * d - radius1 * radius1) /
                    (2 * radius2 * d));
        } else {
            u1 = 0;
            u2 = 0;
        }
        Log.d("Metaball", "center2:" + Arrays.toString(center2) + ",center1:" + Arrays.toString(center1));
        float[] centermin = new float[]{center2[0] - center1[0], center2[1] - center1[1]};

        float angle1 = (float) Math.atan2(centermin[1], centermin[0]);
        float angle2 = (float) Math.acos((radius1 - radius2) / d);
        float angle1a = angle1 + u1 + (angle2 - u1) * v;
        float angle1b = angle1 - u1 - (angle2 - u1) * v;
        float angle2a = (float) (angle1 + Math.PI - u2 - (Math.PI - u2 - angle2) * v);
        float angle2b = (float) (angle1 - Math.PI + u2 + (Math.PI - u2 - angle2) * v);

        Log.d("Metaball", "angle1:" + angle1 + ",angle2:" + angle2 + ",angle1a:" + angle1a + ",angle1b:" + angle1b + ",angle2a:" + angle2a + ",angle2b:" + angle2b);


        float[] p1a1 = getVector(angle1a, radius1);
        float[] p1b1 = getVector(angle1b, radius1);
        float[] p2a1 = getVector(angle2a, radius2);
        float[] p2b1 = getVector(angle2b, radius2);

        float[] p1a = new float[]{p1a1[0] + center1[0], p1a1[1] + center1[1]};
        float[] p1b = new float[]{p1b1[0] + center1[0], p1b1[1] + center1[1]};
        float[] p2a = new float[]{p2a1[0] + center2[0], p2a1[1] + center2[1]};
        float[] p2b = new float[]{p2b1[0] + center2[0], p2b1[1] + center2[1]};


        Log.d("Metaball", "p1a:" + Arrays.toString(p1a) + ",p1b:" + Arrays.toString(p1b) + ",p2a:" + Arrays.toString(p2a) + ",p2b:" + Arrays.toString(p2b));

        float[] p1_p2 = new float[]{p1a[0] - p2a[0], p1a[1] - p2a[1]};

        float totalRadius = (radius1 + radius2);
        float d2 = Math.min(v * handle_len_rate, getLength(p1_p2) / totalRadius);
        d2 *= Math.min(1, d * 2 / (radius1 + radius2));
        Log.d("Metaball", "d2:" + d2);
        radius1 *= d2;
        radius2 *= d2;

        float[] sp1 = getVector(angle1a - pi2, radius1);
        float[] sp2 = getVector(angle2a + pi2, radius2);
        float[] sp3 = getVector(angle2b - pi2, radius2);
        float[] sp4 = getVector(angle1b + pi2, radius1);
        Log.d("Metaball", "sp1:" + Arrays.toString(sp1) + ",sp2:" + Arrays.toString(sp2) + ",sp3:" + Arrays.toString(sp3) + ",sp4:" + Arrays.toString(sp4));


        Path path1 = new Path();
        path1.moveTo(p1a[0], p1a[1]);
        path1.cubicTo(p1a[0] + sp1[0], p1a[1] + sp1[1], p2a[0] + sp2[0], p2a[1] + sp2[1], p2a[0], p2a[1]);
        path1.lineTo(p2b[0], p2b[1]);
        path1.cubicTo(p2b[0] + sp3[0], p2b[1] + sp3[1], p1b[0] + sp4[0], p1b[1] + sp4[1], p1b[0], p1b[1]);
        path1.lineTo(p1a[0], p1a[1]);
        path1.close();
        canvas.drawPath(path1, paint);

    }

    private float getLength(float[] b) {
        return (float) Math.sqrt(b[0] * b[0] + b[1] * b[1]);
    }

    private float getDistance(float[] b1, float[] b2) {
        float x = b1[0] - b2[0];
        float y = b1[1] - b2[1];
        float d = x * x + y * y;
        return (float) Math.sqrt(d);
    }


    //测试用
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Circle circle = circlePaths.get(0);
        circle.center[0] = event.getX();
        circle.center[1] = event.getY();
        invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (circlePaths.size() == 0) {
            initMetaballs();
        }


        final Circle circle1 = circlePaths.get(0);
        RectF ball1 = new RectF();
        ball1.left = circle1.center[0] - circle1.radius;
        ball1.top = circle1.center[1] - circle1.radius;
        ball1.right = ball1.left + circle1.radius * 2;
        ball1.bottom = ball1.top + circle1.radius * 2;
        canvas.drawCircle(ball1.centerX(), ball1.centerY(), circle1.radius, paint);

        for (int i = 1; i < circlePaths.size(); i++) {
            metaball(canvas, i, 0, mv, handleLenRate, maxDistance);
        }
    }

}
