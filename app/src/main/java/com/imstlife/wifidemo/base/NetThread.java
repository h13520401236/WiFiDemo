package com.imstlife.wifidemo.base;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.ImageView;

import com.imstlife.wifidemo.MyApplication;
import com.imstlife.wifidemo.R;


/**
 * Created by ADan on 2017/5/2.
 */

public class NetThread {

    private static NetThread instance;
    /**
     * 网络类型
     */
    private int networkType;
    /**
     * 网络状态
     */
    private NetworkInfo.State networkState;
    /**
     * wifi信号等级
     */
    private int wifiLevel;
    private final int wifi_disconnected = -1;// 断开
    private final int wifi_connected0 = 0;// 0格信号
    private final int wifi_connected1 = 1;// 1格信号
    private final int wifi_connected2 = 2;// 2格信号
    private final int wifi_connected3 = 3;// 3格信号
    private final int wifi_connected4 = 4;// 4格信号

    Handler mainHandler;
    private BaseThread wifiThread;
    private WifiManager wifiManager;
    private ConnectivityManager connectivityManager;
    private OnNetChangeListener listener;

    public static NetThread getInstance() {
        if (instance == null) {
            synchronized (NetThread.class) {
                if (instance == null) {
                    instance = new NetThread();
                }
            }
        }
        return instance;
    }

    private NetThread() {
        init(MyApplication.getInstances());
    }

    public void init(Context context) {

        mainHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        if (listener != null) {
                            listener.onNetChange(networkState, networkType, wifiLevel);
                        }
                        break;
                }
            }
        };

        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

    }

    private void startWifiThread() {

        wifiThread = new BaseThread(new BaseRunnable() {
            @Override
            public void whileRun() {
                NetworkInfo info = connectivityManager.getActiveNetworkInfo();
                if (info == null) {
                    // 网络未连接
                    networkType = ConnectivityManager.TYPE_WIFI;
                    networkState = NetworkInfo.State.DISCONNECTED;
                    wifiLevel = wifi_disconnected;
                } else if (info.getState() == NetworkInfo.State.CONNECTED) {
                    // 网络已连接
                    networkState = NetworkInfo.State.CONNECTED;
                    wifiLevel = WifiManager.calculateSignalLevel(wifiManager.getConnectionInfo().getRssi(), 5);
                    switch (info.getType()) {
                        case ConnectivityManager.TYPE_WIFI:
                        case ConnectivityManager.TYPE_ETHERNET:
                        case ConnectivityManager.TYPE_MOBILE:
                            networkType = info.getType();
                            break;
                        default:
                            networkType = ConnectivityManager.TYPE_WIFI;
                            break;
                    }
                } else {
                    // 网络未连接
                    networkType = ConnectivityManager.TYPE_WIFI;
                    networkState = info.getState();
                    wifiLevel = wifi_disconnected;
                }
                mainHandler.sendEmptyMessage(0);
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        wifiThread.start();
    }

    public void start() {
        if (wifiThread == null) {
            startWifiThread();
        }
    }

    public void stop() {
        if (wifiThread != null) {
            wifiThread.end();
            wifiThread = null;
        }
    }

    public void setOnNetChangeListener(OnNetChangeListener listener) {
        this.listener = listener;
    }

    public interface OnNetChangeListener {
        /**
         * 网络状态监测结果
         *
         * @param state
         * @param type
         * @param wifiLevel
         */
        void onNetChange(NetworkInfo.State state, int type, int wifiLevel);
    }

    public void updateWifiView(ImageView wifiView, NetworkInfo.State state, int type, int wifiLevel) {
        if (state == NetworkInfo.State.CONNECTED) { // 已连接
            switch (type) {
                case ConnectivityManager.TYPE_WIFI:
                    switch (wifiLevel) {
                        case wifi_disconnected:
                            wifiView.setImageResource(R.mipmap.wifi_error);
                            break;
                        case wifi_connected0:
                            wifiView.setImageResource(R.mipmap.wifi_error);
                            break;
                        case wifi_connected1:
                            wifiView.setImageResource(R.mipmap.wifi1);
                            break;
                        case wifi_connected2:
                            wifiView.setImageResource(R.mipmap.wifi2);
                            break;
                        case wifi_connected3:
                            wifiView.setImageResource(R.mipmap.wifi3);
                            break;
                        case wifi_connected4:
                            wifiView.setImageResource(R.mipmap.wifi4);
                            break;
                        default:
                            wifiView.setImageResource(R.mipmap.wifi_error);
                            break;
                    }
                    break;
                case ConnectivityManager.TYPE_ETHERNET:
                    wifiView.setImageResource(R.mipmap.wifi_ink);
                    break;
                case ConnectivityManager.TYPE_MOBILE:
                    wifiView.setImageResource(R.mipmap.wifi_ink);
                    break;
                default:
                    wifiView.setImageResource(R.mipmap.wifi_error);
                    break;
            }
        } else if (state == NetworkInfo.State.CONNECTING) { // 正在断开连接
            wifiView.setImageResource(R.mipmap.wifi_error);
        } else if (state == NetworkInfo.State.DISCONNECTING) { // 正在断开连接
            wifiView.setImageResource(R.mipmap.wifi_error);
        } else if (state == NetworkInfo.State.DISCONNECTED) { // 断开连接
            wifiView.setImageResource(R.mipmap.wifi_error);
//        } else if (state == NetworkInfo.State.SUSPENDED) { // API解释：IP流量暂停。不懂神马鬼
//        } else if (state == NetworkInfo.State.UNKNOWN) { // 未知状态
        } else { // 包括SUSPENDED和UNKNOWN的其他未知状态
            wifiView.setImageResource(R.mipmap.wifi_error);
        }
    }

    public boolean isWifiEnabled() {
        return wifiManager.isWifiEnabled();
    }

    public void setWifiEnabled(boolean enabled) {
        wifiManager.setWifiEnabled(enabled);
    }

    public boolean isNetUsable() {
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }
}
