package com.example.sns_project_nfc.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.sns_project_nfc.R;
import com.example.sns_project_nfc.fragment.AnnounceFragment;
import com.example.sns_project_nfc.fragment.UserInfoFragment;
import com.example.sns_project_nfc.fragment.UserListFragment;
import com.example.sns_project_nfc.fragment.nfcFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends BasicActivity {                                                       // part10 : BasicActivity로 통합해버림, AppCompatActivity(6')
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    //안드로이드 생명주기중 하나
    @Override
    protected void onResume() {
        super.onResume();
    }

    //안드로이드 생명주기중 하나
    @Override
    protected void onPause(){
        super.onPause();
    }


    //안드로이드에서는 onActivityResult() 메소드를 통해 호출된 Activity에서 저장한 결과를 돌려줍니다.
    // 이 때 requestCode는 처음 startActivityForResult()의 두번째 인수 값이며,
    // resultCode는 호출된 Activity에서 설정한 성공(RESULT_OK)/실패(RESULT_CANCEL) 값입니다.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                init();
                break;
        }
    }

    //바텀네비랑 파이어베이스 문서 불러온는거
    private void init(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();                                // part5 : 로그인 시        // part22 : 운래는 옮길때 homeFragment로 옮겨졌으나 매번 불러오는것이 비효율적이라 여기로 옮김
        if (firebaseUser == null) {
            myStartActivity(SignUpActivity.class);                                                              // part5 : 로그인 정보 없으면 회원가입 화면으로
        } else {
            //myStartActivity(CameraActivity.class);                                                            // part5 : test

            DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(firebaseUser.getUid());
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            if (document.exists()) {                                                            // part5 : 로그인 되있었어도 정보가 있으면 불러오고
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            } else {                                                                            // part5 : 아니면 입력받는다.   (18')
                                Log.d(TAG, "No such document");
                                myStartActivity(MemberInitActivity.class);
                            }
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });

            AnnounceFragment announceFragment = new AnnounceFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, announceFragment)
                    .commit();
//            UserInfoFragment userInfoFragment = new UserInfoFragment();
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.container, userInfoFragment)
//                    .commit();

            BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);            // part22 : 바텀 네비게이션바  설정 (47'20")
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
//                        case R.id.nfc:
//                            nfcFragment nfcFragment = new nfcFragment();
//                            getSupportFragmentManager().beginTransaction()
//                                    .replace(R.id.container, nfcFragment)
//                                    .commit();
//                            return true;
                        case R.id.announce:
                            AnnounceFragment announceFragment = new AnnounceFragment();
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.container, announceFragment)
                                    .commit();
                            return true;
                        case R.id.myInfo:
                            UserInfoFragment userInfoFragment = new UserInfoFragment();
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.container, userInfoFragment)
                                    .commit();
                            return true;
                        case R.id.userList:
                            UserListFragment userListFragment = new UserListFragment();
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.container, userListFragment)
                                    .commit();
                            return true;
                    }
                    return false;
                }
            });
        }
    }

    //액티비티 불러오기
    private void myStartActivity(Class c) {                                                             // part22 : c에다가 이동하려는 클래스를 받고 requestcode는 둘다 1로 준다.
        Intent intent = new Intent(this, c);
        startActivityForResult(intent, 1);
    }
}