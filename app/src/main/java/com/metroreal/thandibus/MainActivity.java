package com.metroreal.thandibus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity
{
    FirebaseAuth fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fAuth = FirebaseAuth.getInstance();



    }

    public void aRegistrarse(View v)
    {
        Intent intent = new Intent(MainActivity.this, RegistrarseActivity.class);
        startActivity(intent);
    }
    public void aLogin(View v)
    {
        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onStart()
    {
        super.onStart();
        if (fAuth.getCurrentUser() != null)
        {
            User user = new User();
            Log.w("OnCreate","logueado");
            String tipo = user.getTipo();
            Log.w("OnCreate",tipo);
            /*if (tipo.equals("conductor"))
            {
                startActivity(new Intent(MainActivity.this,ConductorActivity.class));
            }
            else if (tipo.equals("pasajero"))
            {
                startActivity(new Intent(MainActivity.this,PasajeroActivity.class));
            }*/
            //finish();
        }
    }
}
