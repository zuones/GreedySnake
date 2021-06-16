package com.zhjaid.GreedySnake;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * from:zhjaid
 * email:zhjaid@163.com
 * qq:2372315936
 * <p>
 * 贪吃蛇  盘默认大小 50x50
 */
public class GreedySnakeView extends View implements Runnable {

    public GreedySnakeView(Context context) {
        this(context, null);
    }

    public GreedySnakeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GreedySnakeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * @param color 设置蛇身颜色
     */
    public void setSnakeColor(int color) {
        paint.setColor(color);
    }

    /**
     * @param color 设置蛇头颜色
     */
    public void setSnakeHeadColor(int color) {
        hreadPaint.setColor(color);
    }

    /**
     * @param color 设置食物颜色
     */
    public void setFoodColor(int color) {
        foodPaint.setColor(color);
    }

    private void init() {
        setBackgroundColor(Color.BLACK);

        initData();

        direction = Direction.RIGHT;

        paint = new Paint();
        paint.setTextSize(50);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);


        //蛇头部
        hreadPaint = new Paint();
        hreadPaint.setColor(Color.RED);

        //食物画笔
        foodPaint = new Paint();
        foodPaint.setColor(Color.YELLOW);

        gameSleep = (int) (sleep);

        drawThread = new Thread(this);

    }

    /**
     * 开始游戏
     */
    public void start() {
        if (drawThread != null)
            drawThread.start();
    }

    /**
     * 初始化游戏数据
     */
    private void initData() {
        points.clear();
        //初始化 在屏幕中间添加点
        if (points.size() == 0) {
            //计算起点y和起点x
            float startY = mHeight / 2 - spotSize;
            float startX = mWidth / 2 - spotSize * startLength;
            for (int i = 0; i < startLength; i++) {
                Point point = new Point();
                point.x = (int) (startX + i * spotSize);
                point.y = (int) (startY);
                points.add(point);
            }
        }
        refreshFood();
    }


    /**
     * 刷新食物
     */
    private void refreshFood() {
        try {
            //产生食物
            if (mHeight == 0)
                mHeight = mWidth;
            foodPoint = new Point();

            int randomWidth = new Random().nextInt((int) mWidth - spotSize);
            int randomHeight = new Random().nextInt((int) mHeight - spotSize);
            foodPoint.x = randomWidth % spotSize == 0 ? randomWidth + spotSize / 2 : (randomWidth + randomWidth % spotSize) + spotSize / 2;

            foodPoint.y = randomHeight % spotSize == 0 ? randomWidth + spotSize / 2 : (randomHeight + randomHeight % spotSize) + spotSize / 2;

            setMeasuredDimension((int) mWidth, (int) mHeight);
        } catch (Exception e) {

        }

    }

    /**
     * 前进方向
     */
    private Direction direction;

    /**
     * 单个正方形的边长
     */
    private int spotSize;

    /**
     * 画笔
     */
    private Paint paint;
    /**
     * 开始的长度
     */
    private int startLength = 3;

    /**
     * @param startLength 设置开局蛇身的长度
     */
    public void setStartLength(int startLength) {
        this.startLength = startLength;
    }

    /**
     * 每吃一个减少多少时间
     */
    private int sleepOne = 10;

    /**
     * @param sleepOne 设置没吃掉一个食物减少的间隔
     */
    public void setSleepOne(int sleepOne) {
        this.sleepOne = sleepOne;
    }

    //一局游戏的得分/速度
    private int gameSleep;
    /**
     * 列数
     */
    private int Columns = 60;

    /**
     * @param columns 设置游戏列数
     */
    public void setColumns(int columns) {
        Columns = columns;
    }

    /**
     * 坐标集合
     */
    private List<Point> points = new ArrayList<>();
    /**
     * 当前食物的坐标
     */
    private Point foodPoint;
    /**
     * 滑动的灵敏度
     */
    private int Sensitivity = 100;
    /**
     * 刷新间隔，越快移动越快
     */
    private long sleep = 500;

    /**
     * @param sleep 设置刷新间隔
     */
    public void setSleep(long sleep) {
        this.sleep = sleep;
        this.gameSleep = (int) sleep;
    }

    /**
     * 是否监听手势，设置为false后只能通过setDe
     */
    private boolean isMonitorGestures = true;


    private float mWidth;//控件的宽
    private float mHeight;//控件的高

    private Thread drawThread;

    /**
     * 是否显示网格
     */
    private boolean isOpenLine = false;

    /**
     * @param isOpenLine 设置网格的显示和隐藏
     */
    public void setisOpenLine(boolean isOpenLine) {
        this.isOpenLine = isOpenLine;
    }

    /**
     * 食物画笔
     */
    private Paint foodPaint;
    /**
     * 蛇头画笔
     */
    private Paint hreadPaint;


    /**
     * 通知速度刷新
     */
    private Handler updateHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (changeListener != null) {
                changeListener.onSpeedChange((int) (sleep - gameSleep), (int) ((sleep - sleepOne - gameSleep) / sleepOne) + 1,
                        getScore());
            }
        }
    };


    /**
     * @return 获取当前分数，可自行计算
     */
    private int getScore() {
        return (int) (((sleep - sleepOne - gameSleep) / sleepOne) + 1) * (int) (sleep - gameSleep);
    }

    @Override
    public void run() {

        while (true) {
            if (direction != Direction.FINISH) {
                if (points.size() == 0) {
                    continue;
                }

                Point pointHread = points.get(points.size() - 1);//头部

                Point endPoint = points.get(0);//0代表尾巴

                if (pointHread.x > mWidth - spotSize || pointHread.x < spotSize || pointHread.y < spotSize || pointHread.y > mHeight - spotSize) {
                    direction = Direction.FINISH;
                    postInvalidate();
                    return;
                }


                if (Math.max(pointHread.x, foodPoint.x) - Math.min(pointHread.x, foodPoint.x) <= spotSize &&
                        Math.max(pointHread.y, foodPoint.y) - Math.min(pointHread.y, foodPoint.y) <= spotSize) {
                    //这里算是吃到了食物
                    points.add(foodPoint);
                    if (gameSleep > 0 && gameSleep - sleepOne > 0)
                        gameSleep -= sleepOne;
                    Message msg = Message.obtain();
                    updateHandler.sendMessage(msg);
                    refreshFood();
                }

                if (direction == Direction.LEFT) {
                    //向左
                    pointHread.x = pointHread.x - spotSize;
                    endPoint.x = pointHread.x + (spotSize * getXNum(pointHread.x));
                    endPoint.y = pointHread.y;
                } else if (direction == Direction.TOP) {
                    //向左
                    pointHread.y = pointHread.y - spotSize;
                    endPoint.y = pointHread.y + (spotSize * getYNum(pointHread.y));
                    endPoint.x = pointHread.x;
                } else if (direction == Direction.BOTTOM) {
                    //向左
                    pointHread.y = pointHread.y + spotSize;
                    endPoint.y = pointHread.y - (spotSize * getYNum(pointHread.y));
                    endPoint.x = pointHread.x;
                } else if (direction == Direction.RIGHT) {
                    //向左
                    pointHread.x = pointHread.x + spotSize;
                    endPoint.x = pointHread.x - (spotSize * getXNum(pointHread.x));
                    endPoint.y = pointHread.y;
                }
                points.add(points.size() - 1, endPoint);
                points.remove(0);
                postInvalidate();
            }
            try {
                Thread.sleep(gameSleep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param x 当前X索引
     * @return 获取同一x列上有多少自身的点
     */
    private int getXNum(int x) {
        if (x == 0) {
            return startLength;
        }
        int num = 0;
        for (Point point : points) {
            if (point.x == x) {
                num++;
            }
        }
        return num;
    }

    private int getYNum(int y) {
        if (y == 0) {
            return startLength;
        }
        int num = 0;
        for (Point point : points) {
            if (point.y == y) {
                num++;
            }
        }
        return num;
    }


    /**
     * 蛇和食物的样式
     */
    private boolean snakeStyle = false;

    /**
     * @param snakeStyle 设置蛇身样式, false为圆形,true为方形
     */
    public void setSnakeStyle(boolean snakeStyle) {
        this.snakeStyle = snakeStyle;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (direction != Direction.FINISH) {
            for (int i = 0, u = points.size(); i < u; i++) {
                RectF rf1 = new RectF(points.get(i).x,
                        points.get(i).y,
                        points.get(i).x + spotSize,
                        points.get(i).y + spotSize);
                if (i == u - 1) {
                    if (snakeStyle)
                        canvas.drawRect(rf1, hreadPaint);
                    else
                        canvas.drawOval(rf1, hreadPaint);
                } else {
                    if (snakeStyle)
                        canvas.drawRect(rf1, paint);
                    else
                        canvas.drawOval(rf1, paint);
                }
            }

            //canvas.drawText("当前速度：" + (sleep - gameSleep) + " 已经吃掉" + ((sleep - sleepOne - gameSleep) / sleepOne) + "个食物", 50, 50, paint);

            if (foodPoint != null) {
                //绘制食物
                RectF food = new RectF(foodPoint.x,
                        foodPoint.y,
                        foodPoint.x + spotSize,
                        foodPoint.y + spotSize);
                if (snakeStyle)
                    canvas.drawRect(food, foodPaint);
                else
                    canvas.drawOval(food, foodPaint);
            }
        } else {
            //游戏结束
            if (changeListener != null) {
                changeListener.onFinish((int) ((sleep - sleepOne - gameSleep) / sleepOne) + 1,  getScore());
            }
//            StaticLayout myStaticLayout = new StaticLayout(String.format(endText, ((sleep - sleepOne - gameSleep) / sleepOne)), (TextPaint) textPaint, canvas.getWidth(), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
//            myStaticLayout.draw(canvas);
//            canvas.drawText(, mWidth / 2 - textRect.width() / 2,
//                    mHeight / 2 - textRect.height() / 2, textPaint);
        }

        if (isOpenLine) {
            if (spotSize != 0) {
                for (int i = 0; i < Columns; i++) {
                    int big = i * spotSize;
                    canvas.drawLine(big, 0, big, mHeight, paint);
                }
                for (int i = 0; i < mHeight / spotSize; i++) {
                    int big = i * spotSize;
                    canvas.drawLine(0, big, mHeight, big, paint);
                }
            }
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int widthsize = MeasureSpec.getSize(widthMeasureSpec);
        int heightsize = MeasureSpec.getSize(heightMeasureSpec);

        spotSize = widthsize / Columns;

        mWidth = widthsize * 1.0f;

        if (mode == MeasureSpec.EXACTLY) {
            //固定大小
            mHeight = heightsize * 1.0f;
        } else if (mode == MeasureSpec.UNSPECIFIED) {
            mHeight = mWidth * 1.0f;
        }

        //产生食物
        if (mHeight == 0)
            mHeight = mWidth;
        foodPoint = new Point();

        int randomWidth = new Random().nextInt((int) mWidth - spotSize);
        int randomHeight = new Random().nextInt((int) mHeight - spotSize);
        foodPoint.x = randomWidth % spotSize == 0 ? randomWidth + spotSize / 2 : (randomWidth + randomWidth % spotSize) + spotSize / 2;

        foodPoint.y = randomHeight % spotSize == 0 ? randomWidth + spotSize / 2 : (randomHeight + randomHeight % spotSize) + spotSize / 2;

        setMeasuredDimension((int) mWidth, (int) mHeight);
    }

    /**
     * 重新开始
     */
    public void againGame() {
        init();
        postInvalidate();
    }

    /**
     * 提前结束游戏
     */
    public void endEarly() {
        direction = Direction.FINISH;
    }

    /**
     * @param isMonitorGestures 设置是否打开手势监听
     */
    public void setMonitorGestures(boolean isMonitorGestures) {
        this.isMonitorGestures = isMonitorGestures;
    }

    /**
     * @param direction 设置游戏方向，可用于代替手势
     */
    public void setGmaeDirection(Direction direction) {
        this.direction = direction;
        if (changeListener != null) {
            changeListener.onDirectionChange(direction);
        }
    }

    private int downX, downY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (!isMonitorGestures) {
            return false;
        }
        if (action == MotionEvent.ACTION_DOWN) {
            downX = (int) event.getX();
            downY = (int) event.getY();
        }
        if (action == MotionEvent.ACTION_MOVE) {

        }
        if (action == MotionEvent.ACTION_UP) {
            if (direction == Direction.FINISH) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//                builder.setTitle("提示").setMessage("是否重新开始？").setPositiveButton("重新开始", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        init();
//                        postInvalidate();
//                    }
//                }).setNegativeButton("取消", null).show();
                return true;
            } else {
                int upY = (int) event.getY();
                int upX = (int) event.getX();

                if (downY - upY > Sensitivity) {
//                Point point = new Point();
//                point.x = upX;
//                point.y = upY;
//
//                points.add(point);
                    direction = Direction.TOP;
                    if (changeListener != null) {
                        changeListener.onDirectionChange(Direction.TOP);
                    }
                    //Toast.makeText(getContext(), "向上", Toast.LENGTH_LONG).show();

                    //      invalidate();
                    return true;
                }
                if (upY - downY > Sensitivity) {
                    direction = Direction.BOTTOM;
                    if (changeListener != null) {
                        changeListener.onDirectionChange(Direction.BOTTOM);
                    }
                    //Toast.makeText(getContext(), "向下", Toast.LENGTH_LONG).show();
                    return true;
                }
                if (downX - upX > Sensitivity) {
                    direction = Direction.LEFT;
                    if (changeListener != null) {
                        changeListener.onDirectionChange(Direction.LEFT);
                    }
                    //Toast.makeText(getContext(), "向左", Toast.LENGTH_LONG).show();
                    return true;
                }
                if (upX - downX > Sensitivity) {
                    direction = Direction.RIGHT;
                    if (changeListener != null) {
                        changeListener.onDirectionChange(Direction.RIGHT);
                    }
                    //Toast.makeText(getContext(), "向右", Toast.LENGTH_LONG).show();
                    return true;
                }
            }
        }
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        initData();
        invalidate();
    }

    public enum Direction {
        LEFT,
        RIGHT,
        TOP,
        BOTTOM,
        FINISH
    }

    /**
     * @param changeListener 设置游戏进度监听器
     */
    public void setChangeListener(SpeedChangeListener changeListener) {
        this.changeListener = changeListener;
    }

    private SpeedChangeListener changeListener;

    //当游戏数据发生了变化出发监听器
    public interface SpeedChangeListener {
        /**
         * @param speed 当前的速度
         * @param num   吃掉的食物数量
         * @param score 得分
         */
        void onSpeedChange(int speed, int num, int score);

        /**
         * 游戏结束
         *
         * @param num   吃掉食物的数量
         * @param score 得分（可自己计算）
         */
        void onFinish(int num, int score);

        /**
         * @param direction 方向变化出发这个方法
         */
        void onDirectionChange(Direction direction);
    }
}
