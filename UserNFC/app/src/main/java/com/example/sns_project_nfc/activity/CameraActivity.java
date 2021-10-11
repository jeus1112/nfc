/*
 * Copyright 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.sns_project_nfc.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;

import com.example.sns_project_nfc.R;
import com.example.sns_project_nfc.fragment.Camera2BasicFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import static com.example.sns_project_nfc.Util.INTENT_PATH;

public class CameraActivity extends BasicActivity {
    private Camera2BasicFragment camera2BasicFragment;

    /**
     * This a callback object for the {@link ImageReader}. "onImageAvailable" will be called when a
     * still image is ready to be saved.
     */
    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener                        //ImageReader로부터 사용가능한 새로운 이미지가 있을 때 호출
            = new ImageReader.OnImageAvailableListener() {

        @Override
        public void onImageAvailable(ImageReader reader) {
            Image mImage = reader.acquireNextImage();                                                   // part7 : Camera2BasicFragment에서 한 Imagesaver와 역할은 같음 (17')
            File mFile = new File(getExternalFilesDir(null), "profileImage.jpg");           // 경로 지정은 MemberInitActivity 에서 해줬음 (119줄)

            ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            FileOutputStream output = null;
            try {
                output = new FileOutputStream(mFile);
                output.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                mImage.close();
                if (null != output) {
                    try {
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            Intent intent = new Intent();                                                               // part7 : 카메라로 찍고 나면 ResultCode를 RESULT_OK로 지정한다.
            intent.putExtra(INTENT_PATH, mFile.toString());                                             // 지정하면 MemberInitActivity 프로필 이미지 띄우는 부분(71줄)에
            setResult(Activity.RESULT_OK, intent);                                                      // 촬영한 사진을 띄운다. (18')

            camera2BasicFragment.closeCamera();                                                         // part7 : 찍은사진이 정보입력 화면에 띄워질수 있게 종료해준다. (21')
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        setToolbarTitle(getResources().getString(R.string.app_name));
        if (null == savedInstanceState) {
            camera2BasicFragment = new Camera2BasicFragment();
            camera2BasicFragment.setOnImageAvailableListener(mOnImageAvailableListener);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, camera2BasicFragment)
                    .commit();
        }
    }

}