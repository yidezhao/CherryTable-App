package edu.upenn.cis350.cherrytable.ui.account;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import edu.upenn.cis350.cherrytable.R;
import edu.upenn.cis350.cherrytable.data.AccountDataSource;
import edu.upenn.cis350.cherrytable.data.LoginRepository;
import edu.upenn.cis350.cherrytable.data.model.UserAccount;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;

public class AccountFragment extends Fragment implements View.OnClickListener {

    private AccountViewModel accountViewModel;
    private TextView usernameText;
    private TextView emailText;
    private TextView phoneText;

    private ImageView profileImageView;
    private Context context;

    private String username;


    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private List<String> selectedCheckBoxes;
    private List<String> uncheckedCheckBoxes;

    // Stores all info about the user for the duration of this; initialized in onCreateView
    private UserAccount userAccount;

    private static final int CHECKBOX_ID = 160;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        username = "Matthew";

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);

        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        View root = inflater.inflate(R.layout.activity_account, container, false);

        context = getActivity();

        selectedCheckBoxes = new ArrayList<>();
        uncheckedCheckBoxes = new ArrayList<>();

        LoginRepository lr = LoginRepository.getInstance();

        // make new account object given user ID and username from loginrepository
        AccountDataSource ads = new AccountDataSource();

        // Set listeners
        Button newPhotoButton = (Button) root.findViewById(R.id.newPhotoButton);
        newPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickNewPhotoButton(v);
            }
        });

        Button updateButton = (Button) root.findViewById(R.id.updateButton);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    clickUpdateButton(v);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Button deleteButton = (Button) root.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickDeleteButton(v);
            }
        });

        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        usernameText = getView().findViewById(R.id.usernameText);
        emailText = getView().findViewById(R.id.emailText);
        phoneText = getView().findViewById(R.id.phoneText);

        profileImageView = getView().findViewById(R.id.profileImageView);
//        usernameText.setText(userAccount.getDisplayName());

        String username = "Matthew";

        LinearLayout parentLayout = (LinearLayout) getView().findViewById(R.id.check_add_layout);

        try {
//          populateCheckboxes is called inside of getCurrentProfile
            getCurrentProfile(username, parentLayout);
        } catch (Exception e) {

            Toast.makeText(getContext(), "Could not load profile!", Toast.LENGTH_LONG).show();
        }
    }

    public void onClick(View v) { // check for button clicks
        switch (v.getId()) {
            case R.id.newPhotoButton:
                clickNewPhotoButton(v);
            case R.id.updateButton:
                try {
                    clickUpdateButton(v);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            case R.id.deleteButton:
                clickDeleteButton(v);
            case CHECKBOX_ID:
                clickCheckbox(v);
        }
    }

    public void clickNewPhotoButton(View v) {
        if (!hasImagePermissions()) {
            requestImagePermissions();
        } else {
            selectImage(context);
        }
    }

    public void clickUpdateButton(View v) throws Exception {
        String newEmail = emailText.getText().toString();
        String newPhone = phoneText.getText().toString();

        try {
            // write to database with newEmail and newPhone
            updateProfile(username, newEmail, newPhone, selectedCheckBoxes);
        } catch (Exception e) {
            e.printStackTrace();
        }


        Toast toast = Toast.makeText(getContext(), "Updated!", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void clickDeleteButton(View v) {

        try {
            deleteProfile(username);
        } catch (Exception e) {
            e.printStackTrace();
        }

        LoginRepository lr = LoginRepository.getInstance();
        lr.logout();
    }

    public void clickCheckbox(View v) {
        // see: populateCheckboxes
    }

    private void selectImage(Context context) {
        final CharSequence[] options = { "Take Photo", "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose your profile picture");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);

                    // Choose from gallery is now deprecated
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , 1);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK && data != null) {
                    Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                    profileImageView.setImageBitmap(selectedImage);
                    // upload image to server

                    try {
                        updateProfileImage(username, selectedImage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                break;
            case 1:
                if (resultCode == RESULT_OK && data != null) {
                    Uri selectedImage =  data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    if (selectedImage != null) {
                        Cursor cursor = context.getContentResolver().query(selectedImage,
                                filePathColumn, null, null, null);
                        if (cursor != null) {
                            cursor.moveToFirst();

                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            String picturePath = cursor.getString(columnIndex);
                            profileImageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                            cursor.close();
                        }
                    }

                }
                break;
        }
    }

    private boolean hasImagePermissions() {
        System.out.println(ContextCompat.checkSelfPermission(getContext(), READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED);
        return ContextCompat.checkSelfPermission(getContext(), READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestImagePermissions() {
        ActivityCompat.requestPermissions(
                getActivity(),
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectImage(context);
                }
            }
        }
    }


    private void getCurrentProfile(String username, LinearLayout parentLayout) throws IOException, ParseException, JSONException {

        URL url = new URL("http://10.0.2.2:8080/getCurrentProfile?name=" + username);
        Log.d("LINE1", url.toString());
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setRequestMethod("GET");

        InputStream inputStream = httpConn.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line1 = bufferedReader.readLine();

        JSONObject user = new JSONObject(line1).getJSONObject("user");
        String name = user.getString("name");
        String email = user.getString("email");
        String phone = user.getString("contact");
        String profilePicUrl;

        try {
            profilePicUrl = user.getString("profilePic");
        } catch (Exception e) {
            // default if user has not uploaded a picture yet
            profilePicUrl = "https://upload.wikimedia.org/wikipedia/commons/5/53/Google_%22G%22_Logo.svg";
        }

        URL url_value = new URL(profilePicUrl);
        if (profileImageView != null) {
            Bitmap pic =
                    BitmapFactory.decodeStream(url_value.openConnection().getInputStream());
            profileImageView.setImageBitmap(pic);
        }

        JSONArray interests = user.getJSONArray("interests");

        ArrayList<String> interestData = new ArrayList<String>();
        if (interests != null) {
            for (int i=0;i<interests.length();i++){
                interestData.add(interests.getString(i));
            }
        }

        Log.d("LINE1", line1);
        Log.d("NAME", name);
        Log.d("EMAIL", email);

        usernameText.setText(name);
        emailText.setText(email);
        phoneText.setText(phone);

        populateCheckboxes(parentLayout, interestData);
    }

    public void populateCheckboxes(LinearLayout parentLayout, ArrayList<String> interests) {
        for (String interest : interests) {
            CheckBox checkBox = new CheckBox(getContext());

            checkBox.setId(CHECKBOX_ID);
            checkBox.setText(interest);

            checkBox.setChecked(true);
            selectedCheckBoxes.add(checkBox.getText().toString());

            LinearLayout.LayoutParams checkParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            checkParams.setMargins(15, 10, 10, 10);

            checkBox.setOnClickListener(this);
            parentLayout.addView(checkBox, checkParams);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        selectedCheckBoxes.add(checkBox.getText().toString());
                        uncheckedCheckBoxes.remove(checkBox.getText().toString());
                    } else {
                        selectedCheckBoxes.remove(checkBox.getText().toString());
                        uncheckedCheckBoxes.add(checkBox.getText().toString());
                    }

                }
            });
        }

        List<String> totalInterests = new ArrayList();
        totalInterests.add("Hunger");
        totalInterests.add("Disaster Relief");
        totalInterests.add("Poverty");
        totalInterests.add("Disease");

        for (String totalInterest : totalInterests) {
            if (!interests.contains(totalInterest)) {
                CheckBox checkBox = new CheckBox(getContext());
                checkBox.setId(CHECKBOX_ID);
                checkBox.setText(totalInterest);
                checkBox.setChecked(false);

                LinearLayout.LayoutParams checkParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                checkParams.setMargins(15, 10, 10, 10);

                checkBox.setOnClickListener(this);
                parentLayout.addView(checkBox, checkParams);

                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            selectedCheckBoxes.add(checkBox.getText().toString());
                        } else {
                            selectedCheckBoxes.remove(checkBox.getText().toString());
                        }

                    }
                });
            }
        }
    }


    private void updateProfile(String username, String email, String phone, List<String> interests) throws IOException, JSONException {

        URL url = new URL("http://10.0.2.2:8080/updateUserProfile?name=" + username + "&email="
        + email + "&phone=" + phone + "&interests=" + concatInterests(interests));
        Log.d("LINE1", url.toString());
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setRequestMethod("POST");

        InputStream inputStream = httpConn.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line1 = bufferedReader.readLine();

        Log.d("LINE1", line1);
    }

    public String concatInterests(List<String> interests) {
        String base = "";
        for (String interest : interests) {
            base += interest;
            base += ",";
        }

        // remove last comma
        return base.substring(0, base.length() - 1);
    }

    private void updateProfileImage(String username, Bitmap imageBitmap) throws IOException, JSONException {

        String url = "http://10.0.2.2:8080/uploadImageMobileUser?name=" + username;
        Log.d("LINE1", url.toString());

        byte[] data = null;
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
            MultipartEntity entity = new MultipartEntity();

            if (imageBitmap != null) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                data = bos.toByteArray();
                entity.addPart("pic", new ByteArrayBody(data,"image/jpeg", "pic.jpg"));
            }

            entity.addPart("name", new StringBody(username));

            httppost.setEntity(entity);
            HttpResponse resp = httpclient.execute(httppost);
            HttpEntity resEntity = resp.getEntity();

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteProfile(String username) throws IOException, JSONException {

        URL url = new URL("http://10.0.2.2:8080/deleteProfile?name=" + username);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setRequestMethod("POST");

        InputStream inputStream = httpConn.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line1 = bufferedReader.readLine();

        Log.d("LINE1", line1);
    }
}
