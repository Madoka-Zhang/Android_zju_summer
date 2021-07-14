package com.example.chapter3.homework.Cycler;

import java.util.ArrayList;
import java.util.List;

public class TestDataSet {

    public static List<TestData> getData() {
        List<TestData> result = new ArrayList();
        result.add(new TestData("まどか", "お早う"));
        result.add(new TestData("天帝子", "哒哒哒，早上好"));
        result.add(new TestData("咸鱼聊天群", "群友们在干什么喵"));
        result.add(new TestData("苇名屑一郎", "我要守护苇名"));
        result.add(new TestData("卷怪", "太卷了"));
        result.add(new TestData("ミク", "ねねねねね"));
        result.add(new TestData("断魂生", "这地球生存环境越来越不行了啊"));
        result.add(new TestData("断舍离", "我哥哥又犯病了"));
        result.add(new TestData("龙主", "上次的那笔交易你考虑如何了"));
        result.add(new TestData("这个可好玩了", "+1+1"));
        result.add(new TestData("Stemming", "I will kill you"));
        return result;
    }

}