package com.example.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

import java.io.PrintWriter;

public class joystickCanvas extends View {

    private final String aileronString = "set controls/flight/aileron ";
    private final String elevatorString = "set controls/flight/elevator ";
    private int width, height;
    private Paint ellipse;
    boolean isPlayerMoving;
    private Paint wallPaint;
    private Paint joystick;
    Point initPoint;
    Point newPoint;
    int radiusP;
    int ovalTop;
    int ovalBottom;
    int ovalRight;
    int ovalleft;
    public String SERVER_IP;
    public int SERVER_PORT;
    public PrintWriter writer;
    RectF rect;


    public joystickCanvas(Context context) {
        super(context);
        //Set the ellipse color to gray
        ellipse = new Paint(Paint.ANTI_ALIAS_FLAG);
        ellipse.setColor(Color.GRAY);
        ellipse.setStyle(Paint.Style.FILL);
        //Set the background color to blue
        wallPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        wallPaint.setColor(Color.parseColor("#3aa8c1"));
        wallPaint.setStyle(Paint.Style.FILL);
        //Set the joystick color to green
        joystick = new Paint(Paint.ANTI_ALIAS_FLAG);
        joystick.setColor(Color.parseColor("#33cc5a"));
        joystick.setStyle(Paint.Style.FILL);
        isPlayerMoving = false;
        //Create center points
        newPoint = new Point();
        initPoint = new Point();
        radiusP = 70;
    }


    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        int x = getPaddingLeft() + getPaddingRight();
        int y = getPaddingTop() + getPaddingBottom();
        width = w - x;
        height = h - y;
        //Set the initial center point
        initPoint.x = width / 2;
        initPoint.y = height / 2;
        //Set the current center point to the initial center point
        newPoint.x = initPoint.x;
        newPoint.y = initPoint.y;
        //Set the oval bounder
        ovalleft = (width / 2) - 150;
        ovalTop = (height / 2) - 300;
        ovalRight = (width / 2) + 150;
        ovalBottom = (height / 2) + 300;
        //Create  the oval
        rect = new RectF(ovalleft, ovalTop, ovalRight, ovalBottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //Create the background
        Rect back = new Rect(0, 0, canvas.getWidth(), canvas.getHeight());
        //Draw all the shapes
        canvas.drawRect(back, wallPaint);
        canvas.drawOval(rect, ellipse);
        canvas.drawCircle(newPoint.x, newPoint.y, radiusP, joystick);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                int x = (int) event.getX();
                int y = (int) event.getY();
                //Check if the user click on the joystick
                if ((initPoint.y - radiusP <= y) && (initPoint.y + radiusP >= y) &&
                        (initPoint.x - radiusP <= x) && (initPoint.x + radiusP >= x)) {
                    isPlayerMoving = true;
                    newPoint.x = x;
                    newPoint.y = y;
                    break;
                } else {
                    isPlayerMoving = false;
                }
            }
            case MotionEvent.ACTION_MOVE: {
                if (!isPlayerMoving)
                    return true;
                int x = (int) event.getX();
                int y = (int) event.getY();
                //Check if the new point is inside the oval
                double ellipseBound = Math.pow(x - initPoint.x, 2) / Math.pow(150, 2) +
                        Math.pow(y - initPoint.y, 2) / Math.pow(300, 2);
                if (ellipseBound <= 1) {
                    isPlayerMoving = true;
                    newPoint.x = x;
                    newPoint.y = y;
                    calcValue(x, y);
                    break;
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                newPoint.x = initPoint.x;
                newPoint.y = initPoint.y;
                isPlayerMoving = false;
                break;
            }
            case MotionEvent.ACTION_CANCEL: {
                newPoint.x = initPoint.x;
                newPoint.y = initPoint.y;
                isPlayerMoving = false;
                break;
            }
        }
        invalidate();
        return true;
    }

    /**
     * The function send command to the server
     */
    public void sendCommand(final String message) {
        Runnable run = new Runnable() {
            @Override
            public void run() {
                if (writer != null) {
                    writer.println(message);
                    writer.flush();
                }
            }
        };
        Thread thread = new Thread(run);
        thread.start();
    }

    /**
     * The function calculate the aileron and elevator values according to x and y position
     */
    public void calcValue(int x, int y) {
        //Calculate the aileron and elevator
        float aileron = ((float) x - (float) initPoint.x) / ((float) initPoint.x);
        float elevator = ((float) y - ((float) initPoint.y)) / ((float) initPoint.y);
        String sendAileron = aileronString + Float.toString(aileron);
        String sendElevator = elevatorString + Float.toString(elevator);
        //Send the values to the server
        sendCommand(sendAileron);
        sendCommand(sendElevator);
    }

}
