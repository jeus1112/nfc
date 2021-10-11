package com.example.sns_project_nfc;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.example.sns_project_nfc.listener.OnPostListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import static com.example.sns_project_nfc.Util.isStorageUrl;
import static com.example.sns_project_nfc.Util.showToast;
import static com.example.sns_project_nfc.Util.storageUrlToName;

public class FirebaseHelper {                                                                           // part19 : Firevasehelper로 이동 (61')
    private Activity activity;
    private OnPostListener onPostListener;
    private int successCount;

    public FirebaseHelper(Activity activity) {
        this.activity = activity;
    }

    public void setOnPostListener(OnPostListener onPostListener){
        this.onPostListener = onPostListener;
    }

    public void storageDelete(final AnnunceInfo annunceInfo){                                                 // part16: 스토리지의 삭제 (13')
        FirebaseStorage storage = FirebaseStorage.getInstance();                                        // part17 : 스토리지 삭제 (문서) (19'50")
        StorageReference storageRef = storage.getReference();

        final String id = annunceInfo.getId();
        ArrayList<String> contentsList = annunceInfo.getContents();
        for (int i = 0; i < contentsList.size(); i++) {
            String contents = contentsList.get(i);
            if (isStorageUrl(contents)) {
                successCount++;                                                                             // part17 : 사진의 개수가 여러개인 게시물의 경우 (23'35")
                StorageReference desertRef = storageRef.child("posts/" + id + "/" + storageUrlToName(contents));    // part17: (((파이어베이스에서 삭제))) 파이에베이스 스토리지는 폴더가 없다, 하나하나가 객체로서 저장 (13'30")
                desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        successCount--;
                        storeDelete(id, annunceInfo);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        showToast(activity, "Error");
                    }
                });
            }
        }
        //storeDelete(id, postInfo); 호,,
    }

    private void storeDelete(final String id, final AnnunceInfo annunceInfo) {                                     // part15 : (((DB에서 삭제))) 스토리지에서는 삭제 x
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        if (successCount == 0) {
            firebaseFirestore.collection("posts").document(id)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            showToast(activity, "게시글을 삭제하였습니다.");
                            onPostListener.onDelete(annunceInfo);
                            //postsUpdate();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showToast(activity, "게시글을 삭제하지 못하였습니다.");
                        }
                    });
        }
    }
}
