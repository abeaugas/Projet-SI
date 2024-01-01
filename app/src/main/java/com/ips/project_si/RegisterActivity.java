package com.ips.project_si;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ips.project_si.model.User;

public class RegisterActivity extends AppCompatActivity {
    private EditText registerEmailEditText;
    private EditText registerNomEditText;
    private EditText registerPrenomEditText;
    private EditText registerMdpEditText;
    private EditText registerMdpConfirmEditText;
    private EditText registerTelephoneEditText;
    private Button registerBtn;
    private ProgressBar registerProgressBar;


    private TextView deja;
    private FirebaseAuth auth; //la classe qui va communiquer avec Firbase

    private User user;
    private DatabaseReference dbRef;//bdd realtime pour stocker nos données

    @Override
    //instancier les views existantes dans l'activité (.xml)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initViews();// pour initialiser les views
        auth=FirebaseAuth.getInstance();//pour instancier Firebaseauth
        dbRef= FirebaseDatabase.getInstance().getReference("users"); //pour créer la collection users dans firebaseS

        deja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(RegisterActivity.this, com.ips.project_si.LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerProgressBar.setVisibility(View.VISIBLE);
                //récuperer les champs remplit par l'utilisateur
                String email=registerEmailEditText.getText().toString();
                String password=registerMdpEditText.getText().toString();
                String telephone=registerTelephoneEditText.getText().toString();
                String nom=registerNomEditText.getText().toString();
                String prenom=registerPrenomEditText.getText().toString();
                String confirmPassword=registerMdpConfirmEditText.getText().toString();
                
                // tester si les champs sont nulls(non référencé dans la mémoire) ou vides(existe déjà ms sans valeur)
                if(email.isEmpty() || password.isEmpty() || telephone.isEmpty() || nom.isEmpty() || prenom.isEmpty() || confirmPassword.isEmpty()
                || email.equals("") || password.equals("") || telephone.equals("") || nom.equals("") || prenom.equals("") || confirmPassword.equals(""))
                {
                    Toast.makeText(RegisterActivity.this, "Ils restent des champs à remplire", Toast.LENGTH_SHORT).show();
                    return; //Pour sortir de la méthode onClick()
                }
                //Tester si le password et son confirmation sont identiques
                if(!password.equals(confirmPassword))
                {
                    Toast.makeText(RegisterActivity.this, "Le mot de passe n'est pas identique", Toast.LENGTH_SHORT).show();
                    return;
                }
                user=new User();
                //renseigner les valeurs du champs de l'utilisateur
                user.setEmail(email);
                user.setNom(nom);
                user.setPrenom(prenom);
                user.setTelephone(telephone);
                user.setPassword(password);

                auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(
                        new OnCompleteListener<AuthResult>() {

                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {// Si le registre est fait avec succès
                            if(task.isSuccessful())
                            {   user.setId(auth.getCurrentUser().getUid()); // pour initaliser l'id d'utilisateur par une valeur
                                storeUserInfos(user);//pour stocker les données de user dans firebase
                                registerProgressBar.setVisibility(View.GONE);
                                Toast.makeText(RegisterActivity.this, "Succès", Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(RegisterActivity.this, com.ips.project_si.LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }else
                            {
                                registerProgressBar.setVisibility(View.GONE);
                                Toast.makeText(RegisterActivity.this, "Failude", Toast.LENGTH_SHORT).show();

                            }
                            }
                        }
                ).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
        });

    }

    private void storeUserInfos(User user) {
        dbRef.child(user.getId()).setValue(user); //remplire la collection par les infos de user
    }

    private void initViews() {
        registerEmailEditText=findViewById(R.id.editTextEmail);//Pour récuperer View ou il existe la case email
        registerNomEditText=findViewById(R.id.editTextSignInNom);
        registerPrenomEditText=findViewById(R.id.editTextSignInPrenom);
        registerTelephoneEditText=findViewById(R.id.editTextSignInNumTele);
        registerMdpEditText=findViewById(R.id.editTextSignInMdp);
        registerMdpConfirmEditText=findViewById(R.id.editTextSignInMdpConfirm);
        registerBtn=findViewById(R.id.buttonSingIn);
        registerProgressBar=findViewById(R.id.progressBarRegister);
        deja = findViewById(R.id.textViewSingInCreerCompte);

    }
}