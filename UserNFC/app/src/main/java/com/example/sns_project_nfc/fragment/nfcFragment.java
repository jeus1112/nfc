package com.example.sns_project_nfc.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.Locale;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.sns_project_nfc.KeyInfo;
import com.example.sns_project_nfc.R;
import com.example.sns_project_nfc.UserInfo;
import com.example.sns_project_nfc.activity.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static android.content.ContentValues.TAG;

public class nfcFragment extends Fragment {
               // 위젯관련변수
    private NfcAdapter nfcAdapter;      // NFC 통신관련 변수
    private NdefMessage mNdeMessage;
    private FirebaseUser CurrentUser;
    private FirebaseFirestore Firestore;
    private UserInfo userInfo;
    private KeyInfo keyInfo;
    public nfcFragment() {                                                                                 // part22 : 프레그먼트로 내용 이전 (21'40")
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_nfc, container, false);

        final TextView text = view.findViewById(R.id.text);

        CurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        DocRefe_USERS_CurrentUid();

        Toolbar myToolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(myToolbar);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle("NFC 인증");
        }

        /*
         * 2. NFC 단말기 정보 가져오기
         */
        nfcAdapter = NfcAdapter.getDefaultAdapter(getActivity()); // nfc를 지원하지않는 단말기에서는 null을 반환.

        if(nfcAdapter != null) { text.setText("NFC 단말기를 접촉해주세요"+nfcAdapter+""); }
        else { text.setText("NFC 기능이 꺼져있습니다. 켜주세요"+nfcAdapter+""); }

        final DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());  // users 테이블 연결 후 현재 사용자와 일치하는 데이터를 받아옴
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull final Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    final DocumentSnapshot document = task.getResult();  // document에 유저의 정보를 저장
                                    if (document != null) {
                                        if (document.exists()) {     // Not Null 이며 유저의 정보가 존재한다면
                                            final DocumentReference documentReferenceKey = FirebaseFirestore.getInstance().collection("BuildingKey").document(userInfo.getBuilding());
                                            documentReferenceKey.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task1) {     // 유저 테이블의 빌딩 값과 BuildingKey 테이블에서 UID값과 일치하는 정보를 받아옴 == 공동현관 출입문 키
                                                    if(task1.isSuccessful()) {
                                                        final DocumentSnapshot documentKey = task1.getResult();
                                                        if(documentKey != null){
                                                            if (document.getData().get("authState") != null && document.getData().get("authState").equals("O")) {   // 유저의 authState(세대인증 여부)가 완료된 상태인 'O' 일 경우
                                                                mNdeMessage = new NdefMessage(
                                                                        new NdefRecord[]{    // 유저의 정보를 받아와 Ndef 메세지로 가공 후 발송
                                                                                createNewTextRecord("이름 : " + userInfo.getName(), Locale.ENGLISH, true),   // 유저의 이름
                                                                                createNewTextRecord("아파트 : " + userInfo.getAddress(), Locale.ENGLISH, true),  // 유저의 아파트 명
                                                                                createNewTextRecord("동: " + userInfo.getBuilding(), Locale.ENGLISH, true),  // 유저의 아파트 동
                                                                                createNewTextRecord("세대인증여부 : 세대인증이 완료된 회원입니다. ", Locale.ENGLISH, true),
                                                                                createNewTextRecord("키값: " + documentKey.getData().get("Passkey"), Locale.ENGLISH, true),  // 빌딩키 테이블에서 받아온 공동현관 출입문 키
                                                                                createNewTextRecord("공동 현관 개방 성공", Locale.ENGLISH, true),
                                                                        }   // 유저의 아파트 동과 빌딩키 테이블에서 받아온 공동현관 출입문 키를 수신기에서 비교합니다.
                                                                );
                                                            } else if (document.getData().get("authState") != null && document.getData().get("authState").equals("X") || document.getData().get("authState").equals("-")) {
                                                                mNdeMessage = new NdefMessage(          // 유저의 authState(세대인증 여부)가 신청하지 않은 'X'이거나 인증이 진행중인 '-' 일 경우
                                                                        new NdefRecord[]{    // 유저의 정보를 받아와 Ndef 메세지로 가공 후 발송
                                                                                createNewTextRecord("이름 : " + userInfo.getName(), Locale.ENGLISH, true),   // 유저의 이름
                                                                                createNewTextRecord("아파트 : " + userInfo.getAddress(), Locale.ENGLISH, true),  // 유저의 아파트 명
                                                                                createNewTextRecord("세대인증여부 : 세대인증이 필요한 회원입니다. ", Locale.ENGLISH, true),
                                                                                createNewTextRecord("세대인증이 완료된 후 다시 시도하여 주십시오.", Locale.ENGLISH, true),
                                                                                createNewTextRecord("공동 현관 개방 실패", Locale.ENGLISH, true)
                                                                        }
                                                                );
                                                            }
                                                        }}
                                                }
                                            });

                        } else {
                            Log.d(TAG, "No such document");
                        }
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        return view;
    }
    /*
     * 액티비티 화면이 나오기 전에 실행되는 메소드이다.
     * onCreate 에서 정한 mNdeMessage 의 데이터를 NFC 단말기에 전송한다.
     */
    public void DocRefe_USERS_CurrentUid(){
        DocumentReference docRefe_USERS_CurrentUid = FirebaseFirestore.getInstance().collection("users").document(CurrentUser.getUid());
        docRefe_USERS_CurrentUid.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                userInfo= documentSnapshot.toObject(UserInfo.class);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            nfcAdapter.setNdefPushMessage(mNdeMessage, getActivity());

        }
    }
    /*
     * 액티비티 화면이 종료되면 NFC 데이터 전송을 중단하기 위해 실행되는 메소드이다.
     */
    @Override
    public void onPause() {
        super.onPause();
        if (nfcAdapter != null) {
            nfcAdapter.setNdefPushMessage(mNdeMessage, getActivity());
        }
    }
    /*
     * 텍스트 형식의 데이터를 mNdeMessage 변수에 넣을 수 있도록 변환해 주는 메소드이다.
     */
    public static NdefRecord createNewTextRecord(String text, Locale locale, boolean encodelnUtf8){
        byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));
        Charset utfEncoding = encodelnUtf8 ? Charset.forName("UTF-8"):Charset.forName("UTF-16");
        byte[] textBytes = text.getBytes(utfEncoding);
        int utfBit = encodelnUtf8 ? 0:(1<<7);
        char status = (char)(utfBit + langBytes.length);
        byte[] data = new byte[1 + langBytes.length + textBytes.length];
        data[0] = (byte)status;
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);
        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN,NdefRecord.RTD_TEXT, new byte[0], data);
    }
}

