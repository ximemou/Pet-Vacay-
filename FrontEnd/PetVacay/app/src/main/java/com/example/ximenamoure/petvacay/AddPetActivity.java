package com.example.ximenamoure.petvacay;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.content.DialogInterface;
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

import static com.example.ximenamoure.petvacay.UserRegistrationActivity.CLIENTID;


public class AddPetActivity extends AppCompatActivity {


    static final int TOMAR_FOTO = 100;
    Uri fileUri;

    File petImage;
    private ProgressDialog progress;

    private TextView mDisplayAge;
    private NumberPicker.OnClickListener mNumberPicker;
    private int  petAge;
    private EditText petName;
    private Button savePetButton;
    private RoundedImage changeImageButton;
    private int petWeight=1;
    private TextView mDisplayWeight;
    private int clientId;
    private EditText mAdditionalInfo;
    private RadioGroup radioGroupGenderPet;

    private RadioGroup radioGroupTypePet;
    private RadioButton petTypeDog;
    private RadioButton petMaleGender;

    private String petGender;


    private String petType;


    private CheckBox vaccination;

    private CheckBox friendly;
    private boolean isFriendly=false;
    private boolean hasVaccination=false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pet);

        clientId = getIntent().getIntExtra("CLIENT_ID", 0);
        mDisplayAge=(TextView)findViewById(R.id.petAge);
        changeImageButton = (RoundedImage) findViewById(R.id.changeImage);

        petName=(EditText)findViewById(R.id.petName);

        vaccination=(CheckBox)findViewById(R.id.hasVaccination);
        friendly=(CheckBox)findViewById(R.id.isFriendly);
        mAdditionalInfo=(EditText) findViewById(R.id.petAdditionalInfo);


        radioGroupGenderPet=(RadioGroup)findViewById(R.id.radioGroupPetGender);

        radioGroupTypePet=(RadioGroup)findViewById(R.id.radioGroupPetType);

        petTypeDog=(RadioButton) findViewById(R.id.dogType);
        petMaleGender=(RadioButton)findViewById(R.id.maleGender);

        savePetButton=(Button)findViewById(R.id.savePetButton);

        mDisplayWeight=(TextView)findViewById(R.id.petWeight);
        mDisplayAge.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                numberpickerDialog();

            }
        });


        mDisplayWeight.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                weigthPickerDialog();
            }
        });

        changeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                fileUri = getNewFile();
                i.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(i, TOMAR_FOTO);
            }
        });

        radioGroupGenderPet.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                petMaleGender.setError(null);
            }
        });
        radioGroupTypePet.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                petTypeDog.setError(null);
            }
        });
        

        mDisplayWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                mDisplayWeight.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDisplayAge.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                mDisplayAge.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        savePetButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                attemptSavePet();
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            changeImageButton.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

        //for API 23+ so it doesnt throw FileUriExposedException
        if(Build.VERSION.SDK_INT>=24){
            try{
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }


    private void weigthPickerDialog(){

        NumberPicker weightPicker=new NumberPicker(this);
        weightPicker.setMaxValue(100);
        weightPicker.setMinValue(1);
        weightPicker.setValue(petWeight);
        NumberPicker.OnValueChangeListener myValueChangeListener=new NumberPicker.OnValueChangeListener(){

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                petWeight=newVal;
                mDisplayWeight.setText(""+newVal+" kg ");



            }
        };
        weightPicker.setOnValueChangedListener(myValueChangeListener);
        AlertDialog.Builder builder=new AlertDialog.Builder(this).setView(weightPicker);
        builder.setTitle("Peso");
        builder.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {



            }
        } );
        builder.show();

    }


    private void numberpickerDialog(){
        NumberPicker agePicker=new NumberPicker(this);
        agePicker.setMaxValue(20);
        agePicker.setMinValue(0);
        agePicker.setValue(petAge);
        NumberPicker.OnValueChangeListener myValueChangeListener=new NumberPicker.OnValueChangeListener(){

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                petAge=newVal;
                mDisplayAge.setText(""+newVal+ " años");


            }
        };
        agePicker.setOnValueChangedListener(myValueChangeListener);
        AlertDialog.Builder builder=new AlertDialog.Builder(this).setView(agePicker);
        builder.setTitle("Edad");
        builder.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        } );

        builder.show();

    }


    private void savePet(File petImg){

        int selected=radioGroupGenderPet.getCheckedRadioButtonId();
        RadioButton gender=(RadioButton) findViewById(selected);


        if(selected!=-1) {
            petGender = gender.getText().toString();
        }


        int typeSelected=radioGroupTypePet.getCheckedRadioButtonId();
        RadioButton type=(RadioButton)findViewById(typeSelected);

        if(typeSelected!=-1) {
            petType = type.getText().toString();
        }


        if(friendly.isChecked()){
            isFriendly=true;
        }
        if(vaccination.isChecked()){
            hasVaccination=true;
        }



            String url = BackendConfig.GetInstance().BackendIP + "clients/" + clientId + "/pets";

            JSONObject dataToSend = makeJsonToSend(petImg);


            RequestQueue queue = Volley.newRequestQueue(this);

            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.POST, url, dataToSend, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {

                            progress.dismiss();
                            Toast.makeText(AddPetActivity.this, "Mascota agregada", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(AddPetActivity.this, ClientProfileActivity.class);
                            i.putExtra(CLIENTID, clientId);
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
                                    progress.dismiss();
                                    Toast.makeText(AddPetActivity.this, msg, Toast.LENGTH_SHORT).show();
                                } catch (UnsupportedEncodingException ex) {
                                }
                            }
                        }
                    });
            jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(jsObjRequest);
    }


    private void attemptSavePet() {
        // Reset errors.
        petName.setError(null);
        mDisplayAge.setError(null);
        mDisplayWeight.setError(null);
        petMaleGender.setError(null);
        petTypeDog.setError(null);

        boolean cancel = false;
        View focusView = null;



        if (TextUtils.isEmpty(petName.getText().toString())) {
            petName.setError(getString(R.string.error_field_required));
            focusView = petName;
            cancel = true;
        }

        if (TextUtils.isEmpty( mDisplayWeight.getText().toString())) {
            mDisplayWeight.setError(getString(R.string.error_field_required));
            focusView = mDisplayWeight;
            cancel = true;

        }
        if (TextUtils.isEmpty( mDisplayAge.getText().toString())) {
            mDisplayAge.setError(getString(R.string.error_field_required));
            focusView = mDisplayAge;
            cancel = true;

        }
        if (radioGroupGenderPet.getCheckedRadioButtonId()<=0) {
            petMaleGender.setError("Debe seleccionar uno");
            focusView = petMaleGender;
            cancel = true;
        }

        if (radioGroupTypePet.getCheckedRadioButtonId()<=0) {
            petTypeDog.setError("Debe seleccionar uno");
            focusView = petTypeDog;
            cancel = true;
        }


        if (cancel) {
            // There was an error
            focusView.requestFocus();
        } else {
            startProgressBar();
            savePet(petImage);
        }
    }



    private JSONObject makeJsonToSend(File petImage) {
        JSONObject json = new JSONObject();

            try{

                json.put("Name", petName.getText().toString());
                json.put("PetType", petType);
                json.put("Age", petAge);
                json.put("Weight", petWeight);
                json.put("FriendlyPet", isFriendly);
                json.put("HasVaccination", hasVaccination);
                json.put("Gender", petGender);
                json.put("Information", mAdditionalInfo.getText().toString());
                if(petImage!=null) {
                    byte [] image = getImageByteArray(petImage);
                    json.put("PetImage", new JSONArray(image));
                }

            }
            catch(JSONException ex) {}
            return json;

    }

    private Uri getNewFile(){
        Uri uri = null;
        try{
            //crea una carpeta misFotos en el directorio de las fotos
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
                    changeImageButton.setImageBitmap(bmp);
                    SavePetImageFile(imgFile);
                }
            }
        }
        catch(Exception ex){
            Log.e("Error al volver de foto", ex.getMessage());
        }
    }

    private void SavePetImageFile(File imgFile) {
        petImage = imgFile;
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

    private void startProgressBar()
    {
        progress = new ProgressDialog(AddPetActivity.this);
        progress.setTitle("Guardando mascota");
        progress.setMessage("Esperando respuesta...");
        progress.setCancelable(false);
        progress.show();
    }
}
