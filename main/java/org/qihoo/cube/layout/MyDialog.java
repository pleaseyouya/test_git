package org.qihoo.cube.layout;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import org.qihoo.cube.util.image.AsyncTaskListener;

public class MyDialog {

	public static void dialog(Context mContext, String content) {
		Builder builder = new Builder(mContext);
		builder.setMessage(content);
		builder.setPositiveButton("确定", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				}
		}).show();
	}

    public static void dialogDelete(Context mContext,String content, final AsyncTaskListener listener) {
        Builder builder = new Builder(mContext);
        builder.setMessage(content);
        builder.setPositiveButton("确定", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                listener.onAsyncTaskComplete();
            }
        });
        builder.setNegativeButton("取消", new OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }

        }).show();
    }
}
