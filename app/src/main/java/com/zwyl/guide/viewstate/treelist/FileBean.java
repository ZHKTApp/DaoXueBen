package com.zwyl.guide.viewstate.treelist;


public class FileBean {
    @TreeNodeId
    private int _id;
    @TreeNodePid
    private int parentId;
    @TreeNodeLabel
    private String name;
    private long length;
    private String desc;
    @TreeNodeClickid
    private String clickId;

    public FileBean(int _id, int parentId, String name, String clickId) {
        super();
        this._id = _id;
        this.parentId = parentId;
        this.name = name;
        this.clickId = clickId;
    }

}