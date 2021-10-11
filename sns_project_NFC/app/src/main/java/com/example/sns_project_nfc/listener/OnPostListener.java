package com.example.sns_project_nfc.listener;

import com.example.sns_project_nfc.AnnunceInfo;
import com.example.sns_project_nfc.UserInfo;

public interface OnPostListener {
    void onDelete(AnnunceInfo annunceInfo);
    void onDeleteuser(UserInfo userInfo);
    void onModify();
}
