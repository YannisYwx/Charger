package com.sevenchip.charger.data.mode;

import com.sevenchip.charger.contract.MainContract;
import com.sevenchip.charger.contract.SettingsContract;
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
public class SettingsMode implements SettingsContract.Mode{

    @Override
    public void setChannelToDevice(Channel channel) {

    }

    private void test() {
        List<? extends Number> l1 = new ArrayList<>();
//        l1.add(new Integer(1));
//        l1.add(new Double(1.1));
        List<? super Number> l2 = new ArrayList<>();
        l2.add(1);
        l2.add(11.1);
        List<Integer> ints = new ArrayList<>();//正确，生成一个类型是Integer的集合
        List<Object> numbers = new ArrayList<>();//!!!错误，List<Integer>不是List<Object>的子类
        numbers.addAll(ints);
    }
}
