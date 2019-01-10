package kr.co.munjanara.c17_reflection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Random;

import javax.microedition.khronos.egl.EGLDisplay;

public class Ball {
    int x,y;
    int rad;
    int dx, dy;
    int color;
    int count;

    static Ball Create(int x, int y, int Rad) {
        Random Rnd = new Random();
        Ball NewBall = new Ball();

        NewBall.x = x;
        NewBall.y = y;
        NewBall.rad = Rad;
        do {
            NewBall.dx = Rnd.nextInt(11) - 5;
            NewBall.dy = Rnd.nextInt(11) - 5;
        } while (NewBall.dx == 0 || NewBall.dy == 0);
        NewBall.count = 0;
        NewBall.color = Color.rgb(Rnd.nextInt(256), Rnd.nextInt(256), Rnd.nextInt(256));

        return  NewBall;
    }

    void Move(int Width, int Height) {
        x += dx;
        y += dy;

        if( x<rad || x>Width-rad) {
            dx *= -1;
            count++;
        }
        if(y<rad|| y>Height-rad) {
            dy *= -1;
            count++;
        }
    }

    void Draw(Canvas canvas) {
        Paint pnt = new Paint();
        pnt.setAntiAlias(true);

        int r;
        int alpha;

        for(r=rad, alpha=1; r>4; r--, alpha+=5) {
            pnt.setColor(Color.argb(alpha, Color.red(color),Color.green(color),Color.blue(color)));
            canvas.drawCircle(x,y,r,pnt);
        }
    }
}


class MyView extends View {
    Bitmap  mBack;
    ArrayList<Ball> arball = new ArrayList<Ball>();
    final static int DELAY = 50;
    final static int RAD = 24;

    public MyView(Context context) {
        super(context);
        mBack = BitmapFactory.decodeResource(context.getResources(), R.drawable.surfaceview_android);
        Display display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        mBack = Bitmap.createScaledBitmap(mBack, display.getWidth()*2, display.getHeight()*2, true);

        mHandler.sendEmptyMessageDelayed(0, DELAY);
    }

    @Override
    public boolean onFilterTouchEventForSecurity(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            Ball NewBall = Ball.Create((int)event.getX(),(int)event.getY(), RAD);
            arball.add(NewBall);
            invalidate();
            return  true;
        }
        return  false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mBack, 0,0, null);
        for(int idx=0;idx< arball.size();idx++) {
            arball.get(idx).Draw(canvas);
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Ball B;
            for(int idx=0;idx<arball.size();idx++) {
                B = arball.get(idx);
                B.Move(getWidth(), getHeight());
                if(B.count > 4) {
                    arball.remove(idx);
                    idx--;
                }
            }
            invalidate();
            mHandler.sendEmptyMessageDelayed(0, DELAY);
        }
    };
}
