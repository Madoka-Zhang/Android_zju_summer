package com.bytedance.camp.chapter4.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ClockView extends View {

    private static final int FULL_CIRCLE_DEGREE = 360;
    private static final int UNIT_DEGREE = 6;
    private static final int OFF_DEGREE = 90;

    private static final float UNIT_LINE_WIDTH = 8; // 刻度线的宽度
    private static final int HIGHLIGHT_UNIT_ALPHA = 0xFF;
    private static final int NORMAL_UNIT_ALPHA = 0x80;

    private static final float HOUR_NEEDLE_LENGTH_RATIO = 0.4f; // 时针长度相对表盘半径的比例
    private static final float MINUTE_NEEDLE_LENGTH_RATIO = 0.55f; // 分针长度相对表盘半径的比例
    private static final float SECOND_NEEDLE_LENGTH_RATIO = 0.7f; // 秒针长度相对表盘半径的比例
    private static final float SECOND_NEEDLE_EX_RATIO = 0.07f;
    private static final float HOUR_NEEDLE_WIDTH = 12; // 时针的宽度
    private static final float MINUTE_NEEDLE_WIDTH = 8; // 分针的宽度
    private static final float SECOND_NEEDLE_WIDTH = 4; // 秒针的宽度

    private Calendar calendar = Calendar.getInstance();

    private float radius = 10; // 表盘半径
    private float centerX = 0; // 表盘圆心X坐标
    private float centerY = 0; // 表盘圆心Y坐标

    private List<RectF> unitLinePositions = new ArrayList<>();
    private Paint cirPaint = new Paint();
    private Paint unitPaint = new Paint();
    private Paint needlePaint = new Paint();
    private Paint numberPaint = new Paint();
    private Paint.FontMetrics fontMetrics = new Paint.FontMetrics();

    private static final int REFRESH = 1;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg)
        {
            switch (msg.what) {
                case REFRESH:
                    invalidate();
            }
        }
    };

    public ClockView(Context context) {
        super(context);
        init();
    }

    public ClockView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ClockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        cirPaint.setAntiAlias(true);
        cirPaint.setStyle(Paint.Style.STROKE);

        unitPaint.setAntiAlias(true);
        unitPaint.setColor(Color.WHITE);
        unitPaint.setStrokeWidth(UNIT_LINE_WIDTH);
        unitPaint.setStrokeCap(Paint.Cap.ROUND);
        unitPaint.setStyle(Paint.Style.STROKE);

        // TODO 设置绘制时、分、秒针的画笔: needlePaint
        needlePaint.setAntiAlias(true);
        needlePaint.setStrokeCap(Paint.Cap.ROUND);
        needlePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        // TODO 设置绘制时间数字的画笔: numberPaint
        numberPaint.setAntiAlias(true);
        numberPaint.setColor(Color.WHITE);
        numberPaint.setTextSize(70);
        numberPaint.setColor(Color.WHITE);
        numberPaint.setTextAlign(Paint.Align.CENTER);
        numberPaint.setStrokeWidth(10);
        numberPaint.setFakeBoldText(true);

        requestLayout();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        configWhenLayoutChanged();
    }

    private void configWhenLayoutChanged() {
        float newRadius = Math.min(getWidth(), getHeight()) / 2f;
        if (newRadius == radius) {
            return;
        }
        radius = newRadius;
        centerX = getWidth() / 2f;
        centerY = getHeight() / 2f;

        // 当视图的宽高确定后就可以提前计算表盘的刻度线的起止坐标了
        for (int degree = 0; degree < FULL_CIRCLE_DEGREE; degree += UNIT_DEGREE) {
            double radians = Math.toRadians(degree);
            float startX = (float) (centerX + (radius * (1 - 0.05f)) * Math.cos(radians));
            float startY = (float) (centerX + (radius * (1 - 0.05f)) * Math.sin(radians));
            float stopX = (float) (centerX + radius * Math.cos(radians));
            float stopY = (float) (centerY + radius * Math.sin(radians));
            unitLinePositions.add(new RectF(startX, startY, stopX, stopY));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawUnit(canvas);
        drawTimeNeedles(canvas);
        drawTimeNumbers(canvas);
        drawCir(canvas);
        // TODO 实现时间的转动，每一秒刷新一次
        handler.sendMessageDelayed(Message.obtain(handler, REFRESH), 1);
    }

    // 绘制中心圆
    private void drawCir(Canvas canvas) {
        cirPaint.setStyle(Paint.Style.STROKE);
        cirPaint.setStrokeWidth(15);
        cirPaint.setColor(Color.WHITE);
        canvas.drawCircle(centerX, centerY, 15, cirPaint);
        cirPaint.setStyle(Paint.Style.FILL);
        cirPaint.setColor(Color.GRAY);
        canvas.drawCircle(centerX, centerY, 15, cirPaint);
    }

    // 绘制表盘上的刻度
    private void drawUnit(Canvas canvas) {
        for (int i = 0; i < unitLinePositions.size(); i++) {
            if (i % 5 == 0) {
                unitPaint.setAlpha(HIGHLIGHT_UNIT_ALPHA);
            } else {
                unitPaint.setAlpha(NORMAL_UNIT_ALPHA);
            }
            RectF linePosition = unitLinePositions.get(i);
            canvas.drawLine(linePosition.left, linePosition.top, linePosition.right, linePosition.bottom, unitPaint);
        }
    }

    private void drawTimeNeedles(Canvas canvas) {
        Time time = getCurrentTime();
        int hour = time.getHours();
        int minute = time.getMinutes();
        int second = time.getSeconds();
        // TODO 根据当前时间，绘制时针、分针、秒针
        /**
         * 思路：
         * 1、以时针为例，计算从0点（12点）到当前时间，时针需要转动的角度
         * 2、根据转动角度、时针长度和圆心坐标计算出时针终点坐标（起始点为圆心）
         * 3、从圆心到终点画一条线，此为时针
         * 注1：计算时针转动角度时要把时和分都得考虑进去
         * 注2：计算坐标时需要用到正余弦计算，请用Math.sin()和Math.cos()方法
         * 注3：Math.sin()和Math.cos()方法计算时使用不是角度而是弧度，所以需要先把角度转换成弧度，
         *     可以使用Math.toRadians()方法转换，例如Math.toRadians(180) = 3.1415926...(PI)
         * 注4：Android视图坐标系的0度方向是从圆心指向表盘3点方向，指向表盘的0点时是-90度或270度方向，要注意角度的转换
         */
        float hourDegree, minuteDegree, secondDegree;
        float endX, endY;
        float secX, secY;
        //Hour
        needlePaint.setColor(Color.WHITE);
        needlePaint.setStrokeWidth(HOUR_NEEDLE_WIDTH);
        hourDegree = (hour + minute/60f) * UNIT_DEGREE * 5 - OFF_DEGREE;
        endX = (float) (centerX + radius * Math.cos(Math.toRadians(hourDegree)) * HOUR_NEEDLE_LENGTH_RATIO);
        endY = (float) (centerY + radius * Math.sin(Math.toRadians(hourDegree)) * HOUR_NEEDLE_LENGTH_RATIO);
        canvas.drawLine(centerX, centerY, endX, endY, needlePaint);
        // int hourDegree = 180;
        // float endX = (float) (centerX + radius * HOUR_NEEDLE_LENGTH_RATIO * Math.cos(Math.toRadians(hourDegree)))
        //Minute
        needlePaint.setStrokeWidth(MINUTE_NEEDLE_WIDTH);
        minuteDegree = (minute) * UNIT_DEGREE - OFF_DEGREE;
        endX = (float) (centerX + radius * Math.cos(Math.toRadians(minuteDegree)) * MINUTE_NEEDLE_LENGTH_RATIO);
        endY = (float) (centerY + radius * Math.sin(Math.toRadians(minuteDegree)) * MINUTE_NEEDLE_LENGTH_RATIO);
        canvas.drawLine(centerX, centerY, endX, endY, needlePaint);
        //Second
        needlePaint.setColor(Color.GRAY);
        needlePaint.setStrokeWidth(SECOND_NEEDLE_WIDTH);
        secondDegree = second * UNIT_DEGREE - OFF_DEGREE;
        secX = (float) (centerX - radius * Math.cos(Math.toRadians(secondDegree)) * SECOND_NEEDLE_EX_RATIO);
        secY = (float) (centerY - radius * Math.sin(Math.toRadians(secondDegree)) * SECOND_NEEDLE_EX_RATIO);
        endX = (float) (centerX + radius * Math.cos(Math.toRadians(secondDegree)) * SECOND_NEEDLE_LENGTH_RATIO);
        endY = (float) (centerY + radius * Math.sin(Math.toRadians(secondDegree)) * SECOND_NEEDLE_LENGTH_RATIO);
        canvas.drawLine(secX, secY, endX, endY, needlePaint);
    }

    private void drawTimeNumbers(Canvas canvas) {
        // TODO 绘制表盘时间数字（可选）
        numberPaint.getFontMetrics(fontMetrics);
        float textHeight = fontMetrics.descent - fontMetrics.ascent;
        float baseX, baseY;
        float off = (fontMetrics.bottom - fontMetrics.top)/2 - fontMetrics.bottom;
        for (int i = 0; i < 12; i++) {
            float hourDegree = i * 30 - 60;
            String number;
            if (i>8) {
                number = String.valueOf(i + 1);
            } else {
                number = "0" + String.valueOf(i + 1);
            }
            float textWidth = numberPaint.measureText(number);
            baseX = (float) (centerX + (radius * 0.73f + textWidth / 2f) * Math.cos(Math.toRadians(hourDegree)));
            baseY = (float) (centerY + (radius * 0.73f + textHeight / 2f) * Math.sin(Math.toRadians(hourDegree)));
            canvas.drawText(number, baseX, baseY + off, numberPaint);
        }
    }

    // 获取当前的时间：时、分、秒
    private Time getCurrentTime() {
        calendar.setTimeInMillis(System.currentTimeMillis());
        return new Time(
                calendar.get(Calendar.HOUR),
                calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.SECOND));
    }
}
