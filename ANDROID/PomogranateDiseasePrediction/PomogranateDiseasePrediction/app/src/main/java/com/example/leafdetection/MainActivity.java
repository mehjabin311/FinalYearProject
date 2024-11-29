package com.example.leafdetection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private boolean connected;
    private static final int INPUT_SIZE = 224;
    private static final int IMAGE_MEAN = 117;
    private static final float IMAGE_STD = 1;
    private static final String INPUT_NAME = "input";
    //private static final String OUTPUT_NAME = "output";
    private static final String OUTPUT_NAME = "final_result";
    public static String url = "";
    private Button mButtonSelectPhoto;
    private Button mButtonTakePhoto;
    private Button mButtonDiagnose;
    private ImageView mImageView;
    private TextView mTextView;
    private File temFile;
    private Bitmap mDiagnosisBitmap;

    private static final int PHOTO_REQUEST_CAMERA = 1;
    private static final int PHOTO_REQUEST_GALLERY = 2;
    private static final int PHOTO_REQUEST_CUT = 3;
    private static final int PHOTO_REQUEST_CODE = 4;
    private static final int PERMISSION_REQUEST_CODE = 5;
    private static final String PHOTO_FILE_NAME = "temp_photo.jpg";

    public static final int REQUEST_IMAGE = 100;
    public static final int REQUEST_PERMISSION = 200;
    private String imageFilePath = "";

    Spinner categories_spinner;
    EditText etDescription, etTitle;
    Button btnAddPost;
    double lat = 0.0;
    double lon = 0.0;
    private double latitude;
    private double longitude;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 2000 * 60 * 1;
    boolean mLocationPermissionGranted = false;
    protected LocationManager locationManager;
    Location location;
    boolean cangetLocation = false;
    ProgressDialog dialog;
    public static String SERVER_URL = "http://192.168.0.114:5000/analysis";
    private static final int MY_CAMERA_REQUEST_CODE = 100;

    Translate translate;
    String str_marathi[] = {"  "
    };

    String str_english[] = {

            "Detected disease : Bacterial Blight. \n Remedy:- Pruning infected plant material is the first step in controlling the disease. Remove affected parts of the plant and toss them. Do not add them to the compost pile!",
            "Detected disease : Fruit Spot \n Remedy:- Get rid of diseased fruits/leaves/twigs. Prune infected branches off the plant. Spray the plants with appropriate fungicides with active ingredients such as thiophanate, carbendazim, or propiconazole..",
            "Detected disease : Anthracnose \n  Spray Hexaconazol @ 1 ml/litre or Thiophanate methyl @ 1g/litre or Carbendazim @ 1g/litre at 15 days interval. Destroy diseased parts of the plant.",

            "Detected disease : Fruit_Borer. \n Remedy:- Spray one of the following chemicals: dimethoate (2 ml/lit), indoxacarb (1 g/lit), cypermethrin (1.5 ml/lit) or profenophos (2 ml/lit) at a fortnightly interval from flowering to fruit development.Chemical applications of lambda-cyhalothrin are also recommended for effective control of pomegranate fruit borer.",
            "No disease detected, Fruit is healthy."
    };
    String str_eenglish[] = {
            "Detected disease : Anthracnose \n  Spray Hexaconazol @ 1 ml/litre or Thiophanate methyl @ 1g/litre or Carbendazim @ 1g/litre at 15 days interval. Destroy diseased parts of the plant.",
            "Detected disease : Bacterial Blight. \n Remedy:- Pruning infected plant material is the first step in controlling the disease. Remove affected parts of the plant and toss them. Do not add them to the compost pile!",
            "Detected disease : Fruit_Borer. \n Remedy:- Spray one of the following chemicals: dimethoate (2 ml/lit), indoxacarb (1 g/lit), cypermethrin (1.5 ml/lit) or profenophos (2 ml/lit) at a fortnightly interval from flowering to fruit development.Chemical applications of lambda-cyhalothrin are also recommended for effective control of pomegranate fruit borer.",

            "Detected disease : Fruit Spot \n Remedy:- Get rid of diseased fruits/leaves/twigs. Prune infected branches off the plant. Spray the plants with appropriate fungicides with active ingredients such as thiophanate, carbendazim, or propiconazole..",
            "No disease detected, Fruit is healthy."
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButtonSelectPhoto = (Button) findViewById(R.id.buttonSelectPhoto);
        mButtonTakePhoto = (Button) findViewById(R.id.buttonTakePhotoPhoto);
        mImageView = (ImageView) findViewById(R.id.image);
        btnAddPost = (Button) findViewById(R.id.btnAddPost);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);
        }
        btnAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Bitmap bitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
                String str_bitmap = BitMapToString(bitmap);
                if (str_bitmap.length() > 0) {

                    getDisease();
                } else {
                    Toast.makeText(MainActivity.this, "Please select image", Toast.LENGTH_SHORT).show();
                }


            }
        });
        mButtonTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
                } else {
                    openCameraIntent();
                }
            }
        });

        mButtonSelectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gallery(view);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void openCameraIntent() {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (pictureIntent.resolveActivity(getPackageManager()) != null) {

            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            Uri photoUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", photoFile);
            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(pictureIntent, REQUEST_IMAGE);
        }
    }

    private File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        imageFilePath = image.getAbsolutePath();

        return image;
    }


    private boolean hasSdcard() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    public Bitmap scaleImage(Bitmap bitmap) {
        int orignalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        float scaleWidth = ((float) INPUT_SIZE) / orignalWidth;
        float scaleHeight = ((float) INPUT_SIZE) / originalHeight;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, orignalWidth, originalHeight, matrix, true);
        return scaledBitmap;
    }


    private void gallery(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Thanks for granting Permission", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCameraIntent();
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHOTO_REQUEST_GALLERY) {
            if (data != null) {
                Uri uri = data.getData();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("Success!!!");
                mDiagnosisBitmap = scaleImage(bitmap);
                mImageView.setImageBitmap(mDiagnosisBitmap);
//                new FindDepartment().execute();
            }
        }
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {

                mImageView.setImageURI(Uri.parse(imageFilePath));
                BitmapDrawable drawable = (BitmapDrawable) mImageView.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                mDiagnosisBitmap = scaleImage(bitmap);
                mImageView.setImageBitmap(mDiagnosisBitmap);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "You cancelled the operation", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == PHOTO_REQUEST_CUT) {
            if (data != null) {
                Bitmap bitmap = data.getParcelableExtra("data");
                mImageView.setImageBitmap(bitmap);
            }
            try {
                temFile.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }


    public static String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] arr = baos.toByteArray();
        return Base64.encodeToString(arr, Base64.DEFAULT);
    }


    public void getDisease() {


        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setIndeterminate(false);
        dialog.setMessage("Please wait...");
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, SERVER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (dialog.isShowing())
                            dialog.dismiss();

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int str = jsonObject.getInt("value");
                            loadLocale(str);
                        } catch (JSONException e) {
                            Toast.makeText(MainActivity.this, "Error...", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                Toast.makeText(MainActivity.this, "Error..", Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                Bitmap bitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
                String str_bitmap = BitMapToString(bitmap);
                params.put("imageData", str_bitmap);
                params.put("selection","0");
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    //load language saved in shred prefer
    public void loadLocale(int index) {
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String lang = prefs.getString("My_Lang", "");
        if (lang.equals("en")) {
            AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                    .setMessage(str_english[index])
                    .setTitle("Alert")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create();

            dialog.show();
        } else {
            AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                    .setMessage(str_marathi[index])
                    .setTitle("Alert")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create();

            dialog.show();
        }

    }

    public void getTranslateService() {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try (InputStream is = getResources().openRawResource(R.raw.credentials)) {

            //Get credentials:
            final GoogleCredentials myCredentials = GoogleCredentials.fromStream(is);

            //Set credentials and get translate service:
            TranslateOptions translateOptions = TranslateOptions.newBuilder().setCredentials(myCredentials).build();
            translate = translateOptions.getService();

        } catch (IOException ioe) {
            ioe.printStackTrace();

        }
    }

    public void translate(String originalText) {

        //Get input text to be translated:

        Translation translation = translate.translate(originalText, Translate.TranslateOption.targetLanguage("mr"), Translate.TranslateOption.model("base"));
        String translatedText = translation.getTranslatedText();

        //Translated text and original text are set to TextViews:
        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setMessage(originalText + "\n" + translatedText)
                .setTitle("Alert")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();

        dialog.show();


    }

    public boolean checkInternetConnection() {

        //Check internet connection:
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        //Means that we are connected to a network (mobile or wi-fi)
        connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;

        return connected;
    }

}
