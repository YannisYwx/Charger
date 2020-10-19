package com.sevenchip.charger.data.bean;

/**
 * Author : Alvin
 * CreateTime : 2020/7/18 19:55
 * Email : 923080261@qq.com
 * Description :
 */
public class Channel {

    private int id;
    private String name;
    private int voltage;
    private int mAh;
    private float temperature;
    private int status;
    private float current;
    private int type;
    private int capacity;
    private String statusInfo;

    public Channel() {
    }

    public Channel(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVoltage() {
        return voltage;
    }

    public void setVoltage(int voltage) {
        this.voltage = voltage;
    }

    public int getAh() {
        return mAh;
    }

    public void setAh(int ah) {
        mAh = ah;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public float getCurrent() {
        return current;
    }

    public void setCurrent(float current) {
        this.current = current;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getStatusInfo() {
        return statusInfo;
    }

    public void setStatusInfo(String statusInfo) {
        this.statusInfo = statusInfo;
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", voltage=" + voltage +
                ", mAh=" + mAh +
                ", temperature=" + temperature +
                ", status=" + status +
                ", current=" + current +
                ", type=" + type +
                ", capacity=" + capacity +
                ", statusInfo='" + statusInfo + '\'' +
                '}';
    }
}
