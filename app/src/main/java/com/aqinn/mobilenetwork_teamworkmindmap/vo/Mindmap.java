package com.aqinn.mobilenetwork_teamworkmindmap.vo;

/**
 * @author Aqinn
 * @date 2020/6/14 11:57 PM
 */
public class Mindmap {

    private String mmId;

    private String name;

    private String date;

    private String imgPath;

    public Mindmap(String mmId, String name, String date, String imgPath) {
        this.mmId = mmId;
        this.name = name;
        this.date = date;
        this.imgPath = imgPath;
    }

    public Mindmap(String mmId, String name) {
        this.mmId = mmId;
        this.name = name;
    }

    public Mindmap() {
    }

    public String getMmId() {
        return mmId;
    }

    public void setMmId(String mmId) {
        this.mmId = mmId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }
}
