package org.qihoo.cube.activity;

import java.io.File;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.qihoo.cube.adapter.AlbumGridViewAdapter;
import org.qihoo.cube.util.data.ApplicationManager;
import org.qihoo.cube.util.data.BackgroundLogin;
import org.qihoo.cube.util.data.Gallery;
import org.qihoo.cube.util.data.Photo;
import org.qihoo.cube.util.data.Preference;
import org.qihoo.cube.util.data.Zone;
import org.qihoo.cube.util.file.FileUtil;
import org.qihoo.cube.util.image.AsyncTaskListener;
import org.qihoo.cube.util.image.ImageLoadTask;
import org.qihoo.cube.util.image.ImageManager;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.victor.loading.rotate.RotateLoading;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.qihoo.cube.R;
import org.qihoo.cube.util.security.AESFunc;
import org.qihoo.cube.util.view.Thumbnail;

public class SelectphotoActivityGrid extends Activity implements AsyncTaskListener{

    protected ImageLoader loader;
    protected DisplayImageOptions options;

    private TextView activity_selectphoto_grid_title;
    private ImageButton activity_selectphoto_grid_finish;
    private GridView activity_selectphoto_grid_gridview;
    private TextView activity_selectphoto_grid_return;
    private AlbumGridViewAdapter gridImageAdapter;
    private LinearLayout selectedImageLayout;
    private HorizontalScrollView scrollview;
    private HashMap<String, ImageView> hashMap = new HashMap<String, ImageView>();

    private ArrayList<String> dataList;
    private ArrayList<String> selectedDataList = new ArrayList<String>();
    private String editContent;
    private String imgLocation;
    private boolean booleanExtra;

    private RotateLoading loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_selectphoto_grid);

        getItems();

        getIntentData();

        refelshGrid();

    }

    private void refelshGrid(){
        init();
        initListener();
    }

    private void getItems(){
        activity_selectphoto_grid_title = (TextView) findViewById(R.id.activity_selectphoto_grid_title);
        activity_selectphoto_grid_finish = (ImageButton) findViewById(R.id.activity_selectphoto_grid_finish);
        activity_selectphoto_grid_gridview = (GridView) findViewById(R.id.activity_selectphoto_grid_gridview);
        activity_selectphoto_grid_return = (TextView) findViewById(R.id.activity_selectphoto_grid_return);

        ImageManager imagerManager = new ImageManager();
        loader = imagerManager.getLoader();
        options = imagerManager.getOptions();
    }

    private void getIntentData(){
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            ArrayList<String> selList1 = (ArrayList<String>) bundle.getSerializable("dataList");
            ArrayList<String> pathList = bundle.getStringArrayList("listPath");
            ArrayList<String> selList2 = bundle.getStringArrayList("selectedDataList");
            editContent = bundle.getString("editContent");
            imgLocation = bundle.getString("name");

            if (pathList != null) {
                dataList = pathList;
            }

            if (selList2 != null) {
                selectedDataList = selList2;
            } else if (selList1 != null) {
                selectedDataList = selList1;
            }
            booleanExtra = bundle.getBoolean("album");
            Log.i("youzh", booleanExtra+"----");
        }
    }

    private void init() {
        activity_selectphoto_grid_finish.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectphotoActivityGrid.this, SelectphotoActivity.class);
                intent.putExtra("selectedDataList", selectedDataList);
                intent.putExtra("album", booleanExtra);
                setResult(-1 , intent);

                SelectphotoActivityGrid.this.finish();
            }
        });

        if(!TextUtils.isEmpty(imgLocation)){
            activity_selectphoto_grid_title.setText(imgLocation);
        } else {
            activity_selectphoto_grid_title.setText("最近照片");
        }

        gridImageAdapter = new AlbumGridViewAdapter(this, dataList, selectedDataList, loader, options);
        activity_selectphoto_grid_gridview.setAdapter(gridImageAdapter);
        selectedImageLayout = (LinearLayout) findViewById(R.id.selected_image_layout);
        scrollview = (HorizontalScrollView) findViewById(R.id.scrollview);

        initSelectImage();
    }

    private void initSelectImage() {
        if (selectedDataList == null)
            return;
        selectedImageLayout.removeAllViews();
        for (final String path : selectedDataList) {
            ImageView imageView = (ImageView) LayoutInflater.from(SelectphotoActivityGrid.this).inflate(R.layout.choose_imageview, selectedImageLayout, false);
            selectedImageLayout.addView(imageView);
            hashMap.put(path, imageView);
//			DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.pic_loading).cacheInMemory(true).cacheOnDisk(true).build();
            loader.displayImage("file://" + path, imageView, options);
            imageView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    removePath(path);
                    gridImageAdapter.notifyDataSetChanged();
                }
            });
        }
        //activity_selectphoto_grid_return.setText("完成(" + selectedDataList.size() + ")");
    }

    private void initListener() {

        gridImageAdapter.setOnItemClickListener(new AlbumGridViewAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(final CheckBox toggleButton, int position, final String path, boolean isChecked) {
                if (selectedDataList.size() >= 20) {
                    toggleButton.setChecked(false);
                    if (!removePath(path)) {
                        Toast.makeText(SelectphotoActivityGrid.this, "只能选择20张图片", Toast.LENGTH_SHORT).show();
                    }
                    return;
                }
                if (isChecked) {
                    if (!hashMap.containsKey(path)) {
                        ImageView imageView = (ImageView) LayoutInflater.from(SelectphotoActivityGrid.this).inflate(R.layout.choose_imageview, selectedImageLayout, false);
                        selectedImageLayout.addView(imageView);
                        imageView.postDelayed(new Runnable() {

                            @Override
                            public void run() {

                                int off = selectedImageLayout.getMeasuredWidth() - scrollview.getWidth();
                                if (off > 0) {
                                    scrollview.smoothScrollTo(off, 0);
                                }
                            }
                        }, 100);

                        hashMap.put(path, imageView);
                        selectedDataList.add(path);
//							DisplayImageOptions options = new DisplayImageOptions.Builder().showStubImage(R.drawable.group_item_pic_bg).cacheInMemory(true).cacheOnDisc(true).build();
                        loader.displayImage("file://" + path, imageView, options);
                        imageView.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                toggleButton.setChecked(false);
                                removePath(path);
                            }
                        });
                        //activity_selectphoto_grid_return.setText("完成(" + selectedDataList.size() + ")");
                    }
                } else {
                    removePath(path);
                }

            }
        });

        activity_selectphoto_grid_return.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent;

                if (booleanExtra) {
                    intent = new Intent();
                    intent.putExtra("datalist", selectedDataList);
                    setResult(RESULT_OK, intent);
                } else {
                    RelativeLayout progress = (RelativeLayout) findViewById(R.id.activity_selectphoto_grid_progress);
                    progress.setVisibility(View.VISIBLE);
                    loadingDialog = (RotateLoading)findViewById(R.id.activity_selectphoto_grid_rotateloading);
                    loadingDialog.start();

                    ImageLoadTask loadTask = new ImageLoadTask(selectedDataList, SelectphotoActivityGrid.this);
                    loadTask.execute();
                }


                //SelectphotoActivityGrid.this.finish();
            }
        });
    }

    private boolean removePath(String path) {
        if (hashMap.containsKey(path)) {
            selectedImageLayout.removeView(hashMap.get(path));
            hashMap.remove(path);
            removeOneData(selectedDataList, path);
            //activity_selectphoto_grid_return.setText("完成(" + selectedDataList.size() + ")");
            return true;
        } else {
            return false;
        }
    }

    private void removeOneData(ArrayList<String> arrayList, String s) {
        for (int i = 0; i < arrayList.size(); i++) {
            if (arrayList.get(i).equals(s)) {
                arrayList.remove(i);
                return;
            }
        }
    }


    private void DeleteImage(String imgPath){
        ContentResolver resolver = this.getContentResolver();
        Cursor cursor = MediaStore.Images.Media.query(resolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media._ID} , MediaStore.Images.Media.DATA + "=?",
                new String[]{imgPath} , null);
        boolean result = false;
        if (cursor.moveToFirst()){
            long id = cursor.getLong(0);
            Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Uri uri = ContentUris.withAppendedId(contentUri, id);
            int count = this.getContentResolver().delete(uri, null, null);
            result = count == 1;
        }else{
            File file = new File(imgPath);
            result = file.delete();
        }

        if (result){
            Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAsyncTaskComplete(Object... objects) {
        // TODO Auto-generated method stub
        //loadingDialog.stop();

        for (int j = 0; j < selectedDataList.size(); j++){
            for (int i = 0; i < dataList.size(); i++){
                if (dataList.get(i).equals(selectedDataList.get(j))){
                    dataList.remove(i);
                    i--;
                }
            }
        }

        String path;
        //int size = selectedDataList.size();
        for (int i = 0; i < selectedDataList.size(); i++){
            path = selectedDataList.get(i);
            DeleteImage(selectedDataList.get(i));
            if (removePath(path)){
                i--;
            }
        }

        refelshGrid();

        Intent intent = new Intent(SelectphotoActivityGrid.this, SelectphotoActivity.class);
        intent.putExtra("selectedDataList", selectedDataList);
        intent.putExtra("album", booleanExtra);
        setResult(2 , intent);
        finish();
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

