package org.qihoo.cube.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import org.qihoo.cube.R;
import org.qihoo.cube.util.data.Photo;
import org.qihoo.cube.util.view.ViewHolder;

import java.util.List;

/**
 * Created by wangjinfa on 2015/9/1.
 */
public class PhotoAdapter extends BaseAdapter {

    private List<Photo> photos;
    private Context context;
    private boolean showEdit;

    public PhotoAdapter(Context context, List<Photo> photos, boolean showEdit) {
        this.context = context;
        this.photos = photos;
        this.showEdit = showEdit;
    }

    @Override
    public int getCount() {
        return photos.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.select_imageview, parent,
                    false);
        }
        ViewHolder holder = ViewHolder.get(convertView);

        ImageView imageView = holder.findViewById(R.id.image_view);
        final CheckBox checkBox = holder.findViewById(R.id.select_pic_check);

        // 如果是在相册界面,不显示图片右上角的checkBox,否则显示
        if (showEdit) {
            checkBox.setVisibility(View.VISIBLE);
        } else {
            checkBox.setVisibility(View.GONE);
        }

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if (isChecked) {
                    addAnimation(checkBox);
                    photos.get(position).setChecked(true);
                } else {
                    photos.get(position).setChecked(false);
                }
            }
        });

        // 缩略图的路径
        String imagePath = photos.get(position).getThumbPath();

        // 解析图片
        Bitmap src = BitmapFactory.decodeFile(imagePath);

        imageView.setImageBitmap(src);
        return convertView;
    }

    @Override
    public Object getItem(int position) {
        return photos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
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
