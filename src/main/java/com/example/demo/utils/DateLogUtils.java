package com.example.demo.utils;

import java.util.Date;

public class DateLogUtils {


    public static Date startDateLog() {
        Date startDate = new Date();
        System.out.println("开始时间"+DateUtils.dateToStr(startDate));
        return startDate;
    }

    public static void endDateLog(Date startDate) {
        Date endDate = new Date();
        System.out.println("结束时间"+DateUtils.dateToStr(endDate));
        System.out.println(DateUtils.getDistanceTime(startDate,endDate));
    }
}
