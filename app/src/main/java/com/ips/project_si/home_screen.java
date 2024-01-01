package com.ips.project_si;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class home_screen extends AppCompatActivity {

    private RecyclerView recyclerView;

    private List<String> salles ;
    private List<String> titles ;
    private List<Integer> images;

    private myAdapter adapter;

    private TextView bjr_txtView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private DocumentReference user_ref = db.collection("users").document("user_one");

    private static final String TAG = "MyActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView leftIcon = findViewById(R.id.return_arriere);
        ImageView rightIcon = findViewById(R.id.menu);
        recyclerView = findViewById(R.id.recycler_view);
        salles = new ArrayList<>();
        titles = new ArrayList<>();
        images = new ArrayList<>();
        adapter = new myAdapter(this,titles,images);


        bjr_txtView = findViewById(R.id.bonjour);

        loadUser_salles();

        images.add(R.drawable.baseline_home_24);
        images.add(R.drawable.baseline_person_24);

        GridLayoutManager manager = new GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);


    }


    public void loadUser_salles(){


        user_ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                       salles = (ArrayList<String>) document.get("access");


                       for(String salle : salles){
                           titles.add(salle);
                       }

                        adapter.notifyDataSetChanged();


                    }else{
                        Log.d(TAG,"no such document");
                    }
                }
            }
        });





    }
}
