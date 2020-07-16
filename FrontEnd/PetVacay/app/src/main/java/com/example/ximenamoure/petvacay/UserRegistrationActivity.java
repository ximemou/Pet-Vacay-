package com.example.ximenamoure.petvacay;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.text.TextWatcher;
import android.text.Editable;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ximenamoure.petvacay.BackendConfig.BackendConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class UserRegistrationActivity extends AppCompatActivity {

    private EditText txtMail;
    private EditText txtPassword;
    private EditText txtConfirmPassword;
    private Button btnRegisterUser;

    public static final String CLIENTID="";

    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        txtMail = (EditText) findViewById(R.id.txtMail);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        txtConfirmPassword = (EditText) findViewById(R.id.txtConfirmPassword);
        checkMail(txtMail);
        checkPassword(txtPassword);
        checkConfirmPassword(txtConfirmPassword);

        btnRegisterUser = (Button) findViewById(R.id.btnRegisterUser);
        registUser(btnRegisterUser);
    }

    private void checkMail(EditText txtMail) {
        txtMail.setError(null);

        txtMail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    checkMail();
                }
            }
        });
    }

    private void checkMail() {
        String email = txtMail.getText().toString();
        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(email)) {
            txtMail.setError("Este campo es requerido");
            focusView = txtMail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            txtMail.setError("Email inválido");
            focusView = txtMail;
            cancel = true;
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private void checkPassword(EditText txtPassword) {
        txtPassword.setError(null);

        txtPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    checkPassword();
                }
            }
        });
    }

    private void checkPassword() {
        String password = txtPassword.getText().toString();
        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password)) {
            txtPassword.setError("Este campo es requerido");
            focusView = txtPassword;
            cancel = true;
        }
        else if (!isPasswordValid(password)) {
            txtPassword.setError("Contraseña inválida");
            focusView = txtPassword;
            cancel = true;
        }
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 6;
    }

    private void checkConfirmPassword(EditText txtConfirmPassword) {
        txtConfirmPassword.setError(null);
        //checkPassword(txtConfirmPassword);

        //String password = txtConfirmPassword.getText().toString();
        txtConfirmPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    checkConfirmPassword();
                }
            }
        });
    }

    private void checkConfirmPassword() {
        if(!(txtPassword.getText().toString()).equals(txtConfirmPassword.getText().toString())) {
            txtConfirmPassword.setError("Las contraseñas ingresadas no coinciden");
        }
    }

    private void registUser(Button btnRegisterUser) {
        btnRegisterUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(txtMail.getError() == null && txtPassword.getError() == null
                        && txtConfirmPassword.getError() == null) {

                    startProgressBar();
                    registerUser();


                } else {
                    Toast.makeText(UserRegistrationActivity.this, "No se puede completar el registro, " +
                            "hay campos con errores", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void registerUser()
    {
        String url = BackendConfig.GetInstance().BackendIP + "clients";
        JSONObject dataToSend = makeJsonToSend();

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, url, dataToSend, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            progress.dismiss();
                            saveLoggedUser();
                            Toast.makeText(UserRegistrationActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(UserRegistrationActivity.this, ClientProfileActivity.class);
                            i.putExtra(CLIENTID, Integer.parseInt(response.get("message").toString()));
                            startActivity(i);

                        }
                        catch(JSONException ex){
                            Toast.makeText(UserRegistrationActivity.this, "No se ha podido registrar", Toast.LENGTH_SHORT).show();
                        }

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

                                progress.dismiss();
                                Toast.makeText(UserRegistrationActivity.this, msg , Toast.LENGTH_SHORT).show();
                            }
                            catch(UnsupportedEncodingException ex) {}
                        }
                    }
                });
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsObjRequest);
    }

    private void saveLoggedUser() {
        SharedPreferences m = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = m.edit();
        editor.putString("LoggedClient", CLIENTID);
        editor.commit();
    }

    private JSONObject makeJsonToSend()
    {
        JSONObject json = new JSONObject();
        try{
            json.put("Email", txtMail.getText().toString());
            json.put("Password", txtPassword.getText().toString());
            json.put("RepeatedPassword", txtConfirmPassword.getText().toString());
        }
        catch(JSONException ex) {}
        return json;
    }

    private void startProgressBar()
    {
        progress = new ProgressDialog(UserRegistrationActivity.this);
        progress.setTitle("Cargando");
        progress.setMessage("Esperando respuesta...");
        progress.setCancelable(false);
        progress.show();
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(UserRegistrationActivity.this, LoginActivity.class);
        startActivity(i);
    }

}
