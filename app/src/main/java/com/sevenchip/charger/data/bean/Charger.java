package com.sevenchip.charger.data.bean;

/**
 * Author : Yannis.Ywx
 * CreateTime : 2020/7/18 19:54
 * Email : 923080261@qq.com
 * Description : Charger
 */
public class Charger {
    private int id;
    private String name;
    private Channel CH1;
    private Channel CH2;
    private UpstreamData channel01;
    private UpstreamData channel02;

    public Charger() {

    }

    public Charger(int id, String name, Channel CH1, Channel CH2) {
        this.id = id;
        this.name = name;
        this.CH1 = CH1;
        this.CH2 = CH2;
    }

    public Channel getCH1() {
        return CH1;
    }

    public void setCH1(Channel CH1) {
        this.CH1 = CH1;
    }

    public Channel getCH2() {
        return CH2;
    }

    public void setCH2(Channel CH2) {
        this.CH2 = CH2;
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

    @Override
    public String toString() {
        return "Charger{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", CH1=" + CH1 +
                ", CH2=" + CH2 +
                '}';
    }
}
