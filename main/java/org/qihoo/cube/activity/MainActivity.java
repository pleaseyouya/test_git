package org.qihoo.cube.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.greycellofp.tastytoast.TastyToast;

import org.qihoo.cube.R;
import org.qihoo.cube.layout.MyDialog;
import org.qihoo.cube.util.data.AppChecker;
import org.qihoo.cube.util.data.ApplicationManager;
import org.qihoo.cube.util.data.BackgroundLogin;
import org.qihoo.cube.util.data.Preference;
import org.w3c.dom.Text;


public class MainActivity extends Activity implements View.OnClickListener, View.OnTouchListener,
        TextWatcher, View.OnFocusChangeListener{

    private Preference userDetail;

    private Button clearPassword;
    private EditText password;
    private Button confirm;

    private String textPassword;

    private RelativeLayout mainBg;

    private ApplicationManager applicationManager;

    private TextView moreHelp;

    // 是否退出之后重新进入app, true表示重新进入app
    private boolean appBack = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getAppBackFlag();

        Preference.setApplicationContext(getApplicationContext());
        userDetail = Preference.getInstance();

        setContentView(R.layout.activity_main);
        initView();

        applicationManager = (ApplicationManager)this.getApplication();

        Log.i("Gallery Activity", "Launch MainActivity");
    }

    public void initView() {
        confirm = (Button)findViewById(R.id.confirm);
        password = (EditText)findViewById(R.id.password);
        clearPassword = (Button)findViewById(R.id.clear_pwd);
        mainBg = (RelativeLayout)findViewById(R.id.main_bg);
        moreHelp = (TextView)findViewById(R.id.more_help);

        confirm.setOnClickListener(this);
        password.setOnTouchListener(this);
        password.addTextChangedListener(this);
        clearPassword.setOnClickListener(this);
        password.setOnFocusChangeListener(this);
        moreHelp.setOnClickListener(this);

        mainBg.setOnClickListener(this);
        setHint();
    }

    private void setHint(){
        SpannableString hint = new SpannableString("输入6-16位数字密码");
        ForegroundColorSpan hintColor = new ForegroundColorSpan(this.getResources().getColor(R.color.custom_blue));

        hint.setSpan(hintColor, 0, hint.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        password.setHint(new SpannedString(hint));
    }

    private void getAppBackFlag(){
        //ApplicationManager application = (ApplicationManager)getApplication();
        //application.setFlagForHome(true);

        Intent intent = this.getIntent();
        if ((intent != null) && (intent.getExtras() != null)){
            appBack = true;
        }else{
            appBack = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v == clearPassword) {
            textPassword = "";
            password.setText("");
        } else if (v == confirm){
            password.clearFocus();
            if (textPassword == null || textPassword.equals("")) {
                MyDialog.dialog(this, "请输入密码");
//                TastyToast.makeText(this, "请输入密码", TastyToast.STYLE_ALERT).show();
                return;
            }

            if (textPassword.length() < 6){
                MyDialog.dialog(this, "请至少输入6位数字");
//                TastyToast.makeText(this, "请至少输入6位数字", TastyToast.STYLE_ALERT).show();
                return;
            }else if (textPassword.length() > 16){
                MyDialog.dialog(this, "请不要超过16位数字");
//                TastyToast.makeText(this, "请不要超过16位数字", TastyToast.STYLE_ALERT).show();
                return;
            }

            for (int i = 0; i < textPassword.length(); i++){
                if ((textPassword.charAt(i) > '9') || (textPassword.charAt(i) < '0')){
                    MyDialog.dialog(this, "请您输入数字密码");
//                    TastyToast.makeText(this, "请您输入数字密码", TastyToast.STYLE_ALERT).show();
                    return;
                }
            }

            if (appBack){
                if (textPassword.equals(userDetail.getPassword())){
                    Intent intent = this.getIntent();
                    if ((intent != null) && (intent.getExtras() != null)){
                        int stateNumber = intent.getExtras().getInt("stateNumber");
                        if (stateNumber == -1){
                            ApplicationManager application = (ApplicationManager) this.getApplication();
                            application.setStateForEndActivity(0);
                        }
                    }

                    finish();
                    return;
                }
            }

            Intent intent = new Intent(MainActivity.this, ZoneActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            userDetail.setPassword(textPassword);
            if (!userDetail.existZone()) {
                // 新建空间，新建空间文件夹
                userDetail.addZone();
            }

            if (appBack){
                userDetail.setCurrentZone(userDetail.getZoneFromPreference());
            }
            startActivity(intent);
            finish();
        } else if (v == mainBg) {
            password.clearFocus();
        } else if (v == moreHelp) {
            Intent intent = new Intent(MainActivity.this, HelpActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (TextUtils.isEmpty(s)) {
            clearPassword.setVisibility(View.GONE);
        } else {
            clearPassword.setVisibility(View.VISIBLE);
        }
        textPassword = s.toString();
        if (textPassword.equals("")) {
            confirm.setClickable(false);
            confirm.setBackgroundResource(R.drawable.shape_round_rect_blue_hover);
        } else {
            confirm.setClickable(true);
            confirm.setBackgroundResource(R.drawable.shape_round_rect_blue);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (password.isFocusable())
            return false;
        password.setText("");
        password.setFocusable(true);
        password.setFocusableInTouchMode(true);
        password.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = this.getIntent();
        if ((intent != null) && (intent.getExtras() != null)){
            int stateNumber = intent.getExtras().getInt("stateNumber");
            if (stateNumber != -1){
                BackgroundLogin.resume(this, stateNumber);
            } /*else {
                ApplicationManager application = (ApplicationManager) this.getApplication();
                application.setStateForEndActivity(0);
            }*/
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            password.setFocusable(false);
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(password.getWindowToken(), 0);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                //ActivityManager am = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
                //am.restartPackage(this.getPackageName());
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_MAIN);// 添加Action属性
                intent.addCategory(Intent.CATEGORY_HOME);// 添加Category属性
                startActivity(intent);// 启动Activity
                break;
            default:
                break;
        }

        return true;//super.onKeyDown(keyCode, event);

    }
}
