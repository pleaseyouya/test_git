package org.qihoo.cube.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.qihoo.cube.R;
import org.qihoo.cube.util.data.Gallery;
import org.qihoo.cube.util.data.Preference;
import org.qihoo.cube.util.view.ViewHolder;

import java.util.List;

/**
 * Created by wangjinfa on 2015/8/31.
 */
public class GalleryAdapter extends BaseAdapter {

    // activity上下文
    private Context context;
    // 相册列表数据
    private List<Gallery> galleryList;
    // 是否显示编辑框
    private boolean showEdit;
    // 监听全选的listener
    private OnAllCheckedListener allCheckedListener;

    public GalleryAdapter(Context context, List<Gallery> galleryList, boolean showEdit) {
        this.context = context;
        this.galleryList = galleryList;
        this.showEdit = showEdit;
    }

    @Override
    public int getCount() {
        return galleryList.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.edit_list_item, parent,
                    false);
        }
        ViewHolder holder = ViewHolder.get(convertView);

        final ImageView check = (ImageView)holder.findViewById(R.id.activity_edit_item_checkbox);
        if (showEdit) {
            check.setVisibility(View.VISIBLE);
        } else {
            check.setVisibility(View.GONE);
        }
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gallery gallery = galleryList.get(position);

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
                    allCheckedListener.onAllChecked(true);
                } else {
                    allCheckedListener.onAllChecked(false);
                }
            }
        });

        TextView galleryName = holder.findViewById(R.id.activity_edit_item_text);
        galleryName.setText(galleryList.get(position).getName());
        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return galleryList.get(position);
    }

    public void setAllCheckedListener(OnAllCheckedListener allCheckedListener) {
        this.allCheckedListener = allCheckedListener;
    }

}
