package com.example.sns_project_nfc;

public class KeyInfo {
    private String passKey;

    KeyInfo(){}

    public KeyInfo(String passKey){
        this.passKey = passKey;
    }

    public String getPassKey(){ return this.passKey; }
    public void setPassKey(String passKey) { this.passKey = passKey; }
}
