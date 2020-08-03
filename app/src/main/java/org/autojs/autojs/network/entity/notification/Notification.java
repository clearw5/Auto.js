package org.autojs.autojs.network.entity.notification;

import org.autojs.autojs.network.entity.user.User;

public class Notification {

    private boolean read;
    private String mergeId;
    private String importance;
    private String nid;
    private String type;
    private String path;
    private String readClass;
    private String datetime;
    private String from;
    private String bodyShort;
    private String datetimeISO;
    private User user;

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isRead() {
        return read;
    }

    public void setMergeId(String mergeId) {
        this.mergeId = mergeId;
    }

    public String getMergeId() {
        return mergeId;
    }

    public void setImportance(String importance) {
        this.importance = importance;
    }

    public String getImportance() {
        return importance;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public String getNid() {
        return nid;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setReadClass(String readClass) {
        this.readClass = readClass;
    }

    public String getReadClass() {
        return readClass;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getFrom() {
        return from;
    }

    public void setBodyShort(String bodyShort) {
        this.bodyShort = bodyShort;
    }

    public String getBodyShort() {
        return bodyShort;
    }

    public void setDatetimeISO(String datetimeISO) {
        this.datetimeISO = datetimeISO;
    }

    public String getDatetimeISO() {
        return datetimeISO;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public String toString() {
        return
                "Notification{" +
                        "read = '" + read + '\'' +
                        ",mergeId = '" + mergeId + '\'' +
                        ",importance = '" + importance + '\'' +
                        ",nid = '" + nid + '\'' +
                        ",type = '" + type + '\'' +
                        ",path = '" + path + '\'' +
                        ",readClass = '" + readClass + '\'' +
                        ",datetime = '" + datetime + '\'' +
                        ",from = '" + from + '\'' +
                        ",bodyShort = '" + bodyShort + '\'' +
                        ",datetimeISO = '" + datetimeISO + '\'' +
                        ",user = '" + user + '\'' +
                        "}";
    }
}
