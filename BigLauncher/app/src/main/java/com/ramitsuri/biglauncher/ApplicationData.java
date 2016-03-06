package com.ramitsuri.biglauncher;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ramitsuri on 3/6/16.
 */
public class ApplicationData implements Parcelable {

    private String appName = "";
    private String packageName = "";
    private String className = "";
    private String versionName = "";
    private String versionCode = "";
    private String tags = "";
    private String icon = null;

    public ApplicationData(){}

    protected ApplicationData(Parcel in) {
        appName = in.readString();
        packageName = in.readString();
        className = in.readString();
        versionName = in.readString();
        tags = in.readString();
    }

    public static final Creator<ApplicationData> CREATOR = new Creator<ApplicationData>() {
        @Override
        public ApplicationData createFromParcel(Parcel in) {
            return new ApplicationData(in);
        }

        @Override
        public ApplicationData[] newArray(int size) {
            return new ApplicationData[size];
        }
    };

    public String getAppName(){
        return appName;
    }
    public void setAppName(String appName){
        this.appName = appName;
    }

    public String getPackageName(){
        return packageName;
    }
    public void setPackageName(String packageName){
        this.packageName = packageName;
    }

    public String getClassName(){
        return className;
    }
    public void setClassName(String className){
        this.className= className;
    }

    public String getVersionName(){
        return versionName;
    }
    public void setVersionName(String versionName){
        this.versionName = versionName;
    }

    public String getVersionCode(){
        return versionCode;
    }
    public void setVersionCode(String versionCode){
        this.versionCode = versionCode;
    }

    public String getIcon(){
        return icon;
    }

    public void setIcon(String icon){
        this.icon = icon;
    }

    public String getTags(){
        return tags;
    }

    public void setTags(String tags){
        this.tags = tags;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(appName);
        dest.writeString(packageName);
        dest.writeString(className);
        dest.writeString(versionName);
        dest.writeString(tags);
    }
}
