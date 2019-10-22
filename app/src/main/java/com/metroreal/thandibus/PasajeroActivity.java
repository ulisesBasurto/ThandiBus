package com.metroreal.thandibus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class PasajeroActivity extends AppCompatActivity {

    private Button btLogout;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fDatabase;
    private TextView txInfo;
    private Spinner spConductores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pasajero);

        fAuth = FirebaseAuth.getInstance();
        fDatabase = FirebaseFirestore.getInstance();
        btLogout = (Button) findViewById(R.id.btnLogout);
        txInfo = (TextView) findViewById(R.id.txtInfo);
        spConductores = (Spinner) findViewById(R.id.sprConductores);

        btLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fAuth.signOut();
                Intent intent = new Intent(PasajeroActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        showInfo();
        llenarSprConductores();
    }

    private void showInfo()
    {
        String idUsuario = fAuth.getCurrentUser().getUid();
        fDatabase.collection("usuarios").document(idUsuario).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                if (task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists())
                    {
                        txInfo.setText(document.getString("nombre") + ", " + document.getString("tipo"));
                    }
                    else
                    {
                        Toast.makeText(PasajeroActivity.this, "No such document", Toast.LENGTH_SHORT).show();

                    }
                }
                else
                {
                    Toast.makeText(PasajeroActivity.this, "failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void llenarSprConductores()
    {
        fDatabase.collection("usuarios")
                .whereEqualTo("tipo","conductor")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task)
                    {
                        if (task.isSuccessful())
                        {
                            ArrayList<String> conductores = new ArrayList<>();
                            int i = 0;
                            for (QueryDocumentSnapshot document : task.getResult())
                            {
                                conductores.add(document.getId());
                                Log.w("LlenarConductores",conductores.get(i));
                                Log.w("LLenarConductores", document.getId() + " => " + document.getData());
                                i++;
                            }
                            spConductores.setAdapter(new ArrayAdapter<>(PasajeroActivity.this, android.R.layout.simple_spinner_dropdown_item, conductores));
                        }
                        else
                            {
                            Log.d("LLenarConductores", "Error getting documents: ", task.getException());
                            }

                    }
                });

        spConductores.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String idConductor = (String) spConductores.getSelectedItem();
                fDatabase.collection("usuarios").document(idConductor).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null)
                        {
                            Log.w("EscuchaConductorGPS", "Listen failed.", e);
                            return;
                        }

                        if (documentSnapshot != null && documentSnapshot.exists())
                        {
                            txInfo.setText(documentSnapshot.getString("latitud") + ", " + documentSnapshot.getString("longitud"));
                            Log.d("EscuchaConductorGPS", "Current data: " + documentSnapshot.getData());
                        }
                        else
                            {
                                Log.d("EscuchaConductorGPS", "Current data: null");
                            }

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                llenarSprConductores();
            }
        });
    }


}
