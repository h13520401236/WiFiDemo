package com.imstlife.wifidemo.utils;

import android.app.Service;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.imstlife.wifidemo.bean.WifiBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by lihaifeng on 17/3/9.
 */

public class WifiUtils {

    private static final String TAG = "WifiUtils";

    /**
     * 实例
     */
    private static WifiUtils ins;

    /**
     * wifi管理api
     */
    private WifiManager wifiManager;

    /**
     * wifi管理api 中的wifi信息
     */
    private WifiInfo wifiInfo;

    /**
     * 自动刷新wifi信息timer
     */
    private Timer timer;


    /**
     * 自动刷新wifi信息任务
     */
    private TimerTask timerTask;

    /**
     * 调用处可设置此监听
     */
    private OnWIfiStateListener onWIfiStateListener;


    /**
     * 是否开启刷新timmer
     */
    private boolean refTollege;

    //WIFI列表
    public List<WifiBean> wifiBeens;

    /**
     * wifi管理api中的wifi列表
     */
    private List<ScanResult> scanResults;


    public static final WifiUtils getInstance(Context context) {
        return ins == null ? ins = new WifiUtils(context) : ins;
    }

    public WifiUtils(Context context) {
        wifiManager = (WifiManager) context.getSystemService(Service.WIFI_SERVICE);
        wifiInfo = wifiManager.getConnectionInfo();
        startRefWifiTimer();
    }

    /**
     * 设置wifi状态监听回调
     *
     * @param onWIfiStateListener
     * @return
     */
    public WifiUtils setWifiStateListener(OnWIfiStateListener onWIfiStateListener) {
        this.onWIfiStateListener = onWIfiStateListener;
        return ins;
    }


    /**
     * 在主线程刷新UI
     */
    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Config.WHAT_REF_WIFI_STATE_NUM:
                    Log.i(TAG, "state:" + msg.arg1);
                    onWIfiStateListener.updateWifiState(msg.arg1);
                    break;
                case Config.WHAT_REF_WIFI_SCAN_RESULT:
                    onWIfiStateListener.onWifiScanResultReturn((List<WifiBean>) msg.obj);
                    break;
            }
        }
    };

    /**
     * wifi状态刷新
     */
    private void startRefWifiTimer() {

        timer = new Timer();
        timer.schedule(timerTask = new TimerTask() {
            int level;

            @Override
            public void run() {
                Log.i(TAG, "isOpen:" + isWifiOpen());
                if (isWifiOpen()) {//当wifi打开时
                    //刷新wifi信号
                    refWifiStateNum(level);
                    //刷新wifi列表
                    refWifiList();
                } else {
                    //wifi关闭时不作处理
                    refWifiStateNum(0);
                }
            }
        }, 0, 3000);
    }

    /**
     * 刷新wifi信号强度
     *
     * @param level
     */
    private void refWifiStateNum(int level) {
        wifiInfo = wifiManager.getConnectionInfo();
        //获得信号强度值
        level = wifiInfo.getRssi();
        Message msg = Message.obtain();
        //根据获得的信号强度发送信息
        msg.arg1 = getWifiStateNum(level);
        msg.what = Config.WHAT_REF_WIFI_STATE_NUM;
        handler.sendMessage(msg);
    }


    /**
     * 设置是否刷新
     *
     * @param tollege
     */
    public void setScanResultTollege(boolean tollege) {
        this.refTollege = tollege;
        refWifiList();
    }

    /**
     * 刷新wifi列表
     */
    private void refWifiList() {

//        if (!refTollege) {//关闭状态不刷新
//            return;
//        }

        scanResults = getScanResults();
        if (scanResults != null) {
            wifiBeens = new ArrayList<WifiBean>();
            Log.i("TAG", "wifi列表：" + scanResults.size());
            for (int i = 0; i < scanResults.size(); i++) {
                WifiBean wifiBean = new WifiBean();
                wifiBean.setName(scanResults.get(i).SSID);
                wifiBean.setSafeText(getKeyType(scanResults.get(i).SSID));
                wifiBean.setWifiStateNum(getWifiStateNum(scanResults.get(i).level));
                wifiBean.setWifiStateText(getWifiStateText(scanResults.get(i).level));
                wifiBean.setConnected(wifiManager.getConnectionInfo().getSSID().substring(1, wifiManager.getConnectionInfo().getSSID().length() - 1).equals(scanResults.get(i).SSID));
                if (wifiBean.isConnected()) {
                    wifiBean.setIP(getConnectedIPAddress() + "");
                    wifiBean.setMAC(getConnectedMacAddress());
                }
                wifiBeens.add(wifiBean);
            }
        } else {
            Log.i("TAG", "wifi列表：null");
        }
        if (wifiBeens != null && wifiBeens.size() > 0) {
            sort(wifiBeens);
            Message msg = Message.obtain();
            msg.what = Config.WHAT_REF_WIFI_SCAN_RESULT;
            msg.obj = wifiBeens;
            handler.removeMessages(Config.WHAT_REF_WIFI_SCAN_RESULT);
            handler.sendMessage(msg);
        }
    }


    /**
     * 停止刷新wifi计时器
     */
    private void stopRefWifiTimer() {

        refTollege = false;

        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }

    }

    /**
     * 打开Wifi
     */
    public void openWifi() {
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
    }

    public void reConnect(){
        wifiManager.saveConfiguration();
        wifiManager.reconnect();
    }

    /**
     * 是否打开Wifi
     */
    public boolean isWifiOpen() {
        return wifiManager.isWifiEnabled();
    }

    /**
     * 关闭Wifi
     */
    public void closeWifi() {
        if (wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
        }
    }

    /**
     * Wifi状态
     * return Config.WIFI_STATE_XXXXX
     */
    public int checkState() {
        return wifiManager.getWifiState();
    }

    /**
     * 开启wifi扫描
     */
    public boolean startWifiScan() {
        //判断wifi是否开启
        if (checkState() == WifiManager.WIFI_STATE_ENABLED) {
            return wifiManager.startScan();
        }
        return false;
    }

    /**
     * 获得扫描结果
     *
     * @return
     */
    public List<ScanResult> getScanResults() {
        //先开启扫描获得最新wifi结果集
        if (startWifiScan()) {
            this.scanResults = wifiManager.getScanResults();
            return scanResults;
        }
        return null;
    }


    /**
     * 获取当前环境下所有网络配置信息
     *
     * @return
     */
    public List<WifiConfiguration> getConfiguredNetworks() {
        return wifiManager.getConfiguredNetworks();
    }

    /**
     * 删除已存在配置中的所有该网络名称
     *
     * @param SSID 网络名称（WIFI名称）
     * @return
     */
    public void delPreWhetherConfigured(String SSID) {
        List<WifiConfiguration> existingConfigs = getConfiguredNetworks();
        for (WifiConfiguration configuration : existingConfigs) {
            Log.i(TAG, "configuration:" + configuration.SSID);
            if (configuration.SSID.equals("\"" + SSID + "\"")) {
                wifiManager.removeNetwork(configuration.networkId);
            }
        }
    }

    /**
     * 检查之前是否配置过该网，并返回该网络配置
     *
     * @param SSID 网络名称（WIFI名称）
     * @return 存在则返回网络配置对象WifiConfiguration，否则返回null
     */
    public WifiConfiguration checkPreWhetherConfigured(String SSID) {
        List<WifiConfiguration> existingConfigs = getConfiguredNetworks();
        if (existingConfigs != null) {
            for (WifiConfiguration configuration : existingConfigs) {
                Log.i(TAG, "configuration:" + configuration.SSID);
                if (configuration.SSID.equals("\"" + SSID + "\"")) {
                    return configuration;
                }
            }

        }

        return null;
    }

    /**
     * 添加一个新网络到本地网络列表，添加完成后默认是不激活状态，需要掉用enableNetwork函数连接
     *
     * @param SSID     wifi名称
     * @param password 需要连接wifi密码
     * @return 添加成功返回网络标示(networkId)
     */
    public int addWifiConfig(String SSID, String password) {
        //检查本地配置列表是否存在，不存在进行添加
        if (checkPreWhetherConfigured(SSID) == null) {
            WifiConfiguration wifiConfiguration = new WifiConfiguration();
            wifiConfiguration.SSID = "\"" + SSID + "\"";
            wifiConfiguration.preSharedKey = "\"" + password + "\"";
            wifiConfiguration.hiddenSSID = false;
            wifiConfiguration.status = WifiConfiguration.Status.ENABLED;
            return wifiManager.addNetwork(wifiConfiguration);
        }
        return -1;
    }


    public boolean removeConfigByNetId(int netId) {
        return wifiManager.removeNetwork(netId);
    }

    /**
     * 通过指定networkId连接wifi
     *
     * @param networkId
     * @return 连接成功返回true，否则返回false
     */
    public boolean connectWifByNetWorkId(int networkId) {
        List<WifiConfiguration> wifiConfigurations = getConfiguredNetworks();
        if (wifiConfigurations == null && wifiConfigurations.size() <= 0) {
            return false;
        }
        for (WifiConfiguration configuration : wifiConfigurations) {
            if (configuration.networkId == networkId) {
                return wifiManager.enableNetwork(networkId, true); //激活该ID Wifi连接
            }
        }

        return false;
    }

    /**
     * 获取秘钥类型
     *
     * @param SSID wifi名称
     * @return
     */
    public String getKeyType(String SSID) {
        if (scanResults == null) {
            getScanResults();
        }
        for (ScanResult scResult : scanResults) {
            if (!TextUtils.isEmpty(scResult.SSID) && scResult.SSID.equals(SSID)) {
                String capabilities = scResult.capabilities;
                if (!TextUtils.isEmpty(capabilities)) {
                    if (capabilities.contains("WPA") || capabilities.contains("wpa")) {
                        return "WPA/WPA2";
                    } else if (capabilities.contains("WEP") || capabilities.contains("wep")) {
                        return "WEP";
                    } else {
                        return "";
                    }
                }
            }
        }
        return null;
    }

    /**
     * 得到当前连接Wifi信息
     *
     * @return
     */
    public WifiInfo getCurConnectedWifiInfo() {
        return wifiManager.getConnectionInfo();
    }

    /**
     * 得到连接的MAC地址
     *
     * @return
     */
    public String getConnectedMacAddress() {
        WifiInfo wifiInfo = getCurConnectedWifiInfo();
        return (wifiInfo == null) ? "null" : wifiInfo.getMacAddress();
    }

    /**
     * 得到连接的名称SSID
     *
     * @return
     */
    public String getConnectedSSID() {
        WifiInfo wifiInfo = getCurConnectedWifiInfo();
        return (wifiInfo == null) ? "null" : wifiInfo.getSSID();
    }

    //得到连接的IP地址
    public int getConnectedIPAddress() {
        WifiInfo wifiInfo = getCurConnectedWifiInfo();
        return (wifiInfo == null) ? 0 : wifiInfo.getIpAddress();
    }

    //得到连接的networkId
    public int getConnectedID() {
        WifiInfo wifiInfo = getCurConnectedWifiInfo();
        return (wifiInfo == null) ? 0 : wifiInfo.getNetworkId();
    }


    private void sort(final List<WifiBean> list) {

        Collections.sort(list, new Comparator<WifiBean>() {

            @Override
            public int compare(WifiBean arg0, WifiBean arg1) {

                return arg1.getWifiStateNum() - arg0.getWifiStateNum();
            }

        });
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Collections.sort(list, new Comparator<WifiBean>() {
//
//                    @Override
//                    public int compare(WifiBean arg0, WifiBean arg1) {
//
//                        return arg1.getWifiStateNum() - arg0.getWifiStateNum();
//                    }
//
//                });
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
    }

    /**
     * 获取信号强度文本 极弱－极强
     *
     * @param level
     * @return
     */
    private String getWifiStateText(int level) {
        String str = "弱";
        if (level <= 0 && level >= -50) {
            str = "极强";
        } else if (level < -50 && level >= -70) {
            str = "强";
        } else if (level < -70 && level >= -80) {
            str = "中";
        } else if (level < -80 && level >= -100) {
            str = "弱";
        } else {
            str = "极弱";
        }
        return str;
    }

    /**
     * 获取信号强度 1-5
     *
     * @param level
     * @return
     */
    private int getWifiStateNum(int level) {
        int wifistateNum = 0;
        if (level <= 0 && level >= -50) {
            wifistateNum = Config.WIFI_STATE_4;
        } else if (level < -50 && level >= -70) {
            wifistateNum = Config.WIFI_STATE_3;
        } else if (level < -70 && level >= -80) {
            wifistateNum = Config.WIFI_STATE_2;
        } else if (level < -80 && level >= -100) {
            wifistateNum = Config.WIFI_STATE_1;
        } else {
            wifistateNum = Config.WIFI_STATE_0;
        }
        return wifistateNum;
    }

    public interface OnWIfiStateListener {

        /**
         * 刷新wifi状态  UI线程可直接使用
         *
         * @param wifiState Config.WIFI_STATE_XX
         */
        void updateWifiState(int wifiState);


        /**
         * 扫描出wifi列表时调用
         */
        void onWifiScanResultReturn(List<WifiBean> wifiBeanList);
    }


}
