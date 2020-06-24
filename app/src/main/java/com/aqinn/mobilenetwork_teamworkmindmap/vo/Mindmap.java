package com.aqinn.mobilenetwork_teamworkmindmap.vo;

import com.aqinn.mobilenetwork_teamworkmindmap.model.TreeModel;

/**
 * @author Aqinn
 * @date 2020/6/14 11:57 PM
 */
public class Mindmap {

    private Long mmId;

    private String name;

    private Long shareId;

    private Integer shareOn;

    private String pwd;

    private TreeModel tm;

    public Mindmap() {

    }

    public Mindmap(String name) {
        this.name = name;
    }

    public Mindmap(Long mmId, String name) {
        this.mmId = mmId;
        this.name = name;
    }

    public Mindmap(Long mmId, String name, Long shareId, Integer shareOn, String pwd) {
        this.mmId = mmId;
        this.name = name;
        this.shareId = shareId;
        this.shareOn = shareOn;
        this.pwd = pwd;
    }

    public Long getMmId() {
        return mmId;
    }

    public void setMmId(Long mmId) {
        this.mmId = mmId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getShareId() {
        return shareId;
    }

    public void setShareId(Long shareId) {
        this.shareId = shareId;
    }

    public Integer getShareOn() {
        return shareOn;
    }

    public void setShareOn(Integer shareOn) {
        this.shareOn = shareOn;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public TreeModel getTm() {
        return tm;
    }

    public void setTm(TreeModel tm) {
        this.tm = tm;
    }

    @Override
    public String toString() {
        return "Mindmap{" +
                "mmId=" + mmId +
                ", name='" + name + '\'' +
                ", shareId=" + shareId +
                ", shareOn=" + shareOn +
                ", pwd='" + pwd + '\'' +
                '}';
    }
}
