package com.example.ximenamoure.petvacay;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import java.text.SimpleDateFormat;
import java.util.Date;

import android.widget.TextView;

import static com.example.ximenamoure.petvacay.UserRegistrationActivity.CLIENTID;

public class UpdatePetActivity extends AppCompatActivity {


    static final int TOMAR_FOTO = 100;
    Uri fileUri;

    File petImage;
    private int clientId;

    private JSONObject petData;

    private EditText petName;
    private TextView mDisplayAge;
    private TextView mDisplayWeight;

    private int petWeight;
    private int petAge;

    private EditText additionalPetInfo;

    private int petId;

    private RadioButton  radioBtnMaleGender;
    private RadioButton  radioBtnFemaleGender;
    private RadioButton radioBtnDogType;
    private RadioButton radioBtnCatType;


    private RadioGroup radioGroupPetType;
    private RadioGroup radioGroupPetGender;

    private CheckBox checkBoxHasVaccination;
    private CheckBox checkBoxIsFriendly;

    private RoundedImage image;

    private Button savePetButton;

    private String petGender;
    private String petType;

    private boolean isFriendly=false;
    private boolean hasVaccination=false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_pet);

        clientId = getIntent().getIntExtra("CLIENT_ID", 0);
        petId=getIntent().getIntExtra("PET_ID",0);
        petName=(EditText)findViewById(R.id.petName);
        mDisplayAge=(TextView)findViewById(R.id.petAge);
        mDisplayWeight=(TextView)findViewById(R.id.petWeight);
        additionalPetInfo=(EditText)findViewById(R.id.petAdditionalInfo);

        radioBtnMaleGender=(RadioButton)findViewById(R.id.genderMale);
        radioBtnFemaleGender=(RadioButton)findViewById(R.id.femaleGender);

        radioBtnDogType=(RadioButton)findViewById(R.id.dogType);
        radioBtnCatType=(RadioButton)findViewById(R.id.catType);


        savePetButton=(Button)findViewById(R.id.savePetButton);


        radioGroupPetGender=(RadioGroup)findViewById(R.id.radioGroupPetGender);
        radioGroupPetType=(RadioGroup)findViewById(R.id.radioGroupPetType);


        checkBoxHasVaccination=(CheckBox)findViewById(R.id.hasVaccination);
        checkBoxIsFriendly=(CheckBox)findViewById(R.id.isFriendly);

        image=(RoundedImage)findViewById(R.id.changeImage);


        getPetData(clientId,petId);



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

                attemptUpdatePet();
            }
        });


        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                fileUri = getNewFile();
                i.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(i, TOMAR_FOTO);
            }
        });

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



    @Override //se llama cuando startActivityForResult devuelve un valor, que hacer cuando dicha activity termina
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        try{
            if(requestCode == TOMAR_FOTO){
                if(resultCode == RESULT_OK){
                    //Cargo el ImageView con el fileUri
                    File imgFile = new File(fileUri.getPath());
                    byte [] petImg = getImageByteArray(imgFile);
                    Bitmap bmp = BitmapFactory.decodeByteArray(petImg, 0, petImg.length);
                    image.setImageBitmap(bmp);
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


    private void fillPetData() {


        try{
            SharedPreferences m = PreferenceManager.getDefaultSharedPreferences(this);
            String response = m.getString("ResponsePetData", "");
            petData = new JSONObject(response);

            if(!petData.get("name").toString().equals("null")){
                petName.setText(petData.get("name").toString());
                mDisplayWeight.setText(petData.get("weight").toString() + " kg");
                mDisplayAge.setText(petData.get("age").toString()+ " años");
                petAge=  Integer.parseInt(petData.get("age").toString());
                petWeight=Integer.parseInt(petData.get("weight").toString());

                boolean hasVaccination=(boolean)petData.get("hasVaccination");
                boolean isFriendly=(boolean)petData.get("friendlyPet");

                String petType=petData.get("petType").toString();
                String petGender=petData.get("gender").toString();



                if(petGender.equals("Macho")){

                    radioGroupPetGender.check(R.id.maleGender);


                }
                else if(petGender.equals("Hembra")){
                   radioGroupPetGender.check(R.id.femaleGender);
                }


                    if (petType.equals("Perro")) {

                        radioGroupPetType.check(R.id.dogType);
                    } else if(petType.equals("Gato")){

                       radioGroupPetType.check(R.id.catType);
                    }



                if(hasVaccination){
                    checkBoxHasVaccination.setChecked(true);
                }
                if(isFriendly){

                    checkBoxIsFriendly.setChecked(true);
                }

                if(!TextUtils.isEmpty(petData.get("information").toString())){

                    additionalPetInfo.setText(petData.get("information").toString());

                }

                String img = petData.get("petImage").toString();

                if(!img.equals("null")){
                    byte[] imgBytes = Base64.decode(img, Base64.DEFAULT);
                    Bitmap bmp = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
                    image.setImageBitmap(Bitmap.createScaledBitmap(bmp, image.getWidth(),
                           image.getHeight(), false));
                }
            }

        }
        catch (JSONException ex){}
    }


    private void getPetData(int idClient,int idPet) {
        String url = BackendConfig.GetInstance().BackendIP + "clients/" + idClient+"/pets/"+idPet;

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        sharedResponse(response);
                        fillPetData();

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

                                Toast.makeText(UpdatePetActivity.this, msg, Toast.LENGTH_SHORT).show();
                            } catch (UnsupportedEncodingException ex) {
                            }
                        }
                    }
                });
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsObjRequest);

    }

    private void sharedResponse(JSONObject response) {
        SharedPreferences m = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = m.edit();
        editor.putString("ResponsePetData", response.toString());
        editor.commit();
    }


    private void attemptUpdatePet() {
        // Reset errors.
        petName.setError(null);
        mDisplayAge.setError(null);
        mDisplayWeight.setError(null);

        boolean cancel = false;
        View focusView = null;
        if (TextUtils.isEmpty(petName.getText().toString())) {
            petName.setError(getString(R.string.error_field_required));
            focusView = petName;
            cancel = true;
        }

        if (TextUtils.isEmpty( mDisplayWeight.getText().toString())) {
            mDisplayWeight.setError(getString(R.string.error_field_required));
            focusView =  mDisplayWeight;
            cancel = true;

        }
        if (TextUtils.isEmpty( mDisplayAge.getText().toString())) {
            mDisplayAge.setError(getString(R.string.error_field_required));
            focusView =  mDisplayAge;
            cancel = true;

        }
        if (radioGroupPetGender.getCheckedRadioButtonId()<=0) {
            radioBtnMaleGender.setError("Debe seleccionar uno");
            focusView = radioBtnMaleGender;
            cancel = true;
        }

        if (radioGroupPetType.getCheckedRadioButtonId()<=0) {
            radioBtnDogType.setError("Debe seleccionar uno");
            focusView =radioBtnDogType;
            cancel = true;
        }


        if (cancel) {
            // There was an error
            focusView.requestFocus();
        } else {
            updatePet(petImage);
        }
    }


    private void updatePet(File petImg){


        String url = BackendConfig.GetInstance().BackendIP + "clients/" + clientId + "/pet/"+petId;

        JSONObject dataToSend = makeJsonToSend(petImg);


        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.PUT, url, dataToSend, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        //   progress.dismiss();
                        Toast.makeText(UpdatePetActivity.this, "Mascota modificada", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(UpdatePetActivity.this, ClientProfileActivity.class);
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

                                Toast.makeText(UpdatePetActivity.this, msg, Toast.LENGTH_SHORT).show();
                            } catch (UnsupportedEncodingException ex) {
                            }
                        }
                    }
                });
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsObjRequest);

    }


    private JSONObject makeJsonToSend(File petImg) {
        JSONObject json = new JSONObject();

        int selected=radioGroupPetGender.getCheckedRadioButtonId();
        RadioButton gender=(RadioButton) findViewById(selected);


        if(selected!=-1) {
            petGender = gender.getText().toString();
        }


        int typeSelected=radioGroupPetType.getCheckedRadioButtonId();
        RadioButton type=(RadioButton)findViewById(typeSelected);

        if(typeSelected!=-1) {
            petType = type.getText().toString();
        }

        if(checkBoxIsFriendly.isChecked()){
            isFriendly=true;
        }
        if(checkBoxHasVaccination.isChecked()){
            hasVaccination=true;
        }

        try{

            json.put("Name", petName.getText().toString());
            json.put("PetType", petType);
            json.put("Age", petAge);
            json.put("Weight", petWeight);
            json.put("FriendlyPet", isFriendly);
            json.put("HasVaccination", hasVaccination);
            json.put("Gender", petGender);
            json.put("Information",additionalPetInfo.getText().toString());

            if(petImg!=null)
            {
                byte [] image = getImageByteArray(petImg);
                json.put("PetImage", new JSONArray(image));
            }

        }
        catch(JSONException ex) {}
        return json;

    }



}
