package com.ips.project_si;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private EditText emailLoginEditText;

    private static int REQUEST_ENABLE_BT = 1;


    private EditText passwordLoginEditText;
    private Button btnLogin;
    private FirebaseAuth auth;

    private TextView creer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        auth=FirebaseAuth.getInstance();
        initViews();
        creer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this, com.ips.project_si.RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=emailLoginEditText.getText().toString();
                String password=passwordLoginEditText.getText().toString();
                if(email.isEmpty()|| password.isEmpty() || email.equals("")||password.equals("")){
                    Toast.makeText(LoginActivity.this, "toutes les champs doivent être remplis", Toast.LENGTH_SHORT).show();
                    return;
                }
                auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {


                            BluetoothManager bluetoothManager = getSystemService(BluetoothManager.class);
                            BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
                            if (bluetoothAdapter == null) {
                                // Device doesn't support Bluetooth
                            }
                            if (!bluetoothAdapter.isEnabled()) {
                                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                            }

                            Toast.makeText(LoginActivity.this, "Connexion a réussi", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(LoginActivity.this, com.ips.project_si.home_screen.class);
                            startActivity(intent);
                            finish();

                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

            }
        });

    }

    private void initViews() {
        emailLoginEditText=findViewById(R.id.editTextEmail);
        passwordLoginEditText=findViewById(R.id.editTextPassword);
        btnLogin=findViewById(R.id.buttonLogin);
        creer = findViewById(R.id.textViewLoginCreerCompte);
    }
}