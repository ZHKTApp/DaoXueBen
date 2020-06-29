package com.zwyl.guide.main.detaile;

import java.util.List;

public class ResultInfoBean {

    private String textBookChapterName; //:; //第一章",
    private int textBookChapterId; //:; //9c4c6e3959c745029455d728266af382",
    private int textBookChapterParentId; //:; //0",
//    private List<ChildrenBean> childrenList;
    private List<ResultInfoBean> childrenList;
    public String getTextBookChapterName() {
        return textBookChapterName;
    }

    public void setTextBookChapterName(String textBookChapterName) {
        this.textBookChapterName = textBookChapterName;
    }


    public int getTextBookChapterId() {
        return textBookChapterId;
    }

    public void setTextBookChapterId(int textBookChapterId) {
        this.textBookChapterId = textBookChapterId;
    }

    public int getTextBookChapterParentId() {
        return textBookChapterParentId;
    }

    public void setTextBookChapterParentId(int textBookChapterParentId) {
        this.textBookChapterParentId = textBookChapterParentId;
    }

    public List<ResultInfoBean> getChildrenList() {
        return childrenList;
    }

    public void setChildrenList(List<ResultInfoBean> childrenList) {
        this.childrenList = childrenList;
    }

    @Override
    public String toString() {
        return "ResultInfoBean{" +
                "textBookChapterName='" + textBookChapterName + '\'' +
                ", textBookChapterId='" + textBookChapterId + '\'' +
                ", textBookChapterParentId='" + textBookChapterParentId + '\'' +
                ", childrenList=" + childrenList +
                '}';
    }
}
