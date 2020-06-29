package com.zwyl.guide.dialog.bean;

/**
 * Created by BinBinWang on 2018/3/21.
 */

public class BeanNowYear {
    /**
     * academicYearId : 002
     * academicYear : 2019-2020
     * rankNo : null
     */

    public String academicYearId;
    public String academicYear;
    public String rankNo;
    public BeanNowYear(String academicYearId, String academicYear) {
        this.academicYear = academicYear;
        this.academicYearId = academicYearId;
    }

}
