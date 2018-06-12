package com.imstlife.wifidemo.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.imstlife.wifidemo.R;
import com.imstlife.wifidemo.bean.WifiBean;


/**
 * Created by ADan on 2017/3/4.
 */

public class ForgetWifiDialog extends Dialog implements View.OnClickListener {


    private final Context mContext;
    private final DialogClickListener mListener;
    private View view;
    private String mTag;
    private WifiBean wifi;
    private TextView name;
    private TextView strong;
    private TextView ip;

    public ForgetWifiDialog(Context context, DialogClickListener listener) {
        super(context, 0);
        mContext = context;
        mListener = listener;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView();
    }

    public void setContentView() {
        view = View.inflate(mContext, R.layout.dialog_forget_wifi, null);
        view.findViewById(R.id.dialog_cancel).setOnClickListener(this);
        view.findViewById(R.id.dialog_forget).setOnClickListener(this);

        name = (TextView) view.findViewById(R.id.wifi_name);
        strong = (TextView) view.findViewById(R.id.wifi_strong);
        ip = (TextView) view.findViewById(R.id.wifi_ip);
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
            ip.setText(TextUtils.isEmpty(wifi.getIP()) ? "" : wifi.getIP());
        } else {
            name.setText("");
            strong.setText("");
            ip.setText("");
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

    public ForgetWifiDialog setTag(String tag) {
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
            case R.id.dialog_forget:
                if (mListener != null) {
                    mListener.onForget(this, wifi);
                }
                break;
        }
    }

    public interface DialogClickListener {
        void onForget(ForgetWifiDialog dialog, WifiBean wifiBean);

        void onCancel(ForgetWifiDialog dialog);
    }
}
