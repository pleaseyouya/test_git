package org.qihoo.cube.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.greycellofp.tastytoast.TastyToast;

import org.qihoo.cube.R;
import org.qihoo.cube.util.data.ApplicationManager;
import org.qihoo.cube.util.data.BackgroundLogin;
import org.qihoo.cube.util.data.Gallery;
import org.qihoo.cube.util.data.Preference;
import org.qihoo.cube.util.data.Zone;
import org.qihoo.cube.util.file.FileUtil;

/**
 * Created by wangjinfa on 2015/8/31.
 */
public class AddGalleryActivity extends Activity implements View.OnClickListener{

    private ImageView back;
    private EditText gallery;
    private ImageButton confirm;

    private Zone zone;

    private Preference userDetail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_photoname);

        initViews();

        userDetail = Preference.getInstance();
        setZone(userDetail.getCurrentZone());
    }

    public void initViews() {
        back = (ImageView)findViewById(R.id.activity_new_photoname_finish);
        gallery = (EditText)findViewById(R.id.activity_new_photoname_edit);
        confirm = (ImageButton)findViewById(R.id.activity_new_photoname_ok);

        registerEvent();
    }

    public void registerEvent() {
        back.setOnClickListener(this);
        confirm.setOnClickListener(this);
    }

    /**
     * 事件处理方法
     * @param v
     */
    @Override
    public void onClick(View v) {
        if (v == confirm) {
            String galleryName = gallery.getText().toString();
            if (TextUtils.isEmpty(galleryName)) {
                Toast.makeText(this, "请输入相冊名", Toast.LENGTH_SHORT).show();
                return ;
            }
            if (zone.existSameGallery(galleryName)) {
                Toast.makeText(this, "空间已存在该相册", Toast.LENGTH_SHORT).show();
                return ;
            }
            // 新建相册
            Gallery galleryObj = new Gallery(galleryName);

            String galleryPath = zone.getZonePath() + "/" + galleryName;
            //设置相册文件夹路径
            galleryObj.setGalleryPath(galleryPath);
            galleryObj.setEncryPath(galleryPath + "/encrypt");
            galleryObj.setSrcPath(galleryPath + "/src");
            galleryObj.setThumbPath(galleryPath + "/thumb");

            //添加相册进空间
            zone.addGallery(galleryObj);

            //新建相册文件夹
            FileUtil.mkdirs(galleryPath);
            FileUtil.mkdirs(galleryObj.getEncryPath());
            FileUtil.mkdirs(galleryObj.getSrcPath());
            FileUtil.mkdirs(galleryObj.getThumbPath());

            // 更新用户数据
            userDetail.updateCurrentZone();

            Toast.makeText(this, "添加相册成功", Toast.LENGTH_SHORT).show();
            finish();
            return ;
        }
        if (v == back) {
            AddGalleryActivity.this.finish();
        }
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        BackgroundLogin.resume(this, 0);
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
