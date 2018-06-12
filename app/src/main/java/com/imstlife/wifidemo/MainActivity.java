package com.imstlife.wifidemo;

import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.imstlife.wifidemo.base.NetThread;
import com.imstlife.wifidemo.bean.WifiBean;
import com.imstlife.wifidemo.utils.WifiUtils;
import com.imstlife.wifidemo.view.ConnectWifiDialog;
import com.imstlife.wifidemo.view.ForgetWifiDialog;
import com.imstlife.wifidemo.view.WiFiView;

import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, WifiUtils.OnWIfiStateListener {

    private WiFiView wiFiView;
    private ListView wifiList;
    private List<WifiBean> WiFis;
    private WifiAdapter wifiAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WifiUtils.getInstance(this).setWifiStateListener(this);
        wiFiView = (WiFiView) findViewById(R.id.wifi_view);
        wifiList = (ListView) findViewById(R.id.wifi_list);
        findViewById(R.id.check_wifi_state).setOnClickListener(this);
        findViewById(R.id.system_setting).setOnClickListener(this);
        findViewById(R.id.getwifi_list).setOnClickListener(this);
        wifiList.setOnItemClickListener(wifiClickListener);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {//获取当前的网络状态
            case R.id.check_wifi_state:
                if (wiFiView.getVisibility() != View.VISIBLE) {
                    wiFiView.setVisibility(View.VISIBLE);
                }
                NetThread.getInstance().setOnNetChangeListener(new NetThread.OnNetChangeListener() {
                    @Override
                    public void onNetChange(NetworkInfo.State state, int type, int wifiLevel) {
                        wiFiView.updateWifiView(state, type, wifiLevel);
                    }
                });
                NetThread.getInstance().start();
                break;
            case R.id.system_setting://打开系统设置，切换wifi活网络状态。
                Intent intent2 = new Intent(Settings.ACTION_SETTINGS);
                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                MyApplication.getInstances()
                        .startActivity(intent2);
                break;

            case R.id.getwifi_list:
                if (WiFis == null || WiFis.size() < 1) {
                } else {
                    wifiAdapter = new WifiAdapter(WiFis);
                    wifiList.setAdapter(wifiAdapter);
                }
                break;
        }
    }

    //获取wifi强度
    @Override
    public void updateWifiState(int wifiState) {

    }

    //获取wifi列表
    @Override
    public void onWifiScanResultReturn(List<WifiBean> wifiBeanList) {
        for (int i = 0; i < wifiBeanList.size(); i++) {
            Log.e("wifi", "onWifiScanResultReturn: " + wifiBeanList.get(i));
        }
        if (WiFis == null) {
            WiFis = wifiBeanList;
            wifiAdapter = new WifiAdapter(WiFis);
            wifiList.setAdapter(wifiAdapter);
        } else {
            WiFis.clear();
            WiFis.addAll(wifiBeanList);
            notifyWifi();
        }
    }

    private void notifyWifi() {
        if (WiFis == null || WiFis.size() < 1) {
            Log.e("wifi", "notifyWifi: " + "刷新wifi列表中");
        } else {
            if (wifiList.getVisibility() != View.VISIBLE) {
                wifiList.setVisibility(View.VISIBLE);
            }
            wifiAdapter.notifyDataSetChanged();
        }
    }

    class WifiAdapter extends BaseAdapter {
        private List<WifiBean> list;

        public WifiAdapter(List<WifiBean> wiFis) {
            list = wiFis;
        }

        @Override
        public int getCount() {
            return list.size() == 0 ? 0 : list.size();
        }

        @Override
        public WifiBean getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder = null;
            if (view == null) {
                holder = new ViewHolder();
                view = View.inflate(MainActivity.this, R.layout.item_wlan, null);
                holder.name = view.findViewById(R.id.wifiItem_name);
                holder.desc = view.findViewById(R.id.wifiItem_describe);
                holder.wifi = view.findViewById(R.id.wifiItem_wifi);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            WifiBean wifiBean = getItem(i);
            if (wifiBean != null) {
                holder.name.setText(TextUtils.isEmpty(wifiBean.getName()) ? "" : wifiBean.getName());
                if (wifiBean.isConnected()) {
                    holder.desc.setText("已连接");
                } else {
                    String desc = TextUtils.isEmpty(wifiBean.getSafeText()) ? "" : "通过"
                            + wifiBean.getSafeText() + "进行保护";
                    holder.desc.setText(desc);
                }
                holder.wifi.updateWifiView(wifiBean.getWifiStateNum());
                Log.e("wifi", "getView: " + wifiBean.getWifiStateNum());
            }
            return view;
        }


        class ViewHolder {
            public TextView name;
            public TextView desc;
            public WiFiView wifi;
        }
    }

    private View selectedWifiItem;
    private AdapterView.OnItemClickListener wifiClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (WiFis != null && WiFis.size() > position) {
                WifiBean wifiBean = WiFis.get(position);
                if (wifiBean != null) {
                    if (wifiBean.isConnected()) {
                        showForgetDialog(wifiBean);
                    } else {
                        if (TextUtils.isEmpty(wifiBean.getPassword())) {
                            showConnectWifiDialog(wifiBean);
                        } else {
                            connectWifi(wifiBean.getName(), wifiBean.getPassword());
                        }
                        selectedWifiItem = view;
                    }
                }
            }
        }
    };

    private void connectWifi(String name, String password) {
        WifiConfiguration configuration = WifiUtils.getInstance(this).checkPreWhetherConfigured(name);
        if (configuration != null) {
            boolean result = WifiUtils.getInstance(this).removeConfigByNetId(configuration.networkId);
        }
        int netId = WifiUtils.getInstance(this).addWifiConfig(name, password);

        boolean isSuccess = WifiUtils.getInstance(this).connectWifByNetWorkId(netId);
        WifiUtils.getInstance(this).reConnect();
    }


    private void showForgetDialog(WifiBean wifiBean) {
        new ForgetWifiDialog(this, new ForgetWifiDialog.DialogClickListener() {
            @Override
            public void onForget(ForgetWifiDialog dialog, WifiBean wifiBean) {
                disConnectWifi(wifiBean.getName());
                wifiAdapter.notifyDataSetChanged();
                dialog.cancel();
            }

            @Override
            public void onCancel(ForgetWifiDialog dialog) {

            }
        }).show(wifiBean);
    }

    private void disConnectWifi(String name) {
        WifiConfiguration configuration = WifiUtils.getInstance(this).checkPreWhetherConfigured(name);
        if (configuration != null) {
//            boolean result = wifiUtils.removeConfigByNetId(configuration.networkId);
            WifiUtils.getInstance(this).delPreWhetherConfigured(name);
        }
    }


    private void showConnectWifiDialog(WifiBean wifiBean) {
        new ConnectWifiDialog(this, new ConnectWifiDialog.DialogClickListener() {
            @Override
            public void onOk(ConnectWifiDialog dialog, WifiBean wifiBean) {
                connectWifi(wifiBean.getName(), wifiBean.getPassword());
                if (selectedWifiItem != null) {
                    WifiAdapter.ViewHolder tag = (WifiAdapter.ViewHolder) selectedWifiItem.getTag();
                    if (tag != null && tag.desc != null) {
                        tag.desc.setText("正在连接...");
                    }
                }
                wifiAdapter.notifyDataSetChanged();
                dialog.cancel();
            }

            @Override
            public void onCancel(ConnectWifiDialog dialog) {
            }
        }).show(wifiBean);
    }
}
