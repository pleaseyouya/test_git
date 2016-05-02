package org.qihoo.cube.activity;

import java.util.ArrayList;
import java.util.List;


import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import org.qihoo.cube.util.data.BackgroundLogin;
import org.qihoo.cube.util.image.ImageManager;
import org.qihoo.cube.util.media.MediaStoreBucket;

import org.qihoo.cube.R;
import org.qihoo.cube.util.media.MediaStoreCursorHelper;

public class SelectphotoActivity extends Activity implements OnItemClickListener{

    private ImageView activity_selectphoto_finish;
    private ListView activity_selectphoto_list;

    protected ImageLoader loader;
    protected DisplayImageOptions options;

    private ArrayList<String> selectedDataList = new ArrayList<String>();
    //private String editContent;
    //private String imgLocation;
    private boolean booleanExtra;
    private ChanceAdapter adapter;
    private ArrayList<MediaStoreBucket> mBuckets = new ArrayList<MediaStoreBucket>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_selectphoto);

        //loader = ImageLoader.getInstance();
        //loader.init(ImageLoaderConfiguration.createDefault(this));

        getItems();

        setAdapter();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadBuckets();
    }

    private void loadBuckets() {
        List<MediaStoreBucket> buckets = MediaStoreCursorHelper.getBucket(SelectphotoActivity.this);
        if (null != buckets && !buckets.isEmpty()) {
            mBuckets.clear();
            mBuckets.addAll(buckets);
            adapter.notifyDataSetChanged();
        }
    }

    private void getItems(){
        activity_selectphoto_finish = (ImageView) findViewById(R.id.activity_selectphoto_finish);
        activity_selectphoto_list = (ListView) findViewById(R.id.activity_selectphoto_list);

        ImageManager imagerManager = new ImageManager();
        loader = imagerManager.getLoader();
        options = imagerManager.getOptions();

        loader.init(ImageLoaderConfiguration.createDefault(this));

        activity_selectphoto_finish.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }

        });
    }

    private void setAdapter(){
        adapter = new ChanceAdapter(this, mBuckets);
        activity_selectphoto_list.setAdapter(adapter);
        activity_selectphoto_list.setOnItemClickListener(this);
    }

    @SuppressLint("InflateParams")
    private class ChanceAdapter extends BaseAdapter {
        private Context mActThis;
        private ArrayList<MediaStoreBucket> mBuckets;

        public ChanceAdapter(Context mActThis, ArrayList<MediaStoreBucket> mBuckets) {
            this.mActThis = mActThis;
            this.mBuckets = mBuckets;
        }

        @Override
        public int getCount() {
            return mBuckets.size();
        }

        @Override
        public Object getItem(int position) {
            return mBuckets.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ItemAlbum itemAlbum = null;
            if (convertView == null) {
                itemAlbum = new ItemAlbum();
                convertView = LayoutInflater.from(mActThis).inflate(R.layout.selectphoto_list_item, null);
                itemAlbum.itemIVAlbum = (ImageView) convertView.findViewById(R.id.selectphoto_item_photo);
                itemAlbum.itemTVAlbum = (TextView) convertView.findViewById(R.id.selectphoto_item_text);
                convertView.setTag(itemAlbum);
            } else {
                itemAlbum = (ItemAlbum) convertView.getTag();
            }
            MediaStoreBucket mediaStoreBucket = mBuckets.get(position);
            String id = mediaStoreBucket.getId();
            if( id != null){
                ArrayList<String> listPath = MediaStoreCursorHelper.queryPhoto((Activity) mActThis, id);
                String firstImgPath = listPath.get(0);

                loader.displayImage("file://" + firstImgPath, itemAlbum.itemIVAlbum, options);
            } else {
                ArrayList<String> list = MediaStoreCursorHelper.queryAllPhoto((Activity) mActThis);
                String string = list.get(0);
//				DisplayImageOptions options = new DisplayImageOptions.Builder()
//				        .bitmapConfig(Bitmap.Config.RGB_565)
//				        .imageScaleType(ImageScaleType.EXACTLY)
//						.showStubImage(R.drawable.group_item_pic_bg)
//						.cacheInMemory(true)
//						.cacheOnDisc(true)
//						.build();
                loader.displayImage("file://" + string, itemAlbum.itemIVAlbum, options);
            }
            String name = mediaStoreBucket.getName();
            if (name.contains("All Photos")) {
                itemAlbum.itemTVAlbum.setText("最近照片");
            } else {
                itemAlbum.itemTVAlbum.setText(name);
            }
            return convertView;
        }
    }

    class ItemAlbum {
        ImageView itemIVAlbum;
        TextView itemTVAlbum;
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MediaStoreBucket item = (MediaStoreBucket) parent.getItemAtPosition(position);
        if (null != item) {
            loadBucketId(item);
        }
    }

    private void loadBucketId(MediaStoreBucket item) {
        Intent intent = new Intent(this, SelectphotoActivityGrid.class);
        if (item.getName().contains("All Photos")) {
            ArrayList<String> list = MediaStoreCursorHelper.queryAllPhoto(this);
            intent.putExtra("listPath", list);
            intent.putExtra("selectedDataList", selectedDataList);
            intent.putExtra("name", "最近照片");
            intent.putExtra("album", booleanExtra);
//			startActivity(intent);
        } else {
            String id = item.getId();
            ArrayList<String> listPath = MediaStoreCursorHelper.queryPhoto(this, id);
            intent.putExtra("listPath", listPath);
            intent.putExtra("selectedDataList", selectedDataList);
            intent.putExtra("name", item.getName());
            intent.putExtra("album", booleanExtra);
//			startActivity(intent);
        }

        startActivityForResult(intent, 0);
    }

	/*private void loadBucketId(MediaStoreBucket item) {
		Intent intent = new Intent(this, SelectphotoActivityGrid.class);
		if (item.getName().contains("All Photos")) {
			ArrayList<String> list = MediaStoreCursorHelper.queryAllPhoto(this);
			intent.putExtra("listPath", list);
			intent.putExtra("selectedDataList", selectedDataList);
			intent.putExtra("name", "最近照片");
			intent.putExtra("album", booleanExtra);
//			startActivity(intent);
		} else {
			String id = item.getId();
			ArrayList<String> listPath = MediaStoreCursorHelper.queryPhoto(this, id);
			intent.putExtra("listPath", listPath);
			intent.putExtra("selectedDataList", selectedDataList);
			intent.putExtra("name", item.getName());
			intent.putExtra("album", booleanExtra);
//			startActivity(intent);
		}
		setResult(-1, intent);
		SelectphotoActivity.this.finish();
	}*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode == -1 && data != null) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                @SuppressWarnings("unchecked")
                ArrayList<String> selList1 = (ArrayList<String>) bundle.getSerializable("dataList");
                //ArrayList<String> pathList = bundle.getStringArrayList("listPath");
                ArrayList<String> selList2 = bundle.getStringArrayList("selectedDataList");
                //editContent = bundle.getString("editContent");
                //imgLocation = bundle.getString("name");

                if (selList2 != null) {
                    selectedDataList = selList2;
                } else if (selList1 != null) {
                    selectedDataList = selList1;
                }
                booleanExtra = bundle.getBoolean("album");
                Log.i("youzh", booleanExtra+"----");
            }
        } else if (resultCode == 2 && data != null) {
            finish();
        }
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

        if (BackgroundLogin.restart(this)){
            //BackgroundLogin.resume(this, 1);

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
