package com.metroreal.thandibus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegistrarseActivity extends AppCompatActivity
{
    //push a RealtimeDatabase
    private EditText EdTnombre;
    private EditText EdTcorreo;
    private EditText EdTcontraseña;
    private Button BtRegistrarse;

    private String nombre;
    private String correo;
    private String contraseña;

    FirebaseAuth fAuth;
    DatabaseReference fDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);

        fAuth = FirebaseAuth.getInstance();
        fDatabase = FirebaseDatabase.getInstance().getReference();

        EdTnombre = (EditText) findViewById(R.id.txtNombre);
        EdTcorreo = (EditText) findViewById(R.id.txtCorreo);
        EdTcontraseña = (EditText) findViewById(R.id.txtContraseña);
        BtRegistrarse = (Button) findViewById(R.id.btnAceptar);

        BtRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                nombre = EdTnombre.getText().toString();
                correo = EdTcorreo.getText().toString();
                contraseña = EdTcontraseña.getText().toString();

                if (!nombre.isEmpty() && !correo.isEmpty() && !contraseña.isEmpty())
                {
                    if(contraseña.length() >= 6)
                    {
                        registrarUsuario();
                    }
                    else
                    {
                        Toast.makeText(RegistrarseActivity.this, "El password debe contener mas de 6 carateres", Toast.LENGTH_SHORT).show();
                    }

                }
                else
                {
                    Toast.makeText(RegistrarseActivity.this, "Debe completar los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private void registrarUsuario()
    {
        fAuth.createUserWithEmailAndPassword(correo,contraseña).addOnCompleteListener(new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if(task.isSuccessful())
                {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name",nombre);
                    map.put("email",correo);
                    map.put("password",contraseña);
                    String id = fAuth.getUid();
                    fDatabase.child("Users").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            if (task2.isSuccessful())
                            {
                                startActivity(new Intent(RegistrarseActivity.this,ProfileActivity.class));
                                finish();
                            }
                            else
                            {
                                Toast.makeText(RegistrarseActivity.this, "No se crearon los datos", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                }
                else
                {
                    Toast.makeText(RegistrarseActivity.this, "Error al registrar", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}
