package org.qihoo.cube.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.qihoo.cube.R;
import org.qihoo.cube.adapter.GalleryAdapter;
import org.qihoo.cube.util.data.BackgroundLogin;
import org.qihoo.cube.util.data.Preference;
import org.qihoo.cube.util.data.Zone;


/**
 * Created by wangjinfa on 2015/8/30.
 */
public class ZoneActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener{

    private ImageButton editZone;

    private Button addGallery;
    private LinearLayout emptyZoneBody;

    private ListView galleryViews;

    private Zone zone;

    private Preference userDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cubezone);

        initView();

        userDetail = Preference.getInstance();
        setZone(userDetail.getCurrentZone());
    }

    @Override
    protected void onResume() {
        super.onResume();
        BackgroundLogin.resume(this, -1);
        if (zone.getGallerys().size() == 0) {
            showEmptyBody(true);
            return ;
        }
        showEmptyBody(false);
        GalleryAdapter galleryAdapter = new GalleryAdapter(this, zone.getGallerys(), false);
        galleryViews.setAdapter(galleryAdapter);
    }

    /**
     * 获取各个控件
     */
    public void initView() {
        editZone = (ImageButton)findViewById(R.id.activity_myphoto_add_checkall);

        addGallery = (Button)findViewById(R.id.btn_add_first_photo);
        emptyZoneBody = (LinearLayout)findViewById(R.id.empty_zone_body);

        galleryViews = (ListView)findViewById(R.id.gallery_listview);

        registerEvent();
    }

    /**
     * 注册事件
     */
    public void registerEvent() {
        editZone.setOnClickListener(this);
        addGallery.setOnClickListener(this);

        galleryViews.setOnItemClickListener(this);
    }

    /**
     * 是否显示空的空间Layout
     * @param show
     *
     */
    public void showEmptyBody(boolean show) {
        if (show) {
            editZone.setVisibility(View.GONE);
            emptyZoneBody.setVisibility(View.VISIBLE);
            galleryViews.setVisibility(View.GONE);
            return ;
        }
        editZone.setVisibility(View.VISIBLE);
        emptyZoneBody.setVisibility(View.GONE);
        galleryViews.setVisibility(View.VISIBLE);
    }

    /**
     * 事件处理方法
     * @param v
     */
    @Override
    public void onClick(View v) {
        if (v == addGallery) {
            Intent intent = new Intent();
            startActivity(this, AddGalleryActivity.class, intent);
            return ;
        }
        if (v == editZone) {
           Intent intent = new Intent();
           startActivity(this, EditGalleryActivity.class, intent);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // 设置点击的相册为当前相册
        userDetail.setCurrentGallery(zone.getGallerys().get(position));

        Intent intent = new Intent(this, GalleryActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            userDetail.setCurrentZone(null);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 页面跳转
     * @param context
     * @param dst
     * @param intent
     */
    public void startActivity(Context context, Class<?> dst, Intent intent) {
        intent.setClass(context, dst);
        startActivity(intent);
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();

        if (BackgroundLogin.restart(this)){
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("stateNumber", 0);
            startActivity(intent);
        }
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        BackgroundLogin.stop(this, 0);
    }

}
