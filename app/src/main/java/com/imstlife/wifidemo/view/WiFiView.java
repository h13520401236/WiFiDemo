package com.imstlife.wifidemo.view;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.imstlife.wifidemo.R;


/**
 * Created by hengweiyu on 2017/8/17.
 */

public class WiFiView extends ImageView {
    private final int wifi_disconnected = -1;// 断开
    private final int wifi_connected0 = 0;// 0格信号
    private final int wifi_connected1 = 1;// 1格信号
    private final int wifi_connected2 = 2;// 2格信号
    private final int wifi_connected3 = 3;// 3格信号
    private final int wifi_connected4 = 4;// 4格信号

    public WiFiView(Context context) {
        this(context, null);
    }

    public WiFiView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WiFiView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        updateWifiView(NetworkInfo.State.DISCONNECTED, ConnectivityManager.TYPE_WIFI, wifi_connected0);
    }

    public void updateWifiView(int wifiLevel) {
        switch (wifiLevel) {
            case wifi_disconnected:
                setImageResource(R.mipmap.wifi_error);
                break;
            case wifi_connected0:
                setImageResource(R.mipmap.wifi_error);
                break;
            case wifi_connected1:
                setImageResource(R.mipmap.wifi1);
                break;
            case wifi_connected2:
                setImageResource(R.mipmap.wifi2);
                break;
            case wifi_connected3:
                setImageResource(R.mipmap.wifi3);
                break;
            case wifi_connected4:
                setImageResource(R.mipmap.wifi4);
                break;
            default:
                setImageResource(R.mipmap.wifi_error);
                break;
        }
    }

    public void updateWifiView(NetworkInfo.State state, int type, int wifiLevel) {
        if (state == NetworkInfo.State.CONNECTED) { // 已连接
            switch (type) {
                case ConnectivityManager.TYPE_WIFI:
                    switch (wifiLevel) {
                        case wifi_disconnected:
                            setImageResource(R.mipmap.wifi_error);
                            break;
                        case wifi_connected0:
                            setImageResource(R.mipmap.wifi_error);
                            break;
                        case wifi_connected1:
                            setImageResource(R.mipmap.wifi1);
                            break;
                        case wifi_connected2:
                            setImageResource(R.mipmap.wifi2);
                            break;
                        case wifi_connected3:
                            setImageResource(R.mipmap.wifi3);
                            break;
                        case wifi_connected4:
                            setImageResource(R.mipmap.wifi4);
                            break;
                        default:
                            setImageResource(R.mipmap.wifi_error);
                            break;
                    }
                    break;
                case ConnectivityManager.TYPE_ETHERNET:
                    setImageResource(R.mipmap.wifi_ink);
                    break;
                case ConnectivityManager.TYPE_MOBILE:
                    setImageResource(R.mipmap.wifi_ink);
                    break;
                default:
                    setImageResource(R.mipmap.wifi_error);
                    break;
            }
        } else if (state == NetworkInfo.State.CONNECTING) { // 正在断开连接
            setImageResource(R.mipmap.wifi_error);
        } else if (state == NetworkInfo.State.DISCONNECTING) { // 正在断开连接
            setImageResource(R.mipmap.wifi_error);
        } else if (state == NetworkInfo.State.DISCONNECTED) { // 断开连接
            setImageResource(R.mipmap.wifi_error);
//        } else if (state == NetworkInfo.State.SUSPENDED) { // API解释：IP流量暂停。不懂神马鬼
//        } else if (state == NetworkInfo.State.UNKNOWN) { // 未知状态
        } else { // 包括SUSPENDED和UNKNOWN的其他未知状态
            setImageResource(R.mipmap.wifi_error);
        }
    }
}
