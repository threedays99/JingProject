package com.example.travel_uk.Model;

public class SiteModel {

    int id,drawableid;
    String sitename;
    String sitetype;

    public SiteModel() {
    }

    public SiteModel(int id, int drawableid, String sitename, String sitetype) {
        this.id = id;
        this.drawableid = drawableid;
        this.sitename = sitename;
        this.sitetype = sitetype;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDrawableid() {
        return drawableid;
    }

    public void setDrawableid(int drawableid) {
        this.drawableid = drawableid;
    }

    public String getSitename() {
        return sitename;
    }

    public void setSitename(String sitename) {
        this.sitename = sitename;
    }

    public String getSitetype() {
        return sitetype;
    }

    public void setSitetype(String sitetype) {
        this.sitetype = sitetype;
    }
}
