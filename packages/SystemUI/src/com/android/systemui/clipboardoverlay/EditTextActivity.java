/*
 * Copyright (C) 2021 The Android Open Source Project
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

package com.android.systemui.clipboardoverlay;

import static java.util.Objects.requireNonNull;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.android.systemui.R;

/**
 * Lightweight activity for editing text clipboard contents
 */
public class EditTextActivity extends Activity {
    private EditText mEditText;
    private ClipboardManager mClipboardManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clipboard_edit_text_activity);
        findViewById(R.id.copy_button).setOnClickListener((v) -> saveToClipboard());
        findViewById(R.id.share).setOnClickListener((v) -> share());
        mEditText = findViewById(R.id.edit_text);
        mClipboardManager = requireNonNull(getSystemService(ClipboardManager.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        ClipData clip = mClipboardManager.getPrimaryClip();
        if (clip == null) {
            finish();
            return;
        }
        // TODO: put clip attribution in R.id.attribution TextView
        mEditText.setText(clip.getItemAt(0).getText());
        mEditText.requestFocus();
    }

    private void saveToClipboard() {
        ClipData clip = ClipData.newPlainText("text", mEditText.getText());
        mClipboardManager.setPrimaryClip(clip);
        hideImeAndFinish();
    }

    private void share() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, mEditText.getText());
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    private void hideImeAndFinish() {
        InputMethodManager imm = getSystemService(InputMethodManager.class);
        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
        finish();
    }
}