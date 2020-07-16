package com.example.ximenamoure.petvacay;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ximenamoure.petvacay.BackendConfig.BackendConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddClientInfoToProfileActivity extends AppCompatActivity {

    static final int TOMAR_FOTO = 100;

    public static final String CLIENTID="";
    Uri fileUri;
    private ImageView clientImage;
    private TextView clientName;
    private TextView clientSurname;
    private TextView clientPhone;
    private Button updateBtn;
    private JSONObject clientData;

    private File clientImg;

    private int clientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_client_info_to_profile);

        clientId = getIntent().getIntExtra("CLIENT_ID", 0);

        clientImage = (ImageView) findViewById(R.id.changeImage);
        clientName = (TextView) findViewById(R.id.clientName);
        clientSurname = (TextView) findViewById(R.id.clientSurname);
        clientPhone = (TextView) findViewById(R.id.clientPhone);
        updateBtn = (Button) findViewById(R.id.updateClientBtn);

        fillClientData();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            clientImage.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

        //For API 23+ so it doesnt throw FileUriExposedException
        if(Build.VERSION.SDK_INT>=24){
            try{
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        clientImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                fileUri = getNewFile();
                i.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(i, TOMAR_FOTO);
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateClientData();
            }
        });
    }

    private void fillClientData() {
        try{
            SharedPreferences m = PreferenceManager.getDefaultSharedPreferences(this);
            String response = m.getString("Response", "");
            clientData = new JSONObject(response);

            if(!clientData.get("name").toString().equals("null")){
                clientName.setText(clientData.get("name").toString());
                clientSurname.setText(clientData.get("surname").toString());
                clientPhone.setText(clientData.get("phoneNumber").toString());

            }
            else{
                clientName.setHint("EjemploNombre");
                clientSurname.setHint("EjemploApellido");
                clientPhone.setHint("000000000");
            }

        }
        catch (JSONException ex){}
    }

    private Uri getNewFile(){
        Uri uri = null;
        try{
            //it creates a folder  misFotos in the directory of the photos
            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "misFotos");
            if(!mediaStorageDir.exists()){
                mediaStorageDir.mkdirs();
            }
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
            uri = Uri.fromFile(mediaFile);
        }
        catch(Exception ex){
            Log.e("Error al crear archivo", ex.getMessage());
        }
        return uri;
    }

    @Override //called when  startActivityForResult returns a value
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        try{
            if(requestCode == TOMAR_FOTO){
                if(resultCode == RESULT_OK){
                    //Cargo el ImageView con el fileUri
                    File imgFile = new File(fileUri.getPath());
                    byte [] petImg = getImageByteArray(imgFile);
                    Bitmap bmp = BitmapFactory.decodeByteArray(petImg, 0, petImg.length);
                    clientImage.setImageBitmap(bmp);
                    SaveClientImageFile(imgFile);
                    SendImage(imgFile);
                }
            }
        }
        catch(Exception ex){
            Log.e("Error al volver de foto", ex.getMessage());
        }
    }

    private void SaveClientImageFile(File imgFile) {
        clientImg = imgFile;
    }

    private void SendImage(File imgFile) {

        String url = BackendConfig.GetInstance().BackendIP + "clients/" + clientId+"/profileImage";
        byte [] image = getImageByteArray(imgFile);
        JSONObject jsonToSend = makeJson(image);

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, url, jsonToSend, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(error.networkResponse.data != null)
                        {
                            try{
                                String msg = new String(error.networkResponse.data, "UTF-8");
                                JSONObject json;
                                try{
                                    json = new JSONObject(msg);
                                    msg = json.getString("message");
                                }
                                catch(JSONException ex) {}

                                Toast.makeText(AddClientInfoToProfileActivity.this, msg , Toast.LENGTH_SHORT).show();
                            }
                            catch(UnsupportedEncodingException ex) {}
                        }
                    }
                });
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsObjRequest);
    }

    private byte[] getImageByteArray(File imgFile) {
        int size = (int) imgFile.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(imgFile));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        Bitmap b = BitmapFactory.decodeByteArray(bytes, 0 , bytes.length);
        b.compress(Bitmap.CompressFormat.JPEG, 15, byteOutput);
        bytes = byteOutput.toByteArray();
        return bytes;
    }

    private JSONObject makeJson(byte[] image) {
        JSONObject json = new JSONObject();
        try{
            json.put("Image", new JSONArray(image));
        }
        catch(JSONException ex) {}
        return json;
    }

    private JSONObject makeJsonUpdate(){
        JSONObject json = new JSONObject();
        try{
            json.put("Name", clientName.getText().toString());
            json.put("Surname", clientSurname.getText().toString());
            json.put("PhoneNumber", clientPhone.getText().toString());
            json.put("Password", clientData.get("password").toString());
        }
        catch(JSONException ex) {}
        return json;
    }



    private boolean wrongFields(){

        clientName.setError(null);
        clientPhone.setError(null);
        clientSurname.setError(null);

        String name=clientName.getText().toString();
        String surname=clientSurname.getText().toString();
        String phone=clientPhone.getText().toString();
        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(name)) {
            clientName.setError(getString(R.string.error_field_required));
            focusView = clientName;
            cancel = true;
        }

        if (TextUtils.isEmpty(surname)) {
            clientSurname.setError(getString(R.string.error_field_required));
            focusView = clientSurname;
            cancel = true;
        }
        if (surname.length()<4) {
            clientSurname.setError("4 caracteres al menos");
            focusView = clientSurname;
            cancel = true;
        }
        if (surname.length()<4) {
            clientName.setError("4 caracteres al menos");
            focusView = clientName;
            cancel = true;
        }

        if (TextUtils.isEmpty(phone)) {
            clientPhone.setError(getString(R.string.error_field_required));
            focusView = clientPhone;
            cancel = true;
        }

        if (phone.length()<9) {
            clientPhone.setError("9 digitos");
            focusView = clientPhone;
            cancel = true;
        }

        return cancel;

    }

    private void UpdateClientData() {


        if(!wrongFields()) {

            String url = BackendConfig.GetInstance().BackendIP + "clients/" + clientId;
            JSONObject jsonToSend = makeJsonUpdate();

            RequestQueue queue = Volley.newRequestQueue(this);

            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.PUT, url, jsonToSend, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Intent i = new Intent(AddClientInfoToProfileActivity.this, ClientProfileActivity.class);
                            i.putExtra("", clientId);
                            startActivity(i);
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {

                            if (error.networkResponse.data != null) {
                                try {
                                    String msg = new String(error.networkResponse.data, "UTF-8");
                                    JSONObject json;
                                    try {
                                        json = new JSONObject(msg);
                                        msg = json.getString("message");
                                    } catch (JSONException ex) {
                                    }

                                    Toast.makeText(AddClientInfoToProfileActivity.this, msg, Toast.LENGTH_SHORT).show();
                                } catch (UnsupportedEncodingException ex) {
                                }
                            }
                        }
                    });
            jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(jsObjRequest);

        }
        else{
            Toast.makeText(AddClientInfoToProfileActivity.this, "Veifique los datos ingresados", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                clientImage.setEnabled(true);
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(AddClientInfoToProfileActivity.this, ClientProfileActivity.class);
        i.putExtra(CLIENTID,clientId);
        startActivity(i);
    }


}
