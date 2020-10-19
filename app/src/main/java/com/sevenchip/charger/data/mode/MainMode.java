package com.sevenchip.charger.data.mode;

import com.sevenchip.charger.contract.MainContract;
import com.sevenchip.charger.data.bean.Channel;
import com.sevenchip.charger.data.bean.Charger;

import java.util.ArrayList;
import java.util.List;

/**
 * Author : Yannis.Ywx
 * CreateTime : 2020/7/26 21:44
 * Email : 923080261@qq.com
 * Description :
 */
public class MainMode implements MainContract.Mode{
    @Override
    public List<Charger> getChargers() {
        return initTestData();
    }


    private List<Charger> initTestData(){
        List<Charger> data = new ArrayList<>();
        Charger c1 = new Charger();
        c1.setName("Charger001");
        Channel ch1 = new Channel();
        ch1.setAh(3);
        ch1.setCurrent(2.1f);
        ch1.setTemperature(52.6f);
        ch1.setVoltage(128);
        ch1.setName("CH-1");
        ch1.setStatusInfo("12s LiHv charging");
        Channel ch2 = new Channel();
        ch2.setAh(2);
        ch2.setCurrent(3.5f);
        ch2.setTemperature(35.6f);
        ch2.setVoltage(220);
        ch2.setName("CH-2");
        ch2.setStatusInfo("Full");
        c1.setCH1(ch1);
        c1.setCH2(ch2);

        Charger c2 = new Charger();
        c1.setName("Charger002");
        Channel ch2_1 = new Channel();
        ch2_1.setAh(1);
        ch2_1.setCurrent(5f);
        ch2_1.setTemperature(49.9f);
        ch2_1.setVoltage(88);
        ch2_1.setName("CH-2-1");
        ch2_1.setStatusInfo("12s LiHv charging");
        Channel ch2_2 = new Channel();
        ch2_2.setAh(2);
        ch2_2.setCurrent(1.5f);
        ch2_2.setTemperature(25.6f);
        ch2_2.setVoltage(220);
        ch2_2.setName("CH-2-2");
        ch2_2.setStatusInfo("Full");
        c2.setCH1(ch2_1);
        c2.setCH2(ch2_2);

        data.add(c1);
        data.add(c2);
        return data;
    }
}
