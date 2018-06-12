package com.imstlife.wifidemo.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.imstlife.wifidemo.R;
import com.imstlife.wifidemo.bean.WifiBean;


/**
 * Created by ADan on 2017/3/4.
 */

public class ConnectWifiDialog extends Dialog implements View.OnClickListener {


    private final Context mContext;
    private final DialogClickListener mListener;
    private View view;
    private String mTag;
    private TextView name;
    private TextView strong;
    private EditText pwd;
    private CheckBox showPwd;
    private WifiBean wifi;
    private Button ok;

    public ConnectWifiDialog(Context context, DialogClickListener listener) {
        super(context, 0);
        mContext = context;
        mListener = listener;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView();
    }

    public void setContentView() {
        view = View.inflate(mContext, R.layout.dialog_connet_wifi, null);
        view.findViewById(R.id.dialog_cancel).setOnClickListener(this);
        ok = (Button) view.findViewById(R.id.dialog_ok);
        ok.setOnClickListener(this);
        name = (TextView) view.findViewById(R.id.wifi_name);
        strong = (TextView) view.findViewById(R.id.wifi_strong);
        pwd = (EditText) view.findViewById(R.id.wifi_pwd);
        showPwd = (CheckBox) view.findViewById(R.id.wifi_showpwd);
        ok.setBackgroundResource(R.mipmap.dialog_button_pressed);
        ok.setTextColor(Color.BLACK);
        ok.setClickable(false);
        showPwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

//                pwd.setTransformationMethod(isChecked ? HideReturnsTransformationMethod.getInstance() : PasswordTransformationMethod.getInstance());
//                pwd.setInputType(isChecked ? EditorInfo.TYPE_TEXT_VARIATION_PASSWORD : EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

                if (isChecked) {
                    // 显示密码
                    pwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD); //字符
                    pwd.setSelection(pwd.getText().length());
                } else {
                    // 隐藏密码
                    pwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD); //字符
                    pwd.setSelection(pwd.getText().length());
                }
            }
        });
        pwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) {
                    if (!ok.isClickable()) {
                        ok.setBackgroundResource(R.drawable.selector_dialong_button);
                        ok.setTextColor(Color.BLACK);
                        ok.setClickable(true);
                    }
                } else {
                    if (ok.isClickable()) {
                        ok.setBackgroundResource(R.mipmap.dialog_button_pressed);
                        ok.setTextColor(Color.GRAY);
                        ok.setClickable(false);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        super.setContentView(view);
    }

    public void show(WifiBean wifi) {
        this.wifi = wifi;
        if (wifi != null) {
            name.setText(TextUtils.isEmpty(wifi.getName()) ? "" : wifi.getName());
            String strong = "";
            switch (wifi.getWifiStateNum()) {
                case 0:
                    strong = "弱";
                    break;
                case 1:
                    strong = "一般";
                    break;
                case 2:
                    strong = "较强";
                    break;
                case 3:
                    strong = "强";
                    break;
                case 4:
                    strong = "极强";
                    break;
            }
            this.strong.setText(strong);
        } else {
            name.setText("");
            strong.setText("");
        }
        super.show();
    }

    @Override
    public void cancel() {
        if (mListener != null) {
            mListener.onCancel(this);
        }
        super.cancel();
    }

    public ConnectWifiDialog setTag(String tag) {
        mTag = tag;
        return this;
    }

    public String getTag() {
        return mTag;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_cancel:
                cancel();
                if (mListener != null) {
                    mListener.onCancel(this);
                }
                break;
            case R.id.dialog_ok:
                if (mListener != null) {
                    String pwd = this.pwd.getText().toString().trim();
                    wifi.setPassword(pwd);
                    mListener.onOk(this, wifi);
                }
                break;
        }
    }

    public interface DialogClickListener {
        void onOk(ConnectWifiDialog dialog, WifiBean wifiBean);

        void onCancel(ConnectWifiDialog dialog);
    }
}
