package org.qihoo.cube.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.tv.TvContentRating;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.qihoo.cube.R;
import org.qihoo.cube.adapter.PhotoAdapter;
import org.qihoo.cube.util.data.ApplicationManager;
import org.qihoo.cube.util.data.BackgroundLogin;
import org.qihoo.cube.util.data.Gallery;
import org.qihoo.cube.util.data.Preference;

import java.io.File;
import java.net.URI;

/**
 * Created by wangjinfa on 2015/9/1.
 */
public class GalleryActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener{

    private ImageButton back;
    private ImageView edit;

    private GridView photoViews;

    private RelativeLayout addPhoto;

    private LinearLayout emptyBody;

    private Preference userDetail;

    private Gallery gallery;

    private TextView headTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myphoto_add);

        Log.i("Run Slowly", "1");
        initViews();
        userDetail = Preference.getInstance();
        setGallery(userDetail.getCurrentGallery());

    }

    @Override
    protected void onResume() {
        super.onResume();
        BackgroundLogin.resume(this, 0);

        gallery = userDetail.getCurrentGallery();
        headTitle.setText(gallery.getName());
        if (gallery.empty()) {
            showEmptyBody(true);
            return ;
        }
        showEmptyBody(false);

        PhotoAdapter photoAdapter = new PhotoAdapter(this, gallery.getPhotos(), false);
        photoViews.setAdapter(photoAdapter);

    }

    public void initViews() {
        back = (ImageButton)findViewById(R.id.activity_myphoto_add_finish);
        edit = (ImageView)findViewById(R.id.activity_myphoto_add_check);

        photoViews = (GridView)findViewById(R.id.activity_myphoto_add_gridview);

        addPhoto = (RelativeLayout)findViewById(R.id.activity_myphoto_add_addbutton);

        emptyBody = (LinearLayout)findViewById(R.id.empty_zone_body);

        headTitle = (TextView)findViewById(R.id.title_head);

        registerEvent();
    }

    public void registerEvent() {
        back.setOnClickListener(this);
        edit.setOnClickListener(this);

        photoViews.setOnItemClickListener(this);

        addPhoto.setOnClickListener(this);
    }

    public void showEmptyBody(boolean show) {
        if (show) {
            photoViews.setVisibility(View.GONE);
            edit.setVisibility(View.GONE);
            emptyBody.setVisibility(View.VISIBLE);
            return ;
        }
        photoViews.setVisibility(View.VISIBLE);
        edit.setVisibility(View.VISIBLE);
        emptyBody.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        if (v == back) {
            // 退出当前gallery
            userDetail.setCurrentGallery(null);

            this.finish();
            return ;
        }
        if (v == edit) {
            // 跳转到编辑照片页面
            Intent intent = new Intent(this, EditPhotoActivity.class);
            startActivity(intent);
            return ;
        }
        if (v == addPhoto) {
            Intent intent = new Intent(this, SelectphotoActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        userDetail.setCurrentPhoto(gallery.getPhotos().get(position));

        Intent intent = new Intent(GalleryActivity.this, ViewPhotoActivity.class);
        startActivity(intent);

    }

    public void setGallery(Gallery gallery) {
        this.gallery = gallery;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            userDetail.setCurrentGallery(null);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();

        if (BackgroundLogin.restart(this)){
            //BackgroundLogin.resume(this, 1);

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("stateNumber", 1);
            startActivity(intent);
        }
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        BackgroundLogin.stop(this, 1);
    }
}
