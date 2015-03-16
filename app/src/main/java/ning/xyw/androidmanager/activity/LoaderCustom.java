/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ning.xyw.androidmanager.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.widget.Toast;

import ning.xyw.androidmanager.fragment.AppListFragment;

/** Demonstration of the implementation of a custom Loader. */
public class LoaderCustom extends Activity {
    private boolean exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentManager fm = getFragmentManager();
        // Create the list fragment and add it as our sole content.
        if (fm.findFragmentById(android.R.id.content) == null) {
            AppListFragment list = new AppListFragment();
            fm.beginTransaction().add(android.R.id.content, list).commit();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                // doubleClickBack2Exit();
                moveTaskToBack(true);
                break;
            case KeyEvent.KEYCODE_MENU:
                break;
        }
        return true;
    }

    private void doubleClickBack2Exit() {
        new Handler().postDelayed(new Runnable() {// 连按两次返回键退出
                    @Override
                    public void run() {
                        exit = false;
                    }
                }, 2000);
        if (exit) {
            finish();
        } else {
            exit = true;
            Toast.makeText(LoaderCustom.this,
                    "再按一次退出",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 彻底退出应用
     */
    private void exitApp() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }

}
