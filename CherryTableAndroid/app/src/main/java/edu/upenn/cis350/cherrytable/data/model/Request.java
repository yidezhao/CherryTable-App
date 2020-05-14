package edu.upenn.cis350.cherrytable.data.model;

import java.io.Serializable;

public class Request implements Serializable {
    private String title;
    private String desc;
    private String org;
    private int target;
    private int fulfilled;

    public Request(String title, String desc, String org, int target, int fulfilled) {
        this.title = title;
        this.desc = desc;
        this.org = org;
        this.target = target;
        this.fulfilled = fulfilled;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() { return desc; }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getOrg() { return org; }

    public void setOrg(String org) { this.org = org; }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public int getFulfilled() {
        return fulfilled;
    }

    public void setFulfilled(int fulfilled) {
        this.fulfilled = fulfilled;
    }
}
