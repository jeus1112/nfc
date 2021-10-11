package com.example.sns_project_nfc.listener;

import com.example.sns_project_nfc.AnnunceInfo;

public interface OnPostListener {
    void onDelete(AnnunceInfo annunceInfo);
    void onModify();
}
