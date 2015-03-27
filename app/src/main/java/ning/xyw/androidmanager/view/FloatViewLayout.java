package ning.xyw.androidmanager.view;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import ning.xyw.androidmanager.App;
import ning.xyw.androidmanager.R;
import ning.xyw.androidmanager.service.FloatViewService;
import ning.xyw.androidmanager.util.L;

public class FloatViewLayout extends RelativeLayout implements OnClickListener, View.OnLongClickListener {
    private final static String TAG = "FloatViewLayout";
    private float mTouchX;
    private float mTouchY;
    private float x;
    private float y;
    private float mStartX;
    private float mStartY;
    private long mDownTime;
    private RelativeLayout mLayout;
    private View view;

    private WindowManager mWindowManager;
    /**
     * 此wmParams为获取的全局变量，用以保存悬浮窗口的属性
     */
    private WindowManager.LayoutParams wmParams;

    public FloatViewLayout(Context context, WindowManager windowManager,
                           WindowManager.LayoutParams params) {
        super(context);
        mWindowManager = windowManager;
        wmParams = params;
        view = inflate(getContext(), R.layout.floatview_recording, null);
        mLayout = (RelativeLayout) view
                .findViewById(R.id.floatview_recording_layout);
        addView(view);
        setOnClickListener(this);
        setOnLongClickListener(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 获取到状态栏的高度
        Rect frame = new Rect();
        getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        // 获取相对屏幕的坐标，即以屏幕左上角为原点
        x = event.getRawX();
        y = event.getRawY() - statusBarHeight;
        int action = event.getAction() & MotionEvent.ACTION_MASK;//多点触控
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // 捕获手指触摸按下动作
                // 获取相对View的坐标，即以此View左上角为原点
                mDownTime = System.currentTimeMillis();
                mTouchX = event.getX();
                mTouchY = event.getY();
                mStartX = x;
                mStartY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                // 捕获手指触摸移动动作
                updateViewPosition();
                break;
            case MotionEvent.ACTION_UP:
                updateViewPosition();
                mTouchX = mTouchY = 0;
                boolean movedEnough = Math.abs(x - mStartX) > 5 || Math.abs(y - mStartY) > 5;//单一方向移动距离超过5,视为拖动事件。
                boolean lastEnough = System.currentTimeMillis() - mDownTime > 300;//按下时间超过300ms，视为长按操作。
                mDownTime = 0;
                if (!movedEnough) {
                    if (lastEnough) {
                        onLongClick(this);
                    } else {
                        onClick(this);
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                mTouchX = mTouchY = 0;
                mDownTime = 0;
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        L.d(TAG, "onClick");
        ActivityManager am = (ActivityManager) App.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        L.d(TAG, "pkg:" + cn.getPackageName());
        L.d(TAG, "cls:" + cn.getClassName());
    }

    @Override
    public boolean onLongClick(View v) {
        L.d(TAG, "onLongClick");
        FloatViewService.stopService();
        return true;
    }

    private void updateViewPosition() {
        // 更新浮动窗口位置参数
        wmParams.x = (int) (x - mTouchX);
        wmParams.y = (int) (y - mTouchY);
        mWindowManager.updateViewLayout(this, wmParams); // 刷新显示
    }

}
