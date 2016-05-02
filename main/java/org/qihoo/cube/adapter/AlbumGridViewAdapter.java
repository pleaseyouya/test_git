package org.qihoo.cube.adapter;

import java.util.ArrayList;

import org.qihoo.cube.R;
import org.qihoo.cube.layout.SquareLayout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;

import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class AlbumGridViewAdapter extends BaseAdapter implements OnClickListener {

	private Context mContext;
	private ArrayList<String> dataList;
	private ArrayList<String> selectedDataList;
	private ImageLoader loader;
	private DisplayImageOptions options;
//	ImageLoader imageLoader = ImageLoader.getInstance();
//	DisplayImageOptions options = new DisplayImageOptions.Builder()
//	.showStubImage(R.drawable.group_item_pic_bg)
//	.bitmapConfig(Config.RGB_565)
//	.displayer(new FadeInBitmapDisplayer(300))
//	.imageScaleType(ImageScaleType.EXACTLY)
//	.cacheOnDisc(true)
//	.build();
	
	public AlbumGridViewAdapter(Context c, ArrayList<String> dataList, ArrayList<String> selectedDataList, ImageLoader loader, DisplayImageOptions options) {

		this.mContext = c;
		this.dataList = dataList;
		this.selectedDataList = selectedDataList;
		this.loader = loader;
		this.options = options;
	}

	@Override
	public int getCount() {
		return dataList.size();
	}

	@Override
	public Object getItem(int position) {
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private class ViewHolder {
		public ImageView imageView;
		public CheckBox checkBox;
        public SquareLayout grid_square;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.select_imageview, parent, false);
			viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image_view);
            viewHolder.grid_square = (SquareLayout) convertView.findViewById(R.id.grid_squarelayout);
			viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.select_pic_check);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		String path;
		if (dataList != null && dataList.size() > position)
			path = dataList.get(position);
		else
			path = "camera_default";
		if (path.contains("default")) {
			viewHolder.imageView.setImageResource(R.mipmap.addphoto_button_pressed);
		} else {
			
			loader.displayImage("file://" + path, viewHolder.imageView, options);
		}
        viewHolder.grid_square.setOnClickListener(this);
		viewHolder.checkBox.setTag(position);
		viewHolder.checkBox.setOnClickListener(this);
		viewHolder.checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					addAnimation(viewHolder.checkBox);
				}
			}
		});
		if (isInSelectedDataList(path)) {
			viewHolder.checkBox.setChecked(true);
		} else {
			viewHolder.checkBox.setChecked(false);
		}

		return convertView;
	}

	private boolean isInSelectedDataList(String selectedString) {
		if(selectedDataList != null){
			for (int i = 0; i < selectedDataList.size(); i++) {
				if (selectedDataList.get(i).equals(selectedString)) {
					return true;
				}
			}
		}
		return false;
	}
  
	@Override
	public void onClick(View view) {
        CheckBox toggleButton;
        if (view instanceof CheckBox) {
            toggleButton = (CheckBox) view;
        }else{
            toggleButton = (CheckBox) view.findViewById(R.id.select_pic_check);;
            if (toggleButton.isChecked()){
                toggleButton.setChecked(false);
            }else{
                toggleButton.setChecked(true);
            }
        }
        int position = (Integer) toggleButton.getTag();
        if (dataList != null && mOnItemClickListener != null && position < dataList.size()) {
            mOnItemClickListener.onItemClick(toggleButton, position, dataList.get(position), toggleButton.isChecked());
        }
	}

	private OnItemClickListener mOnItemClickListener;

	public void setOnItemClickListener(OnItemClickListener l) {
		mOnItemClickListener = l;
	}

	public interface OnItemClickListener {
		public void onItemClick(CheckBox toggleButton, int position, String path, boolean isChecked);
	}
    
	 /** 
     * 给CheckBox加点击动画，利用开源库nineoldandroids设置动画  
     * @param view 
     */  
    private void addAnimation(View view){  
        float [] vaules = new float[]{0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f, 1.1f, 1.2f, 1.3f, 1.25f, 1.2f, 1.15f, 1.1f, 1.0f};  
        AnimatorSet set = new AnimatorSet();  
        set.playTogether(ObjectAnimator.ofFloat(view, "scaleX", vaules),   
                ObjectAnimator.ofFloat(view, "scaleY", vaules));  
                set.setDuration(150);  
        set.start();  
    } 
}
