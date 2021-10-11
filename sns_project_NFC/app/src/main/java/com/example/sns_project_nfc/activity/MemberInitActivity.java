package com.example.sns_project_nfc.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.sns_project_nfc.R;
import com.example.sns_project_nfc.UserInfo;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;

import static com.example.sns_project_nfc.Util.INTENT_PATH;
import static com.example.sns_project_nfc.Util.showToast;

public class MemberInitActivity extends BasicActivity {
    private static final String TAG = "MemberInitActivity";
    private ImageView profileImageVIew;
    private RelativeLayout loaderLayout;
    private RelativeLayout buttonBackgroundLayout;
    private String profilePath;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_init);
        setToolbarTitle("회원정보");

        loaderLayout = findViewById(R.id.loaderLyaout);
        profileImageVIew = findViewById(R.id.profileImageView);
        buttonBackgroundLayout = findViewById(R.id.buttonsBackgroundLayout);

        buttonBackgroundLayout.setOnClickListener(onClickListener);
        profileImageVIew.setOnClickListener(onClickListener);

        findViewById(R.id.checkButton).setOnClickListener(onClickListener);
        findViewById(R.id.gallery).setOnClickListener(onClickListener);
    }

    @Override
    public void onBackPressed() {                                                                       // part5 : 뒤로가기 이벤트
        super.onBackPressed();
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0: {
                if (resultCode == Activity.RESULT_OK) {
                    profilePath = data.getStringExtra(INTENT_PATH);
                    Glide.with(this).load(profilePath).centerCrop().override(500).into(profileImageVIew);
                    buttonBackgroundLayout.setVisibility(View.GONE);
                }
                break;
            }
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.checkButton:
                    storageUploader();
                    break;
                case R.id.profileImageView:
                    buttonBackgroundLayout.setVisibility(View.VISIBLE);                                 // part8 : 처음에는 안보이다가 이미지그림 누르면 나타나게함 (11'30")
                    break;
                case R.id.buttonsBackgroundLayout:                                                      // part20 : 다른데 누르면 buttonBackgroundLayout 사라지게 해줌 (48')
                    buttonBackgroundLayout.setVisibility(View.GONE);
                    break;
                case R.id.gallery:                                                                      // part7 : 프로필 사진 등록시 앨범에서 고를 시
                    myStartActivity(GalleryActivity.class);
                    break;
            }
        }
    };

    private void storageUploader() {                                                                            // part5 : 회원정보 업로드 로직 (3')
        final String name = ((EditText) findViewById(R.id.nameEditText)).getText().toString();
        final String phoneNumber = ((EditText) findViewById(R.id.phoneNumberEditText)).getText().toString();
        final String birthDay = ((EditText) findViewById(R.id.birthDayEditText)).getText().toString();
        final String address = ((EditText) findViewById(R.id.addressEditText)).getText().toString();
        final String building = "-";
        final String unit = "-";
        final String authState = "ADMINISTER";

        if (name.length() > 0 && phoneNumber.length() > 9 && birthDay.length() > 5 && address.length() > 0) {
            loaderLayout.setVisibility(View.VISIBLE);
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            user = FirebaseAuth.getInstance().getCurrentUser();
            final StorageReference mountainImagesRef = storageRef.child("users/" + user.getUid() + "/profileImage.jpg");
            final Date createdID = new Date();                                                              // + : 사용자 리스트 수정 (현재 날짜 받아오기 [ 사진마다 달라서 그때 그댸 불르기])

            if (profilePath == null) {                                                                      // part5 : 데이터 추가 (9'10")
                UserInfo userInfo = new UserInfo(name, phoneNumber, birthDay, createdID, address, building, unit, authState, user.getUid());          // + : 사용자 리스트 수정 (가입날짜 추가[사진 없는 버전])
                storeUploader(userInfo);
            } else {
                try {
                    InputStream stream = new FileInputStream(new File(profilePath));                        // part7 : 입력한 회원정보를 스토리지에 저장 (25'20")
                    UploadTask uploadTask = mountainImagesRef.putStream(stream);
                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return mountainImagesRef.getDownloadUrl();                                  // part7 : mountainImagesRef.getDownloadUrl()를 아래 task.getResult();에서 받아오는 것이 아닐까?
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();                                         // part7 : 입력한 회원정보를 DB에 저장 (28')
                                Log.d("LOOG", " " + task.getResult().toString());
                                UserInfo userInfo = new UserInfo(name, phoneNumber, birthDay, address, building, unit, createdID, downloadUri.toString(), authState,user.getUid());      // + : 사용자 리스트 수정 (가입날짜 추가)
                                storeUploader(userInfo);
                            } else {
                                showToast(MemberInitActivity.this, "회원정보를 보내는데 실패하였습니다.");
                            }
                        }
                    });
                } catch (FileNotFoundException e) {
                    Log.e("로그", "에러: " + e.toString());
                }
            }
        } else {
            showToast(MemberInitActivity.this, "회원정보를 입력해주세요.");
        }
    }

    private void storeUploader(UserInfo userInfo) {                                                     // part5 : DB에 등록이 됬는지 알려주는 로직
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(user.getUid()).set(userInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showToast(MemberInitActivity.this, "회원정보 등록을 성공하였습니다.");
                        loaderLayout.setVisibility(View.GONE);
                        finish();                                                                       // part5 : 정보 입력시 창이 나가지게 된다.
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast(MemberInitActivity.this, "회원정보 등록에 실패하였습니다.");
                        loaderLayout.setVisibility(View.GONE);
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivityForResult(intent, 0);
    }
}