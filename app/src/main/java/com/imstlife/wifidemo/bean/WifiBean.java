package com.imstlife.wifidemo.bean;

/**
 * Created by lihaifeng on 17/3/10.
 */

public class WifiBean {


    /**
     * wifi连接名
     */
    private String name;

    /**
     * wifi密码
     */
    private String password;

    /**
     * wifi信号强度  Config.WIFI_STATE_XX
     */
    private int wifiStateNum;

    /**
     * wifi信号强度文本  强  若   极强
     */
    private String wifiStateText;



    /**
     * 安全性
     */
    private String safeText;


    /**
     * 连接状态
     */
    private boolean isConnected;


    /**
     * IP
     * 未连接时 为空
     */
    private String IP;

    /**
     * MAC
     */

    private String MAC;


    public WifiBean(String name, String password, int wifiStateNum, String wifiStateText, String safeText) {
        this.name = name;
        this.password = password;
        this.wifiStateNum = wifiStateNum;
        this.wifiStateText = wifiStateText;
        this.safeText = safeText;
    }

    public WifiBean() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getWifiStateNum() {
        return wifiStateNum;
    }

    public void setWifiStateNum(int wifiStateNum) {
        this.wifiStateNum = wifiStateNum;
    }

    public String getWifiStateText() {
        return wifiStateText;
    }

    public void setWifiStateText(String wifiStateText) {
        this.wifiStateText = wifiStateText;
    }

    public String getSafeText() {
        return safeText;
    }

    public void setSafeText(String safeText) {
        this.safeText = safeText;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public String getMAC() {
        return MAC;
    }

    public void setMAC(String MAC) {
        this.MAC = MAC;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }



    @Override
    public String toString() {
        return "WifiBean{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", wifiStateNum='" + wifiStateNum + '\'' +
                ", wifiStateText='" + wifiStateText + '\'' +

                ", safeText='" + safeText + '\'' +
                ", isConnected=" + isConnected +
                ", IP='" + IP + '\'' +
                ", MAC='" + MAC + '\'' +
                '}';
    }
}
