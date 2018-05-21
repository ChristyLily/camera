package com.example.christylilyfang.cameratest;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private View inflate;
    private Button choosePhoto;
    private Button takePhoto;
    private Button cancel;
    private Dialog dialog;
    private static final String IMAGE_TYPE = "image/*";
    private Uri imageUri;
    public static final int TAKE_PHOTO = 1;
    private String TEMP_IMAGE_PATH;
    public static final int CHOOSE_PHOTO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
    //对话框
    public void show(View view) {
        dialog = new Dialog(this, R.style.ActionSheetDialogStyle);
        inflate = LayoutInflater.from(this).inflate(R.layout.dialog, null);
        choosePhoto = (Button) inflate.findViewById(R.id.choosePhoto);
        takePhoto = (Button) inflate.findViewById(R.id.takePhoto);
        cancel = (Button) inflate.findViewById(R.id.btn_cancel);
        choosePhoto.setOnClickListener(this);
        takePhoto.setOnClickListener(this);
        cancel.setOnClickListener(this);
        dialog.setContentView(inflate);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.y = 20;
        dialogWindow.setAttributes(lp);
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.takePhoto:
               Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
               if(it.resolveActivity(getPackageManager())!=null){
                   File photoFile = null;
                   try{
                       photoFile = createImageFile();
                   }catch (IOException ex){
                       ex.printStackTrace();
                   }

                   if(photoFile != null){
                       Uri photoUri = FileProvider.getUriForFile(MainActivity.this,"com.example.christylilyfang.cameratest.fileprovider",photoFile);
                       it.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
                       startActivityForResult(it,TAKE_PHOTO);
                   }
               }
               /*File saveImage = new File(Environment.getExternalStorageDirectory(), "saveImage.jpg");
                try {
                    if (saveImage.exists()) {
                        saveImage.delete();
                    }
                    saveImage.createNewFile();
                    TEMP_IMAGE_PATH = saveImage.getAbsolutePath();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
               // imageUri = FileProvider.getUriForFile(MainActivity.this, "com.example.christylilyfang.cameratest.fileprovider", saveImage);
               if (Build.VERSION.SDK_INT >= 24) {
                    imageUri = FileProvider.getUriForFile(MainActivity.this, "com.example.christylilyfang.cameratest.fileprovider", saveImage);

                } else {
                    imageUri = Uri.fromFile(saveImage);
                }
               // imageUri = Uri.fromFile(saveImage);
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, TAKE_PHOTO);*/
                break;
            case R.id.choosePhoto:
                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }else{
                    openAlbum();
                }
                break;
            case R.id.btn_cancel:
                dialog.dismiss();
                break;
        }
    }
    private File createImageFile() throws IOException{
        String timeStamp = new SimpleDateFormat("yyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp +"_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName,".jpg",storageDir);
        TEMP_IMAGE_PATH = image.getAbsolutePath();
        return image;
    }

    private void openAlbum(){
        Intent intent0 = new Intent(Intent.ACTION_PICK, null);
        //设置Data和Type属性，前者是URI：表示系统图库的URI,后者是MIME码
        intent0.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_TYPE);
        //启动这个intent所指向的Activity
        startActivityForResult(intent0, CHOOSE_PHOTO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String [] permissions,int[] grantResults){
        switch (requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else{
                    Toast.makeText(this,"You denied the permission",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                   Intent it = new Intent(MainActivity.this,ResultActivity.class);
                   it.putExtra("mPicPath",TEMP_IMAGE_PATH);
                   startActivity(it);
                }
                break;

            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    if (requestCode == CHOOSE_PHOTO && data != null) {
                        //相册
                        //通过获取当前应用的contentResolver对象来查询返回的data数据
                        Cursor cursor = this.getContentResolver().query(data.getData(), null, null, null, null);
                        //将cursor指针移动到数据首行
                        cursor.moveToFirst();
                        //获取字段名为_data的数据
                        String imagePath = cursor.getString(cursor.getColumnIndex("_data"));
                        //设置一个intent
                        Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                        //传入所获取的图片的路径
                        intent.putExtra("mPicPath", imagePath);
                        //销毁cursor对象，释放资源
                        cursor.close();
                        startActivity(intent);
                    }
                    break;
                }
            default:
                break;
        }
    }
    /*    @TargetApi(19)
        private String handleImageOnKitKat(Intent data) {
            String imagePath = null;
            Uri uri = data.getData();

            if (DocumentsContract.isDocumentUri(this, uri)) {
                // 通过document id来处理
                String docId = DocumentsContract.getDocumentId(uri);
                if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                    // 解析出数字id
                    String id = docId.split(":")[1];
                    String selection = MediaStore.Images.Media._ID + "=" + id;
                    imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
                }
                else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                    Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                            Long.valueOf(docId));
                    imagePath = getImagePath(contentUri, null);
                }
            }
            else if ("content".equals(uri.getScheme())) {
                // 如果不是document类型的Uri，则使用普通方式处理
                imagePath = getImagePath(uri, null);
            } else if("file".equalsIgnoreCase(uri.getScheme())){
                imagePath = uri.getPath();
            }

            // 根据图片路径显示图片
            //displayImage(imagePath);
            return imagePath;
    }

    private String handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        //displayImage(imagePath);
        return imagePath;
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }

        return path;
    }*/

      /*  private void displayImage(String imagePath) {
            if (imagePath != null) {
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                imageView.setImageBitmap(bitmap);
            }
            else {
                Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
            }
        }*/
}


