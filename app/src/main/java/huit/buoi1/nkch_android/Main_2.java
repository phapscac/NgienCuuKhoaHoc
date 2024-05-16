package huit.buoi1.nkch_android;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
public class Main_2 extends AppCompatActivity {
    private static final int REQUEST_CODE_SELECT_IMAGE = 100;
    private static final int REQUEST_CODE_CAPTURE_IMAGE = 200;
    private static final int REQUEST_CAMERA_PERMISSION = 1001;
    RelativeLayout loadingPanel;
    Button butTV, butAnh,butTviet,butEng;
    TextView txthongbao;
    ImageView imgViewAnh;
    byte[] imageData;
    String audio_vi=null,audio_en;
    private MediaPlayer mediaPlayer;
    private TextToSpeechHelper ttsHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mediaPlayer = MediaPlayer.create(this, R.raw.chao);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                // Khi audio "chao" kết thúc, bắt đầu phát audio "begin"
                MediaPlayer mediaPlayerBegin = MediaPlayer.create(Main_2.this, R.raw.begin);
                mediaPlayerBegin.start();
            }
        });
        mediaPlayer.start(); // Bắt đầu phát audio "chao"

        addControl();
        even();
    }
    void addControl()
    {
        butTV = findViewById(R.id.butTV);
        butAnh=findViewById(R.id.butAnh);
        imgViewAnh = findViewById(R.id.imageViewAnh);
        butEng= findViewById(R.id.butDocEng);
        butTviet= findViewById(R.id.butDoc);
        loadingPanel = findViewById(R.id.loadingPanel);
        txthongbao = findViewById(R.id.txtTB);
    }
    void even()
    {
        butTV.setOnClickListener(v ->
        {
            openGallery();
        });
        if (ContextCompat.checkSelfPermission(Main_2.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Main_2.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            butAnh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openCamera();
                }
            });
        }
       butEng.setOnClickListener(new View.OnClickListener() {

           @Override
           public void onClick(View v) {

               if (imageData != null  && imageData.length > 0){
                   txthongbao.setBackgroundColor(getResources().getColor(android.R.color.white));
                   mediaPlayer = MediaPlayer.create(Main_2.this, R.raw.taochuthichh);
                   mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                       @Override
                       public void onCompletion(MediaPlayer mediaPlayer) {
                           // Khi audio "chao" kết thúc, bắt đầu phát audio "begin"
                           MediaPlayer mediaPlayerBegin = MediaPlayer.create(Main_2.this, R.raw.taoeng);
                           mediaPlayerBegin.start();
                       }
                   });
                   mediaPlayer.start();
                   loadingPanel.setVisibility(View.VISIBLE);
                   callAPI(imageData);
                   imageData = null;
               }else{
                   Toast.makeText(getApplicationContext(), "Bạn chưa chọn hình ảnh", Toast.LENGTH_SHORT).show();
               }
           }
       });
        butTviet.setOnClickListener(v -> {
            if (audio_vi != null) {
                Base64Decoder.playFromBase64(Main_2.this, audio_vi, new Base64Decoder.AudioPlaybackCallback() {
                    @Override
                    public void onPlaybackFinished() {
                        // Callback được gọi khi audio_vi kết thúc phát
                        Base64Decoder.playFromBase64(Main_2.this, audio_en,null);
                    }
                });
            } else {
                // Xử lý khi audio_vi là null
                // Ví dụ: Hiển thị thông báo lỗi
                Toast.makeText(Main_2.this, "Audio không tồn tại", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void openGallery() {
        txthongbao.setText("");
        txthongbao.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        mediaPlayer = MediaPlayer.create(this, R.raw.chonanh);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                // Khi audio "chao" kết thúc, bắt đầu phát audio "begin"
                MediaPlayer mediaPlayerBegin = MediaPlayer.create(Main_2.this, R.raw.selectphoto);
                mediaPlayerBegin.start();
            }
        });
        mediaPlayer.start();
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
    }
    private void openCamera() {
        txthongbao.setText("");
        txthongbao.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        mediaPlayer = MediaPlayer.create(this, R.raw.cammera);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                // Khi audio "chao" kết thúc, bắt đầu phát audio "begin"
                MediaPlayer mediaPlayerBegin = MediaPlayer.create(Main_2.this, R.raw.takephoto);
                mediaPlayerBegin.start();
            }
        });
        mediaPlayer.start();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CODE_CAPTURE_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            mediaPlayer = MediaPlayer.create(this, R.raw.dachon);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    MediaPlayer mediaPlayerBegin = MediaPlayer.create(Main_2.this, R.raw.photoselected);
                    mediaPlayerBegin.start();
                }
            });
            mediaPlayer.start();
            try {
                if (selectedImageUri != null) {

                    Bitmap originalBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                    int ngang = imgViewAnh.getWidth();
                    int cao = imgViewAnh.getHeight();
                    Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, ngang, cao, false);
                    imgViewAnh.setImageBitmap(resizedBitmap);

                    // Lưu dữ liệu hình ảnh
                    imageData = getByteArrayFromBitmap(resizedBitmap);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (requestCode == REQUEST_CODE_CAPTURE_IMAGE && data != null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.dachup);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    MediaPlayer mediaPlayerBegin = MediaPlayer.create(Main_2.this, R.raw.tookphoto);
                    mediaPlayerBegin.start();
                }
            });
            mediaPlayer.start();
            Bundle extras = data.getExtras();
            if (extras != null && extras.containsKey("data")) {
                Bitmap bitmap = (Bitmap) extras.get("data");
                if (bitmap != null) {
                    imgViewAnh.setImageBitmap(bitmap);
                    imageData = getByteArrayFromBitmap(bitmap);
                }
            }
        }
    }
    private byte[] getByteArrayFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }
    private void callAPI(byte[] imageData){
        dcoApi2 api2 = new dcoApi2();
        api2.executeApiRequest(imageData, new dcoApi2.ApiResponseListener() {
            @Override
            public void onSuccess(final String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String enText = jsonObject.getJSONObject("en").getString("text");
                            String viText = jsonObject.getJSONObject("vi").getString("text");
                            audio_en = jsonObject.getJSONObject("en").getString("file");
                            audio_vi = jsonObject.getJSONObject("vi").getString("file");

                            //txt_en.setText(enText);
                            //txt_vi.setText(viText);
                            //butTV.setText(enText);
                           // Toast.makeText(Main_2.this, viText, Toast.LENGTH_LONG).show();
                            //showDialog(Main_2.this,"Kết quả",viText+"\n"+enText);
                            txthongbao.setText(enText+"\n"+viText);
                            //if(txt_en.getVisibility() == View.INVISIBLE){
                                //txt_en.setVisibility(View.VISIBLE);
                                //txt_vi.setVisibility(View.VISIBLE);
                                //btn_play_en.setVisibility(View.VISIBLE);
                                //btn_play_vi.setVisibility(View.VISIBLE);
                            //}
                            loadingPanel.setVisibility(View.INVISIBLE);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            loadingPanel.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
            @Override
            public void onFailure(final String errorMessage) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //txt_en.setText(errorMessage);
                        //Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        //loadingPanel.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });

    }
    public static void showDialog(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
}
