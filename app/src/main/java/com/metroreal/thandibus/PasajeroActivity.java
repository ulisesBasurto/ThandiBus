package com.metroreal.thandibus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class PasajeroActivity extends AppCompatActivity {

    private Button btLogout;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fDatabase;
    private TextView txInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pasajero);

        fAuth = FirebaseAuth.getInstance();
        fDatabase = FirebaseFirestore.getInstance();
        btLogout = (Button) findViewById(R.id.btnLogout);
        txInfo = (TextView) findViewById(R.id.txtInfo);

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
}
