package org.qihoo.cube.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.greycellofp.tastytoast.TastyToast;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import org.qihoo.cube.R;
import org.qihoo.cube.adapter.PhotoAdapter;
import org.qihoo.cube.layout.MyDialog;
import org.qihoo.cube.util.data.ApplicationManager;
import org.qihoo.cube.util.data.BackgroundLogin;
import org.qihoo.cube.util.data.Gallery;
import org.qihoo.cube.util.data.Photo;
import org.qihoo.cube.util.data.Preference;
import org.qihoo.cube.util.image.AsyncTaskListener;
import org.qihoo.cube.util.image.ImageManager;

import java.util.List;

/**
 * Created by wangjinfa on 2015/8/31.
 */
public class EditPhotoActivity extends Activity implements View.OnClickListener,
         Gallery.OnGalleryUpdateListener, AdapterView.OnItemClickListener{

    private ImageButton back;
    private Button selectAll;

    private GridView photoViews;
    private List<Photo> photos;

    private RelativeLayout deletePhoto;
    private RelativeLayout movePhoto;

    private Gallery gallery;

    private Preference userDetail;

    private boolean allSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myphoto_incube);

        initViews();

        // 获取用户数据
        userDetail = Preference.getInstance();
        setGallery(userDetail.getCurrentGallery());
        setPhotos(gallery.getPhotos());
    }

    @Override
    protected void onResume() {
        super.onResume();
        BackgroundLogin.resume(this, 1);

        PhotoAdapter photoAdapter = new PhotoAdapter(this, gallery.getPhotos(), true);
        photoViews.setAdapter(photoAdapter);
    }

    public void initViews() {
        back = (ImageButton)findViewById(R.id.activity_myphoto_incube_finish);
        selectAll = (Button)findViewById(R.id.activity_myphoto_incube_checkall);

        photoViews = (GridView)findViewById(R.id.activity_myphoto_incube_gridview);

        deletePhoto = (RelativeLayout)findViewById(R.id.activity_myphoto_incube_delete);
        movePhoto = (RelativeLayout)findViewById(R.id.activity_myphoto_incube_returnphoto);

        registerEvent();
    }

    public void registerEvent() {
        back.setOnClickListener(this);
        selectAll.setOnClickListener(this);

        deletePhoto.setOnClickListener(this);
        movePhoto.setOnClickListener(this);
        photoViews.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == selectAll) {
            // 如果当前相册没有照片,则全选不可点
            if (gallery.empty()) {
                return ;
            }

            // 点击全选，可能是全选可能是取消全选
            allSelected = !allSelected;
            if (allSelected) {
                selectAll.setText("取消");

                // 所有photo对象都加入被选列表
                gallery.checkAllPhotos(true);
            } else {
                selectAll.setText("全选");

                // 清空被选列表
                gallery.checkAllPhotos(false);
            }

            // todo: 这里可以调整
            for (int i = 0; i < photoViews.getChildCount(); ++i) {
                View view = photoViews.getChildAt(i);
                CheckBox checkBox = (CheckBox)view.findViewById(R.id.select_pic_check);
                checkBox.setChecked(allSelected);
            }
            return ;
        }
        if (v == deletePhoto) {
            AsyncTaskListener listener = new AsyncTaskListener(){

                @Override
                public void onAsyncTaskComplete(Object... objects) {
                    // 如果没有被选中删除的照片, 不做任何处理
                    if (gallery.countCheckedPhotos() == 0) {
                        TastyToast.makeText(EditPhotoActivity.this, "未选中任何相册",
                                TastyToast.STYLE_ALERT).show();
                        return ;
                    }
                    // 传入回调,操作完成更新界面
                    gallery.setUpdateListener(EditPhotoActivity.this);
                    // 删除照片
                    gallery.deleteCheckedPhotos(true);
                    // 更新用户数据
                    userDetail.updateCurrentZone();
                }

            };

            MyDialog.dialogDelete(this, "删除后不可恢复，确定要删除吗？", listener);
            return ;
        }
        if (v == movePhoto) {
            AsyncTaskListener listener = new AsyncTaskListener(){

                @Override
                public void onAsyncTaskComplete(Object... objects) {
                    // 如果没有被选中移出的照片, 不做任何处理
                    if (gallery.countCheckedPhotos() == 0) {
                        Toast.makeText(EditPhotoActivity.this, "未选中任何相册",
                                Toast.LENGTH_SHORT).show();
                        return ;
                    }
                    // 传入回调,操作完成更新界面
                    gallery.setUpdateListener(EditPhotoActivity.this);
                    // 移出空间
                    gallery.deleteCheckedPhotos(false);
                    // 更新用户数据
                    userDetail.updateCurrentZone();

                }
            };

            MyDialog.dialogDelete(this, "确定要移出空间吗？", listener);
            return;
        }
        if (v == back) {
            this.finish();
        }
    }

    public void setGallery(Gallery gallery) {
        this.gallery = gallery;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    @Override
    public void onGalleryUpdate() {
        Toast.makeText(this, "照片已被移除", Toast.LENGTH_SHORT).show();
        PhotoAdapter photoAdapter = new PhotoAdapter(this, gallery.getPhotos(), true);
        photoViews.setAdapter(photoAdapter);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        View childView = photoViews.getChildAt(position);
        CheckBox checkBox = (CheckBox)childView.findViewById(R.id.select_pic_check);
        if (checkBox.isChecked()) {
            addAnimation(checkBox);
            checkBox.setChecked(false);
            photos.get(position).setChecked(false);
        } else {
            checkBox.setChecked(true);
            photos.get(position).setChecked(true);
        }
    }

    private void addAnimation(View view){
        float [] vaules = new float[]{0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f, 1.1f, 1.2f, 1.3f, 1.25f, 1.2f, 1.15f, 1.1f, 1.0f};
        AnimatorSet set = new AnimatorSet();
        set.playTogether(ObjectAnimator.ofFloat(view, "scaleX", vaules),
                ObjectAnimator.ofFloat(view, "scaleY", vaules));
        set.setDuration(150);
        set.start();
    }
}
