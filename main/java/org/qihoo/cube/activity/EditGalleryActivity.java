package org.qihoo.cube.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.greycellofp.tastytoast.TastyToast;

import org.qihoo.cube.R;
import org.qihoo.cube.adapter.GalleryAdapter;
import org.qihoo.cube.adapter.OnAllCheckedListener;
import org.qihoo.cube.layout.MyDialog;
import org.qihoo.cube.util.data.ApplicationManager;
import org.qihoo.cube.util.data.BackgroundLogin;
import org.qihoo.cube.util.data.Gallery;
import org.qihoo.cube.util.data.Preference;
import org.qihoo.cube.util.data.Zone;
import org.qihoo.cube.util.image.AsyncTaskListener;

/**
 * Created by wangjinfa on 2015/9/4.
 */
public class EditGalleryActivity extends Activity implements View.OnClickListener,
        OnAllCheckedListener, Zone.OnZoneUpdateListener, AdapterView.OnItemClickListener{

    // 返回按钮
    private ImageButton back;
    // 全选按钮,如果当前是全选状态,则变成取消全选
    private Button selectAll;

    // 相册列表
    private ListView galleryViews;

    // 删除按钮
    private RelativeLayout delete;

    private Zone zone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        initViews();

        // 获取当前空间
        setZone(Preference.getInstance().getCurrentZone());
    }

    @Override
    protected void onResume() {
        super.onResume();
        BackgroundLogin.resume(this, 0);

        // todo: 这里可以调整
        GalleryAdapter galleryAdapter = new GalleryAdapter(this, zone.getGallerys(), true);
        galleryAdapter.setAllCheckedListener(this);
        galleryViews.setAdapter(galleryAdapter);
    }

    /**
     * 初始化控件
     */
    public void initViews() {
        back = (ImageButton)findViewById(R.id.activity_edit_finish);
        selectAll = (Button)findViewById(R.id.activity_edit_checkall);

        galleryViews = (ListView)findViewById(R.id.activity_edit_listview);

        delete = (RelativeLayout)findViewById(R.id.activity_edit_delete);

        registerEvent();
    }

    /**
     * 注册事件
     */
    public void registerEvent() {
        back.setOnClickListener(this);
        selectAll.setOnClickListener(this);
        delete.setOnClickListener(this);
        galleryViews.setOnItemClickListener(this);
    }

    /**
     * 点击事件处理
     * @param v
     *      当前被点击的View
     */
    @Override
    public void onClick(View v) {
        if (v == back) {
            finish();
            return ;
        }
        if (v == selectAll) {
            // 当前未全选,则全选相册
            if (selectAll.getText().equals("全选")) {
                selectAll.setText("取消");
                checkedAllGallerys(true);
            } else {
                // 当前已经全选,则取消全选
                selectAll.setText("全选");
                checkedAllGallerys(false);
            }
            return ;
        }
        if (v == delete) {
            AsyncTaskListener listener = new AsyncTaskListener(){

                @Override
                public void onAsyncTaskComplete(Object... objects) {
                    //当前没有被选中的相册
                    if (zone.countCheckedGallerys() == 0) {
                        Toast.makeText(EditGalleryActivity.this, "未选中任何相册",
                                Toast.LENGTH_SHORT).show();
                        return ;
                    }
                    zone.setOnZoneUpdateListener(EditGalleryActivity.this);
                    zone.deleteCheckedGallerys();

                    // 更新用户数据
                    Preference.getInstance().updateCurrentZone();
                }

            };

            MyDialog.dialogDelete(this, "确定要删除相册吗？", listener);
        }
    }

    /**
     * 监听全选
     */
    @Override
    public void onAllChecked(boolean checked) {
        // 全选时,全选按钮变为取消全选
        if (checked) {
            selectAll.setText("取消");
        } else {
            selectAll.setText("全选");
        }
        // todo: 这里可以调整
    }

    /**
     * 监听Cube被编辑
     */
    @Override
    public void onZoneUpdate() {
        // todo: 这里可以调整
        GalleryAdapter galleryAdapter = new GalleryAdapter(this, zone.getGallerys(), true);
        galleryAdapter.setAllCheckedListener(this);
        galleryViews.setAdapter(galleryAdapter);
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

    /**
     * 全选或者取消全选相册
     * @param checked
     */
    public void checkedAllGallerys(boolean checked) {
        // 更新Gallery对象列表
        zone.setAllGallerysChecked(checked);
        // 更新界面
        checkedAllGalleryViews(checked);
    }

    /**
     * 全选或者取消全选相册CheckBox
     */
    public void checkedAllGalleryViews(boolean checked) {
        for (int i = 0; i < galleryViews.getChildCount(); ++i) {
            // 获取checkBox
            View item = galleryViews.getChildAt(i);
            ImageView imageButton = (ImageView)item.findViewById(R.id
                    .activity_edit_item_checkbox);

            // 选中或者取消选中
            if (checked) {
                imageButton.setImageResource(R.mipmap.edit_item_oncheck);
            } else {
                imageButton.setImageResource(R.mipmap.edit_item_notcheck);
            }
        }
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
        Log.i("Add Photo", "Gallery Item Clicked");
        View childView = galleryViews.getChildAt(position);
        ImageView check = (ImageView)childView.findViewById(R.id.activity_edit_item_checkbox);
        Gallery gallery = zone.getGallerys().get(position);
        // 当前相册已被选中,取消选中
        if (gallery.isChecked()) {
            check.setImageResource(R.mipmap.edit_item_notcheck);
            gallery.setChecked(false);
        } else {
            // 否则，选中相册
            check.setImageResource(R.mipmap.edit_item_oncheck);
            gallery.setChecked(true);
        }

        // 如果所有相册都被选中了,触发全选监听
        Preference userDetail = Preference.getInstance();
        if (userDetail.getCurrentZone().isAllGalleryChecked()) {
            onAllChecked(true);
        } else {
            onAllChecked(false);
        }
    }
}
