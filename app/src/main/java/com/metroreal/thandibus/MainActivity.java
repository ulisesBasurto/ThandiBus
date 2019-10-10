package com.metroreal.thandibus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity
{


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void aRegistrarse(View v)
    {
        Intent intent = new Intent(MainActivity.this, RegistrarseActivity.class);
        startActivity(intent);
    }
}
