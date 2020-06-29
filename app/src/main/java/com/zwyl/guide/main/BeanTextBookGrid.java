package com.zwyl.guide.main;

public class BeanTextBookGrid {

    public BeanTextBookGrid(String schoolTextBookId, String schoolTextBookName) {
        this.schoolTextBookId = schoolTextBookId;
        this.schoolTextBookName = schoolTextBookName;
    }

    public String schoolTextBookId;//课本id
    public String schoolTextBookName;//课本名称
    public String educationPeriodName;//学段名称（比如小学，初中等学段）
    public String schoolEducationGradeName;//学校年级名称
    public boolean updownType;//上下册
    public String schoolPublisherName;//出版社名称
}
