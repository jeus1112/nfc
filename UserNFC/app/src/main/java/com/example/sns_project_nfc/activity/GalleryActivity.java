package com.example.sns_project_nfc.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sns_project_nfc.R;
import com.example.sns_project_nfc.adapter.GalleryAdapter;

import java.util.ArrayList;

import static com.example.sns_project_nfc.Util.GALLERY_IMAGE;
import static com.example.sns_project_nfc.Util.GALLERY_VIDEO;
import static com.example.sns_project_nfc.Util.INTENT_MEDIA;
import static com.example.sns_project_nfc.Util.showToast;

public class GalleryActivity extends BasicActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);                                                             // part8 : 갤러리를 불러올 때 (16'40")
        setContentView(R.layout.activity_gallery);
        setToolbarTitle("갤러리");

        if (ContextCompat.checkSelfPermission(GalleryActivity.this,                             // part8 : (A) 갤러리 권한 받기 (30')
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(GalleryActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);                                                                     // part8 : (B) 권한을 줘서 requestCode가 1이 되면
            if (ActivityCompat.shouldShowRequestPermissionRationale(GalleryActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

            } else {
                showToast(GalleryActivity.this, getResources().getString(R.string.please_grant_permission));    // part18 : strings에 메시지 관리하기
            }
        } else {
            recyclerInit();
        }
    }

    @Override
    // part8 : (C) 그 requestCode를 받아서 onRequestPermissionsResult
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {   //(33')
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    recyclerInit();                                                                     // part8 : (D) recyclerInit()가 실행되는데
                } else {
                    finish();
                    showToast(GalleryActivity.this, getResources().getString(R.string.please_grant_permission));
                }
            }
        }
    }

    private void recyclerInit(){
        final int numberOfColumns = 3;                                                                  // part9 : (E) recyclerInit()는 사진을 3개씩 나열 (2')

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

        RecyclerView.Adapter mAdapter = new GalleryAdapter(this, getImagesPath(this));
        recyclerView.setAdapter(mAdapter);
    }
                                                                                                                                 // part10 : 아이콘 이미지 생성 (9'50)
    public ArrayList<String> getImagesPath(Activity activity) {
        Uri uri;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        Cursor cursor;
        int column_index_data;
        String PathOfImage = null;
        String[] projection;

        Intent intent = getIntent();
        final int media = intent.getIntExtra(INTENT_MEDIA, GALLERY_IMAGE);                              // part11 : 비디오면 비디오 / 이미지면 이미지 리스트 반환
        if(media == GALLERY_VIDEO){
            uri = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            projection = new String[] { MediaStore.MediaColumns.DATA, MediaStore.Video.Media.BUCKET_DISPLAY_NAME };
        }else{
            uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            projection = new String[] { MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME };
        }

        cursor = activity.getContentResolver().query(uri, projection, null, null, null);
        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

        while (cursor.moveToNext()) {
            PathOfImage = cursor.getString(column_index_data);

            listOfAllImages.add(PathOfImage);
        }
        return listOfAllImages;
    }
}
