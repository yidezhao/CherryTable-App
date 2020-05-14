package edu.upenn.cis350.cherrytable.ui.account;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import edu.upenn.cis350.cherrytable.R;

import static com.google.android.apps.gmm.map.util.jni.NativeHelper.context;

public class AccountActivity extends AppCompatActivity {

    private TextView usernameText;
    private ImageView profileImageView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        usernameText = findViewById(R.id.usernameText);
        profileImageView = findViewById(R.id.profileImageView);
    }
}
