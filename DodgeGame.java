package com.example.dodgegame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class DodgeGame extends AppCompatActivity {
    String time;
    GameSurface gameSurface;
    List<Bitmap> meatballs = new ArrayList<>();
    MediaPlayer backgroundMusic;
    MediaPlayer birdCry;
    Intent gameOver;
    CountDownTimer timer;
    int totalTime = (100000/1000)%60;
    int flip;
    int meatballSpeed = -20;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameSurface = new GameSurface(this);
        setContentView(gameSurface);

        gameOver = new Intent(DodgeGame.this, GameOver.class);

        timer = new CountDownTimer(100000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                NumberFormat f = new DecimalFormat("00");
                long min = (millisUntilFinished / 60000) % 60;
                long sec = (millisUntilFinished / 1000) % 60;
                time = f.format(min) + ":" + f.format(sec);
            }

            @Override
            public void onFinish() {
                Log.d("Test", "Tangerine");
                startActivity(gameOver);
                Log.d("Test", "Banana Bread");
            }
        };
        timer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameSurface.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameSurface.resume();
    }


    public class GameSurface extends SurfaceView implements Runnable, SensorEventListener {
        Thread gameThread;
        SurfaceHolder holder;
        volatile boolean running = false;
        Bitmap ball;
        Bitmap bird;
        Bitmap background;
        Bitmap meatball;
        int ballX = 0;
        int meatballX = 0;
        int meatballY;
        int x = 200;
        Paint paintProperty;
        int screenWidth;
        int screenHeight;
        int score = 0;
        SensorManager sensorManager;
        Sensor accelerometer;
        float xAxis, yAxis, zAxis;
        double Pitch;
        double RAD_TO_DEG = 180d / Math.PI;
        boolean meatballSpawned;
        boolean isHurt = false;

        public GameSurface(Context context) {
            super(context);
            holder = getHolder();
            background = BitmapFactory.decodeResource(getResources(), R.drawable.sun_rise_set);
            bird = BitmapFactory.decodeResource(getResources(), R.drawable.truebird);
            bird = Bitmap.createScaledBitmap(bird, 100, 100, false);
            meatball = BitmapFactory.decodeResource(getResources(), R.drawable.meatball);
            meatball = Bitmap.createScaledBitmap(meatball, 100, 100, false);
            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, sensorManager.SENSOR_DELAY_NORMAL);

            ball = BitmapFactory.decodeResource(getResources(), R.drawable.meatball);

            Display screenDisplay = getWindowManager().getDefaultDisplay();
            Point sizeOfScreen = new Point();
            screenDisplay.getSize(sizeOfScreen);
            screenWidth = sizeOfScreen.x;
            screenHeight = sizeOfScreen.y;
            paintProperty = new Paint();

            meatballSpawned = false;
        }

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            xAxis = sensorEvent.values[0];
            yAxis = sensorEvent.values[1];
            zAxis = sensorEvent.values[2];
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }

        @Override
        public void run() {
            Canvas canvas;
            Drawable d = getResources().getDrawable(R.drawable.sun_rise_set, null);

            backgroundMusic = MediaPlayer.create(DodgeGame.this, R.raw.islandloopmusic);
            backgroundMusic.setVolume(100, 150);
            backgroundMusic.setLooping(true);
            backgroundMusic.start();
            if (!backgroundMusic.isPlaying()) {
                backgroundMusic.start();
                backgroundMusic.setLooping(true);
            }

            flip = 5;

            while (running) {
                Log.d("BallX", "" + ballX);
                if (holder.getSurface().isValid() == false)
                    continue;
                canvas = holder.lockCanvas(null);
                d.setBounds(getLeft(), getTop(), getRight(), getBottom());
                d.draw(canvas);
                Paint paint = new Paint();
                paint.setTextSize(32);

                canvas.drawText(time, 50, 50, paint);

                canvas.drawBitmap(bird, 20, getHeight() / 2 + ballX, null);

                //Log.d("Ball&ScreenWidthFlip", ballX+" "+screenWidth+" "+flip);
                Log.d("Hey", "" + meatballY);
                canvas.drawBitmap(meatball, 1800 + meatballX, meatballY, null);

                canvas.drawText("Score: " + score, 30, 100, paint);

                if (ballX >= 570 || ballX <= -610) {
                    flip = 1;
                    ballX+=0;
                }

                if (!meatballSpawned) {
                    Log.d("Works", "You do not suck");
                    meatballY = (int) (Math.random() * 860) + 20;
                    meatballSpawned = true;
                }

                if (zAxis < 0 && zAxis >= -5) {
                    ballX += flip;
                } else if (zAxis < -5) {
                    ballX += (flip * 4);
                }

                if (zAxis > 0 && zAxis < 5) {
                    ballX -= flip;
                } else if (zAxis > 5) {
                    ballX -= (flip * 4);
                }

                if (meatballX <= -1800) {
                    Log.d("Also Works", "You do not suck twice!");
                    meatballSpawned = false;
                    meatballX = 0;
                    meatballY = ((int) (Math.random()) * 931) - 465;
                    score++;;
                }

                meatballX -= 50;

                //checking if gear and robot intersect
                if(hitDetection(meatball, 1800+meatballX, meatballY, bird, 20, getHeight()/2+ballX)) {
                   Log.d("test", "work plz");
                    meatballX = 0;
                    meatballX = (int) (Math.random() * 931) - 465;

                    //sound effects for hurt
                    birdCry = MediaPlayer.create(DodgeGame.this, R.raw.birdcry);
                    birdCry.start();

                    //changing image for hurt
                    if (!isHurt) {
                        isHurt = true;
                        bird = BitmapFactory.decodeResource(getResources(), R.drawable.hitbird);
                        bird = Bitmap.createScaledBitmap(bird, 100, 100, false);
                        score--;


                        final Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                bird = BitmapFactory.decodeResource(getResources(), R.drawable.truebird);
                                bird = Bitmap.createScaledBitmap(bird, 100, 100, false);
                                isHurt = false;
                            }
                        }, 2000);
                    }
                }

                holder.unlockCanvasAndPost(canvas);
            }
        }

            //checking intersection of gear and robot
            public boolean hitDetection(Bitmap bitmap1, int x1, int y1, Bitmap bitmap2, int x2, int y2) {
                Rect rect1 = new Rect(x1, y1, x1 + bitmap1.getWidth(), y1 + bitmap1.getHeight());
                Rect rect2 = new Rect(x2, y2, x2 + bitmap2.getWidth(), y2 + bitmap2.getHeight());
                return (rect1.left < rect2.right && rect1.right > rect2.left) && (rect1.top < rect2.bottom && rect1.bottom > rect2.top);
            }
        public void resume() {
            running = true;
            gameThread = new Thread(this);
            gameThread.start();
        }

        public void pause() {
            running = false;
            while (true) {
                try {
                    gameThread.join();
                } catch (InterruptedException e) {

                }
            }

        }

        public boolean onTouchEvent(MotionEvent e)
        {
            Toast.makeText(DodgeGame.this, "Screen Touched", Toast.LENGTH_LONG).show();
            flip = 3;
            meatballSpeed = -90;
            meatballX+=meatballSpeed;
            return true;
        }
    } //GameSurface
}