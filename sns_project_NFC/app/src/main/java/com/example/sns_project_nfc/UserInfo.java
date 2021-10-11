package com.example.sns_project_nfc;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UserInfo {
    private String name;
    private String phoneNumber;
    private String birth; //주민번호
    private String address;
    private String building; //동수
    private String unit;    //호수
    private String photoUrl;
    private Date createdID;                                                                                 // + : 사용자 리스트 수정 (날짜 정보 추가)
    private String authState = null;
    private String userUID = null;

    public UserInfo(){}
    public UserInfo(String name, String phoneNumber, String birth, String address, String building, String unit, Date createdID, String photoUrl, String authState, String userUID){     // part5 : 생성자 초기화 (7')
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.birth = birth;
        this.address = address;
        this.building = building;
        this.unit = unit;
        this.createdID = createdID;                                                                         // + : 사용자 리스트 수정 (날짜 정보 추가)
        this.photoUrl = photoUrl;
        this.authState = authState;
        this.userUID = userUID;

    }

    public UserInfo(String name, String phoneNumber, String birth, Date createdID, String address, String building, String unit, String authState, String userUID){      // + : 사용자 리스트 수정(날짜 정보 추가)
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.birth = birth;
        this.address = address;
        this.building = building;
        this.unit = unit;
        this.createdID = createdID;
        this.authState = authState;
        this.userUID = userUID;

    }

    public Map<String, Object> getUserInfo(){
        Map<String, Object> docData = new HashMap<>();
        docData.put("name", name);
        docData.put("phoneNumber", phoneNumber);
        docData.put("birthDay", birth);
        docData.put("createdID", createdID);
        docData.put("address", address);
        docData.put("building", building);
        docData.put("unit", unit);
        docData.put("photoUrl", photoUrl);
        docData.put("authState", authState);
        docData.put("userUID", userUID);
        return  docData;
    }

    public String getName(){
        return this.name;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getPhoneNumber(){
        return this.phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber){
        this.phoneNumber = phoneNumber;
    }
    public String getBirthDay(){
        return this.birth;
    }
    public void setBirthDay(String birthDay){
        this.birth = birthDay;
    }
    public String getAddress(){
        return this.address;
    }
    public void setAddress(String address){
        this.address = address;
    }
    public String getPhotoUrl(){
        return this.photoUrl;
    }
    public void setPhotoUrl(String photoUrl){
        this.photoUrl = photoUrl;
    }

    public Date getCreatedID() { return createdID; }
    public void setCreatedID(Date createdID) { this.createdID = createdID; }

    public String  getAuthState() { return authState; }
    public void setAuthState(String authState) { this.authState = authState; }
    public String  getBuilding() { return building; }
    public void setBuilding(String building) { this.building = building; }
    public String  getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String  getUserUID() { return userUID; }
    public void setUserUID(String userUID) { this.userUID = userUID; }
}
