package com.metroreal.thandibus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistrarseActivity extends AppCompatActivity
{
    private EditText edTnombre;
    private EditText edTcorreo;
    private EditText edTcontraseña;
    private CheckBox checkConductor;
    private Button btRegistrarse;

    private String nombre;
    private String correo;
    private String contraseña;
    private String tipo = "pasajero";

    FirebaseAuth fAuth;
    FirebaseFirestore fDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);

        fAuth = FirebaseAuth.getInstance();
        fDatabase = FirebaseFirestore.getInstance();

        edTnombre = (EditText) findViewById(R.id.txtNombre);
        edTcorreo = (EditText) findViewById(R.id.txtCorreo);
        edTcontraseña = (EditText) findViewById(R.id.txtContraseña);
        checkConductor = (CheckBox) findViewById(R.id.chbConductor);
        btRegistrarse = (Button) findViewById(R.id.btnAceptar);

        btRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                nombre = edTnombre.getText().toString();
                correo = edTcorreo.getText().toString();
                contraseña = edTcontraseña.getText().toString();

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
        if (checkConductor.isChecked())
        {
            tipo = "conductor";
        }
        fAuth.createUserWithEmailAndPassword(correo,contraseña).addOnCompleteListener(new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if(task.isSuccessful())
                {
                    Map<String, Object> map = new HashMap<>();
                    map.put("nombre",nombre);
                    map.put("correo",correo);
                    map.put("contraseña",contraseña);
                    map.put("tipo", tipo);
                    String id = fAuth.getUid();
                    fDatabase.collection("usuarios").document(id).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            if (task2.isSuccessful())
                            {

                                if (tipo.equals("conductor"))
                                {
                                    Intent intent = new Intent(RegistrarseActivity.this,ConductorActivity.class);
                                    intent.addFlags (Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                                else if (tipo.equals("pasajero"))
                                {
                                    Intent intent = new Intent(RegistrarseActivity.this,PasajeroActivity.class);
                                    intent.addFlags (Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                                else
                                {
                                    Log.w("Registrarse","Error enviando a activity personalizado");
                                }
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
