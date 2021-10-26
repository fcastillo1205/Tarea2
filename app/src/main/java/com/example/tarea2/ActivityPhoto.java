package com.example.tarea2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.tarea2.Configuracion.SQLiteConexion;
import com.example.tarea2.Configuracion.transacciones;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ActivityPhoto extends AppCompatActivity {


    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int PETICION_ACCESO_CAN = 101;

    ImageView ObjetoImagen;
    EditText txtDescripcion;
    Button btntakephoto, btnguardar;
    String currentPhotoPath;
    Uri fotoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        ObjetoImagen = (ImageView)  findViewById(R.id.img);
        btntakephoto = (Button)  findViewById(R.id.btnFotoTakePhoto);
        btnguardar = (Button)  findViewById(R.id.btnFotoGuardar);
        txtDescripcion = (EditText) findViewById(R.id.txtDescripcion);

        btntakephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permisos();
            }
        });

        btnguardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bitmap;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), fotoUri);
                    guardarImagen(bitmap,txtDescripcion.getText().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void permisos(){
        //VALIDAR PERMISO QUE ESTA OTORGADO
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            //otorga el permiso si no lo tengo
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PETICION_ACCESO_CAN);
        }else{
            tomarFoto();
        }
    }

    @Override
    public void onRequestPermissionsResult(int RequestCode, String[] permissions, int[] grantResults){
        super.onRequestPermissionsResult(RequestCode, permissions, grantResults);

        if (RequestCode == PETICION_ACCESO_CAN){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // tomarPhoto();
                tomarFoto();
            }
        }else {
            Toast.makeText(getApplicationContext(), "Se necesita el permiso de camara", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE  && resultCode == RESULT_OK) {

            Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), fotoUri);
                ObjetoImagen.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */);
        // Save a file: path for use with ACTION_VIEW
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
    private void tomarFoto(){
        Intent Intenttakephoto= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Intenttakephoto.resolveActivity(getPackageManager()) != null){
            File foto = null;
            try {
                foto = createImageFile();
            }
            catch (Exception ex){
                ex.toString();
            }
            if (foto!= null){
                fotoUri = FileProvider.getUriForFile(this, "com.example.pm01sqlite.fileprovider",foto);
                Intenttakephoto.putExtra(MediaStore.EXTRA_OUTPUT, fotoUri);
                startActivityForResult(Intenttakephoto, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void guardarImagen(Bitmap bitmap, String decripcion) {
        SQLiteConexion conexion = new SQLiteConexion(this, transacciones.NameDatabase, null, 1);
        SQLiteDatabase db = conexion.getWritableDatabase();

        ByteArrayOutputStream baos = new ByteArrayOutputStream(20480);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100 , baos);
        byte[] blob = baos.toByteArray();
        byte[] imgInset = imagemTratada(blob);

        String sql = "INSERT INTO photograh (imagen,descripcion) VALUES(?,?)";
        SQLiteStatement insert = db.compileStatement(sql);
        insert.clearBindings();
        insert.bindBlob(1, imgInset);
        insert.bindString(2, decripcion );
        Long resultado = insert.executeInsert();
        Toast.makeText(getApplicationContext(), "Imagen ingresada : Id " + resultado.toString(), Toast.LENGTH_LONG).show();
        db.close();
        limpiar();

    }

    private byte[] imagemTratada(byte[] imagem_img){

        while (imagem_img.length > 500000){
            Bitmap bitmap = BitmapFactory.decodeByteArray(imagem_img, 0, imagem_img.length);
            Bitmap resized = Bitmap.createScaledBitmap(bitmap, (int)(bitmap.getWidth()*0.8), (int)(bitmap.getHeight()*0.8), true);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            resized.compress(Bitmap.CompressFormat.PNG, 100, stream);
            imagem_img = stream.toByteArray();
        }
        return imagem_img;

    }

    private void limpiar(){
        ObjetoImagen.setImageBitmap(null);
        txtDescripcion.setText(null);
    }
}