package org.qihoo.cube.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.victor.loading.rotate.RotateLoading;

import org.qihoo.cube.R;
import org.qihoo.cube.layout.MyDialog;
import org.qihoo.cube.util.data.ApplicationManager;
import org.qihoo.cube.util.data.BackgroundLogin;
import org.qihoo.cube.util.data.Photo;
import org.qihoo.cube.util.data.Preference;
import org.qihoo.cube.util.image.AsyncTaskListener;
import org.qihoo.cube.util.image.ImageDecryptTask;
import org.qihoo.cube.util.image.ImageManager;


public class ViewPhotoActivity  extends Activity implements View.OnClickListener,
        ImageDecryptTask.ImageRefreshListener,View.OnTouchListener{

    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();
    private DisplayMetrics dm;
    private Bitmap bitmap;

    /** 最小缩放比例*/
    private float minScaleR = 1.0f;
    /** 最大缩放比例*/
    private static final float MAX_SCALE = 10f;

    /** 初始状态*/
    private static final int NONE = 0;
    /** 拖动*/
    private static final int DRAG = 1;
    /** 缩放*/
    private static final int ZOOM = 2;

    /** 当前模式*/
    private int mode = NONE;

    private PointF prev = new PointF();
    private PointF mid = new PointF();
    private float dist = 1f;

    private ImageButton back;
    private RelativeLayout topPanel;

    private ImageView photoView;

    private RelativeLayout deletePhoto;
    private RelativeLayout movePhoto;
    private RelativeLayout bottomPanel;

    private ImageDecryptTask decryptTask;

    private Photo photo;

    private Preference userDetail;

    private AlertDialog progressDialog;
    private RotateLoading loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lookphoto);

        initViews();

        // 获取用户数据
        userDetail = Preference.getInstance();

        // 设置当前照片
        setPhoto(Preference.getInstance().getCurrentPhoto());

        // TODO 这里可以调整
        loadingDialog = (RotateLoading)findViewById(R.id.rotateloading);
        loadingDialog.start();

        // 解密文件
        decryptTask = new ImageDecryptTask(this);
        decryptTask.execute(photo.getEncryPath(), photo.getSrcPath());
    }

    public void initViews() {
        back = (ImageButton)findViewById(R.id.activity_lookphoto_finish);
        topPanel = (RelativeLayout)findViewById(R.id.activity_lookphoto_top);
        photoView = (ImageView)findViewById(R.id.activity_lookphoto_photo);
        photoView.setOnTouchListener(this);

        deletePhoto = (RelativeLayout)findViewById(R.id.activity_lookphoto_delete);
        movePhoto = (RelativeLayout)findViewById(R.id.activity_lookphoto_returnphoto);
        bottomPanel = (RelativeLayout)findViewById(R.id.activity_lookphoto_bottom);

        topPanel.setVisibility(View.GONE);
        bottomPanel.setVisibility(View.GONE);

        registerEvent();
    }

    public void registerEvent() {
        back.setOnClickListener(this);
        deletePhoto.setOnClickListener(this);
        movePhoto.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == back) {
            // 取消当前照片设置
            userDetail.setCurrentPhoto(null);
            // 返回上一个Activity
            finish();
        }else if (v == deletePhoto) {
            AsyncTaskListener listener = new AsyncTaskListener(){

                @Override
                public void onAsyncTaskComplete(Object... objects) {
                    // 当前相册中删除该照片
                    userDetail.getCurrentGallery().deletePhoto(photo, true);

                    // 更新用户数据
                    userDetail.updateCurrentZone();

                    // 返回上一个Activity
                    finish();
                }

            };

            MyDialog.dialogDelete(this, "删除后不可恢复，确定要删除吗？", listener);

        }else if (v == movePhoto) {
            AsyncTaskListener listener = new AsyncTaskListener(){
                @Override
                public void onAsyncTaskComplete(Object... objects) {
                    // 将该照片移出当前空间
                    userDetail.getCurrentGallery().deletePhoto(photo, false);

                    // 更新用户数据
                    userDetail.updateCurrentZone();

//		            ImageManager.insertImage(ViewPhotoActivity.this, photo.getOriginPath());

                    // 返回上一个Activity
                    finish();
                }
            };

            MyDialog.dialogDelete(ViewPhotoActivity.this, "确定要移出空间吗？", listener);
        }

    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    @Override
    public void refreshImage(Bitmap bitmap) {
        // TODO 这里可以调整
        loadingDialog.stop();
        bottomPanel.setVisibility(View.VISIBLE);
//        photoView.setImageBitmap(bitmap);

        this.bitmap = bitmap;
        dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);// 获取分辨率
//        minZoom();
        center();
        photoView.setImageMatrix(matrix);
        photoView.setImageBitmap(this.bitmap);

        topPanel.setVisibility(View.VISIBLE);
        bottomPanel.setVisibility(View.VISIBLE);
        topPanel.bringToFront();
        bottomPanel.bringToFront();
    }

    /**
     * 最小缩放比例，最大为100%
     */
    private void minZoom() {
        minScaleR = Math.min(
                (float) dm.widthPixels / (float) bitmap.getWidth(),
                (float) dm.heightPixels / (float) bitmap.getHeight());
        if (minScaleR < 1.0) {
            matrix.postScale(minScaleR, minScaleR);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            userDetail.setCurrentPhoto(null);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            // 主点按下
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                prev.set(event.getX(), event.getY());
                mode = DRAG;
                break;
            // 副点按下
            case MotionEvent.ACTION_POINTER_DOWN:
                dist = spacing(event);
                // 如果连续两点距离大于10，则判定为多点模式
                if (spacing(event) > 10f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                //savedMatrix.set(matrix);
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - prev.x, event.getY()
                            - prev.y);
                } else if (mode == ZOOM) {
                    float newDist = spacing(event);
                    if (newDist > 10f) {
                        matrix.set(savedMatrix);
                        float tScale = newDist / dist;
                        matrix.postScale(tScale, tScale, mid.x, mid.y);
                    }
                }
                break;
        }
        photoView.setImageMatrix(matrix);
        CheckView();
        return true;
    }

    /**
     * 限制最大最小缩放比例，自动居中
     */
    private void CheckView() {
        float p[] = new float[9];
        matrix.getValues(p);
        if (mode == ZOOM) {
            if (p[0] < minScaleR) {
                //Log.d("", "当前缩放级别:"+p[0]+",最小缩放级别:"+minScaleR);
                matrix.setScale(minScaleR, minScaleR);
            }
            if (p[0] > MAX_SCALE) {
                //Log.d("", "当前缩放级别:"+p[0]+",最大缩放级别:"+MAX_SCALE);
                matrix.set(savedMatrix);
            }
        }
        center();
    }

    /**
     * 横向、纵向居中
     */
    protected void center(boolean horizontal, boolean vertical) {

        Matrix m = new Matrix();
        m.set(matrix);
        RectF rect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
        m.mapRect(rect);

        float height = rect.height();
        float width = rect.width();

        float deltaX = 0, deltaY = 0;

        if (vertical) {
            // 图片小于屏幕大小，则居中显示。大于屏幕，上方留空则往上移，下方留空则往下移
            int screenHeight = dm.heightPixels;
            if (height < screenHeight) {
                deltaY = (screenHeight - height) / 2 - rect.top;
            } else if (rect.top > 0) {
                deltaY = -rect.top;
            } else if (rect.bottom < screenHeight) {
                deltaY = photoView.getHeight() - rect.bottom;
            }
        }

        if (horizontal) {
            int screenWidth = dm.widthPixels;
            if (width < screenWidth) {
                deltaX = (screenWidth - width) / 2 - rect.left;
            } else if (rect.left > 0) {
                deltaX = -rect.left;
            } else if (rect.right < screenWidth) {
                deltaX = screenWidth - rect.right;
            }
        }
        matrix.postTranslate(deltaX, deltaY);
    }

    /**
     * 两点的距离
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    /**
     * 两点的中点
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    private void center() {
        center(true, true);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        BackgroundLogin.resume(this, 1);
    }

    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();

        ApplicationManager application = (ApplicationManager)this.getApplication();

        if (application.getStateForEndActivity() == 0){
            application.setStateForEndActivity(-1);
            return;
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("stateNumber", -1);
        startActivity(intent);
    }

}
