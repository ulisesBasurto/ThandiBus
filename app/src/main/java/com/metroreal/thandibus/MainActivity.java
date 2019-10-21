package com.metroreal.thandibus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
{
    FirebaseAuth fAuth;
    FirebaseFirestore fDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fAuth = FirebaseAuth.getInstance();
        fDatabase = FirebaseFirestore.getInstance();
    }
    @Override
    protected void onStart()
    {
        super.onStart();
        iniciar();
    }

    private void iniciar()
    {
        if (fAuth.getCurrentUser() != null)
        {
            String idUsuario = fAuth.getCurrentUser().getUid();
            fDatabase.collection("usuarios").document(idUsuario).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String tipo = "";
                            tipo = document.getString("tipo");

                            if (tipo.equals("conductor"))
                            {
                                startActivity(new Intent(MainActivity.this,ConductorActivity.class));
                                finish();
                            }
                            else if (tipo.equals("pasajero"))
                            {
                                startActivity(new Intent(MainActivity.this,PasajeroActivity.class));
                                finish();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "No such document", Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        Toast.makeText(MainActivity.this, "failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else
        {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            },3000);
        }
    }
}
