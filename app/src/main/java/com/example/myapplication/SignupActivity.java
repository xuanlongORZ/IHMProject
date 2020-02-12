package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    public DBManager dbHelper;


    @BindView(R.id.input_name)
    EditText _nameText;
    @BindView(R.id.input_email) EditText _emailText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.input_reEnterPassword) EditText _reEnterPasswordText;
    @BindView(R.id.btn_signup)
    Button _signupButton;
    @BindView(R.id.link_login)
    TextView _loginLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        dbHelper = new DBManager(this);
        dbHelper.openDatabase();

        final SQLiteDatabase database;
        database = dbHelper.getDatabase();

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup(database);
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    public void signup(SQLiteDatabase database) {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();


        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        onSignupSuccess(database,name,email,password);
                        progressDialog.dismiss();
                    }
                }, 1500);
    }


    public void onSignupSuccess(SQLiteDatabase database, String name, String email, String password) {
        Cursor cursor = database.query("User", null, null, null, null, null, null);
        int flag = 0;
        if (cursor.moveToFirst()) {
            do{
                System.out.println(cursor.getString(cursor.getColumnIndex("id")));
                System.out.println(cursor.getString(cursor.getColumnIndex("name")));
                System.out.println(cursor.getString(cursor.getColumnIndex("mail")));
                if(email.equals(cursor.getString(cursor.getColumnIndex("mail")))){
                    flag = 1;
                    break;
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        if(flag==0){
            ContentValues values = new ContentValues();
            values.put("name",name);
            values.put("mail",email);
            values.put("password",password);
            values.put("money",0);
            database.insert("User", null, values);
            _signupButton.setEnabled(true);
            database.close();
            Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            finish();
        }else{
            onSignupFailed();
        }
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed - Email address already exists", Toast.LENGTH_LONG).show();
        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            if (name.isEmpty() || name.length() < 2) {
                _nameText.setError("at least 2 characters");
                valid = false;
            } else {
                _nameText.setError(null);
            }
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                _emailText.setError("enter a valid email address");
                valid = false;
            } else {
                _emailText.setError(null);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            if (password.isEmpty() || password.length() < 4 || password.length() > 20) {
                _passwordText.setError("between 4 and 20 characters");
                valid = false;
            } else {
                _passwordText.setError(null);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 20 || !(reEnterPassword.equals(password))) {
                _reEnterPasswordText.setError("Password Do not match");
                valid = false;
            } else {
                _reEnterPasswordText.setError(null);
            }
        }

        return valid;
    }
}