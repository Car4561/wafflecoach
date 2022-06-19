package com.peru.wafflecoach;

import android.util.Log;

public class Version {

    private String newVersion = "";
    private String currentVersion  = "";
    private String tittleVersion = "";
    private String descriptionVersion = "";


    public Version() {

    }
    public Version(String newVersion, String currentVersion, String tittleVersion, String descriptionVersion) {
        this.newVersion = newVersion;
        this.currentVersion = currentVersion;
        this.tittleVersion = tittleVersion;
        this.descriptionVersion = descriptionVersion;
    }

    public boolean itsUpdate(){
        if(currentVersion.equals(newVersion)){
            return  true;
        }
        Log.d("version",currentVersion);
        String[] currentVersionSplit = currentVersion.split("\\.");
        String[] newVersionSplit = newVersion.split("\\.");
        String  currentLastTwo = currentVersionSplit[1] + "." + currentVersionSplit[2];
        String  newVersionLastTwo = newVersionSplit[1] + "." + newVersionSplit[2];

        if( currentVersionSplit[0].equals(newVersionSplit[0])){

            return Double.parseDouble(currentLastTwo) >= Double.parseDouble(newVersionLastTwo);

        }else return Integer.parseInt(currentVersionSplit[0]) > Integer.parseInt(newVersionSplit[0]);

       /* if(currentVersion.equals(newVersion)){
            return  true;
        }*/
    }

    public String getNewVersion() {
        return newVersion;
    }

    public void setNewVersion(String newVersion) {
        this.newVersion = newVersion;
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(String currentVersion) {
        this.currentVersion = currentVersion;
    }

    public String getTittleVersion() {
        return tittleVersion;
    }

    public void setTittleVersion(String tittleVersion) {
        this.tittleVersion = tittleVersion;
    }

    public String getDescriptionVersion() {
        return descriptionVersion;
    }

    public void setDescriptionVersion(String descriptionVersion) {
        this.descriptionVersion = descriptionVersion;
    }
}
