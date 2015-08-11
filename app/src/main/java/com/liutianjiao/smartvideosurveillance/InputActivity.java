package com.liutianjiao.smartvideosurveillance;

import android.app.Activity;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.MultipartRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.liutianjiao.smartvideosurveillance.base.Config;
import com.liutianjiao.smartvideosurveillance.data.SingleRequestQueue;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class InputActivity extends Activity {
    private RequestQueue rQueue;
    private Context context;
    private final int IMAGE_CODE = 0, SCAN_CODE = 1, CAMERA_CODE = 2;
    private final int MIN_SIZE = 150;
    private Button addPicture, openCamera, uploadComplete, back;
    private List<String> pathList = new ArrayList<String>();
    private ImageView image1, image2, image3;
    private ProgressBar uploadProgress;
    private EditText contentText;
    private FunctionButtonListener buttonListener;
    final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    private Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
        context = getBaseContext();
        buttonListener = new FunctionButtonListener();
        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(buttonListener);
        addPicture = (Button) findViewById(R.id.picture);
        addPicture.setOnClickListener(buttonListener);
        openCamera = (Button) findViewById(R.id.camera);
        openCamera.setOnClickListener(buttonListener);
        uploadComplete = (Button) findViewById(R.id.complete);
        uploadComplete.setOnClickListener(buttonListener);
        image1 = (ImageView) findViewById(R.id.temp_pic1);
        image1.setOnClickListener(buttonListener);
        image2 = (ImageView) findViewById(R.id.temp_pic2);
        image2.setOnClickListener(buttonListener);
        image3 = (ImageView) findViewById(R.id.temp_pic3);
        contentText = (EditText) findViewById(R.id.content);
        image3.setOnClickListener(buttonListener);
        uploadProgress = (ProgressBar) findViewById(R.id.upload_progress);
        rQueue = SingleRequestQueue.getRequestQueue(context);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_CODE || requestCode == CAMERA_CODE) {
            String path = null;
            try {
                Uri uri = data.getData(); // 获得图片的url
                if (isKitKat) {
                    String wholeID = DocumentsContract.getDocumentId(uri);
                    String id = wholeID.split(":")[1];
                    String[] column = {MediaStore.Images.Media.DATA};
                    String sel = MediaStore.Images.Media._ID + " =?";
                    Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel, new String[]{id}, null);
                    int columnIndex = cursor.getColumnIndex(column[0]);
                    if (cursor.moveToFirst())
                        path = cursor.getString(columnIndex);
                    cursor.close();
                } else {
                    String[] pojo = {MediaStore.Images.Media.DATA};
                    CursorLoader cursorLoader = new CursorLoader(this, uri, pojo,
                            null, null, null);
                    Cursor cursor = cursorLoader.loadInBackground();
                    cursor.moveToFirst();
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    path = cursor.getString(column_index);
                    cursor.close();
                    //path = cursor.getString(cursor.getColumnIndex(pojo[0]));
                }
                if (pathList.indexOf(path) == -1 && path != null) {
                    Bitmap tempBmp = getPreview(path);
                    if (tempBmp != null) {
                        if (pathList.size() == 0) {
                            image1.setImageBitmap(tempBmp);
                            image1.setVisibility(View.VISIBLE);
                            image2.setImageResource(R.drawable.pic_add_selector);
                            image2.setVisibility(View.VISIBLE);
                        } else if (pathList.size() == 1) {
                            image2.setImageBitmap(tempBmp);
                            image2.setVisibility(View.VISIBLE);
                            image3.setImageResource(R.drawable.pic_add_selector);
                            image3.setVisibility(View.VISIBLE);
                        } else
                            image3.setImageBitmap(tempBmp);
                        pathList.add(path);
                    }
                }
                /**/
            } catch (Exception e) {
                Log.e("TAG-->Error", e.toString());
            }
        } else if (requestCode == SCAN_CODE) {
            //if (resultCode != RESULT_OK) {
            pathList = data.getStringArrayListExtra("pictureList");
            for (int i = 0; i < pathList.size(); i++) {
                Bitmap tempBmp = getPreview(pathList.get(i));
                switch (i) {
                    case 0: {
                        image1.setImageBitmap(tempBmp);
                        image1.setVisibility(View.VISIBLE);
                        break;
                    }
                    case 1: {
                        image2.setImageBitmap(tempBmp);
                        image2.setVisibility(View.VISIBLE);
                        break;
                    }
                    case 2: {
                        image3.setImageBitmap(tempBmp);
                        image3.setVisibility(View.VISIBLE);
                    }
                }
            }
            switch (pathList.size()) {
                case 0: {
                    image1.setVisibility(View.GONE);
                    image2.setVisibility(View.GONE);
                    image3.setVisibility(View.GONE);
                    break;
                }
                case 1: {
                    image2.setImageResource(R.drawable.pic_add_selector);
                    image2.setVisibility(View.VISIBLE);
                    image3.setVisibility(View.GONE);
                    break;
                }
                case 2:
                    image3.setImageResource(R.drawable.pic_add_selector);
            }
        }
    }

    private Bitmap getPreview(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        //options.inJustDecodeBounds = false;
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(path);

        //if (options.outWidth >= MIN_SIZE && options.outHeight >= MIN_SIZE) {
        //options.inJustDecodeBounds = false;
        Bitmap newbmp = ThumbnailUtils.extractThumbnail(bmp, MIN_SIZE, MIN_SIZE);
        return newbmp;
    }

    private void Upload() {
        MultipartRequest request = new MultipartRequest(Config.WEB_ADDRESS + "unusual.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        uploadProgress.setVisibility(View.GONE);
                        uploadComplete.setText("完成");
                        uploadComplete.setClickable(true);
                        Intent intent = new Intent(InputActivity.this,
                                MainActivity.class);
                        intent.putExtra("userName", Config.USER_NAME);
                        startActivity(intent);
                        //finish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                uploadProgress.setVisibility(View.GONE);
                uploadComplete.setText("完成");
                uploadComplete.setClickable(true);
                Config.NETWORK_STATUS = Config.NETWORK_ERROR;
                Toast.makeText(getBaseContext(), "网络错误，上传失败。", Toast.LENGTH_SHORT).show();
            }
        }, getHttpEntity()
        ) {
        };
        uploadProgress.setVisibility(View.VISIBLE);
        uploadComplete.setText("上传中");
        uploadComplete.setClickable(false);
        rQueue.add(request);
    }

    private HttpEntity getHttpEntity() {
        ContentType contentType = ContentType.create(HTTP.PLAIN_TEXT_TYPE,
                HTTP.UTF_8);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addTextBody("picnum", String.valueOf(pathList.size()),
                contentType);
        builder.addTextBody("user_name", "admin", contentType);
        //builder.addTextBody("user_name", Config.USER_NAME, contentType);
        String content = contentText.getText().toString();
        if (!content.isEmpty())
            builder.addTextBody("content", content, contentType);
        for (int i = 0; i < pathList.size(); i++) {
            File file = new File(pathList.get(i));
            FileBody fileBody = new FileBody(file);
            builder.addPart("picture" + String.valueOf(i), fileBody);
        }
        return builder.build();
    }

    class FunctionButtonListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            Intent getAlbum = new Intent(Intent.ACTION_GET_CONTENT);
            getAlbum.setType("image/*");
            Intent getCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Intent scanPicture = new Intent(InputActivity.this,
                    PicScanActivity.class);

            scanPicture.putStringArrayListExtra("pictureList",
                    (ArrayList<String>) pathList);
            switch (v.getId()) {
                case R.id.picture: {
                    if (pathList.size() < 3)
                        startActivityForResult(getAlbum, IMAGE_CODE);
                    break;
                }
                case R.id.camera: {
                    if (pathList.size() < 3)
                        startActivityForResult(getCamera, CAMERA_CODE);
                    break;
                }
                case R.id.complete: {
                    Upload();
                    break;
                }
                case R.id.temp_pic1: {
                    scanPicture.putExtra("curPosition", 0);
                    startActivityForResult(scanPicture, SCAN_CODE);
                    break;
                }
                case R.id.temp_pic2: {
                    if (pathList.size() < 2)
                        startActivityForResult(getAlbum, IMAGE_CODE);
                    else {
                        scanPicture.putExtra("curPosition", 1);
                        startActivityForResult(scanPicture, SCAN_CODE);
                    }
                    break;
                }
                case R.id.temp_pic3: {
                    if (pathList.size() < 3)
                        startActivityForResult(getAlbum, IMAGE_CODE);
                    else {
                        scanPicture.putExtra("curPosition", 2);
                        startActivityForResult(scanPicture, SCAN_CODE);
                    }
                    break;
                }
                case R.id.back:
                    Intent intent = new Intent(InputActivity.this,
                            MainActivity.class);
                    startActivity(intent);
                    finish();
            }
        }
    }
}
