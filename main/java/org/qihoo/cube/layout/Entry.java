package org.qihoo.cube.layout;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.qihoo.cube.R;
import org.qihoo.cube.util.file.FileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

/**
 * Created by wangjinfa on 2015/8/27.
 */
public class Entry extends LayoutAbstract implements View.OnClickListener{

    private EditText password;
    private Button confirm;

    private View container;

    public Entry(Context context) {
        super.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setListener(EventListener listener) {
        this.listener = listener;
    }

    public void inflate() {
        container = inflater.inflate(R.layout.activity_cubezone, null);
        password = (EditText)container.findViewById(R.id.password);
        confirm = (Button)container.findViewById(R.id.confirm);

        confirm.setOnClickListener(this);

    }

    @Override
    public View getView() {
        return container;
    }

    @Override
    public void onClick(View v) {
        Object[] params = new Object[1];
        params[0] = password.getText().toString();
        listener.process(params);
    }

}
