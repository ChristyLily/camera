package com.example.christylilyfang.cameratest;

import android.Manifest;
import android.app.ActionBar;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

public class ResultActivity extends AppCompatActivity {
    private ImageView imageView0;
    private ImageView imageView1;
    private ImageView imageView2;
    private static final String IMAGE_TYPE = "image/*";
    //public static final int requestCode = 11;
    public static final int chooseCode = 12;
    public int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
      //  getActionBar().setDisplayHomeAsUpEnabled(true);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.hide();
        }
        imageView0 = (ImageView)findViewById(R.id.picture0);
        imageView1 = (ImageView)findViewById(R.id.picture1);
        imageView2 = (ImageView)findViewById(R.id.picture2);
        Intent intent=getIntent();
        String picPath=intent.getStringExtra("mPicPath");
        Bitmap bitmap= BitmapFactory.decodeFile(picPath);
        imageView0.setImageBitmap(bitmap);

        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePic();
                id = v.getId();
            }
        });

        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePic();
                id = v.getId();
            }
        });
    }

    public void choosePic(){
        if(ContextCompat.checkSelfPermission(ResultActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(ResultActivity.this,new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }else{
            openAlbum();
        }
    }

    private void openAlbum(){
        Intent intent0 = new Intent(Intent.ACTION_PICK, null);
        //设置Data和Type属性，前者是URI：表示系统图库的URI,后者是MIME码
        intent0.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_TYPE);
        //启动这个intent所指向的Activity
        startActivityForResult(intent0, chooseCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case chooseCode:
                if (resultCode == RESULT_OK) {
                    if (requestCode == chooseCode && data != null) {
                        //相册
                        //通过获取当前应用的contentResolver对象来查询返回的data数据
                        Cursor cursor = this.getContentResolver().query(data.getData(), null, null, null, null);
                        //将cursor指针移动到数据首行
                        cursor.moveToFirst();
                        //获取字段名为_data的数据
                        String imagePath = cursor.getString(cursor.getColumnIndex("_data"));
                        displayImage(imagePath);
                        cursor.close();
                    }
                    break;
                }
            default:
                break;
        }
    }

    private void displayImage(String imagePath){
        if(imagePath!=null && id == R.id.picture1){
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            imageView1.setImageBitmap(bitmap);
        }
        else if(imagePath!=null && id == R.id.picture2){
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            imageView2.setImageBitmap(bitmap);
        }
        else
            Toast.makeText(ResultActivity.this,"failed to get image",Toast.LENGTH_LONG).show();
    }

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.actionbar,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.finish:
                Toast.makeText(ResultActivity.this,"You clicked Finish",Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }*/
}
