package com.metroreal.thandibus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ConductorActivity extends AppCompatActivity {

    private Button btLogout;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fDatabase;
    private TextView txInfo;
 
    LocationManager locationManager;
    double latitud;
    double longitud;
    String idUsuario;
    TextView txLatitud;
    TextView txLongitud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conductor);

        fAuth = FirebaseAuth.getInstance();
        fDatabase = FirebaseFirestore.getInstance();
        btLogout = (Button) findViewById(R.id.btnLogout);
        txInfo = (TextView) findViewById(R.id.txtInfo);
        btLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fAuth.signOut();
                Intent intent = new Intent(ConductorActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        txLatitud = (TextView) findViewById(R.id.txtLatitud);
        txLongitud = (TextView) findViewById(R.id.txtLongitud);
        idUsuario = fAuth.getCurrentUser().getUid();

        mostrarInfo();
    }

    private void mostrarAlert()
    {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Activar Ubicacion")
                .setMessage("Su ubicación esta desactivada.\npor favor active su ubicación " +
                        "usa esta app")
                .setPositiveButton("Configuración de ubicación", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }
    private boolean checkUbiacacion() {
        if (!isUbicacionActivada())
            mostrarAlert();
        return isUbicacionActivada();
    }
    private boolean isUbicacionActivada() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public void statusGPS(View v) {

        if (!checkUbiacacion())
            return;
        Button button = (Button) v;
        if (button.getText().equals("Terminar")) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            }
            locationManager.removeUpdates(location);
            button.setText("Iniciar");
        }
        else {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 1000*10, 5, location);
            Toast.makeText(this, "Ubicacion GPS Iniciado", Toast.LENGTH_LONG).show();
            button.setText("Terminar");
        }
    }

    private final LocationListener location = new LocationListener() {
        public void onLocationChanged(Location location) {
            latitud = location.getLatitude();
            longitud = location.getLongitude();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    txLatitud.setText(latitud + "");
                    txLongitud.setText(longitud + "");
                    Toast.makeText(ConductorActivity.this, "GPS Actualizado", Toast.LENGTH_SHORT).show();
                    Log.w("GPS conductor", "GPS actualizado" );
                    enviarGPS();
                    
                    
                }
            });
        }
        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }
        @Override
        public void onProviderDisabled(String s) {
        }
    };

    private void mostrarInfo()
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
                        Toast.makeText(ConductorActivity.this, "No such document", Toast.LENGTH_SHORT).show();

                    }
                }
                else
                {
                    Toast.makeText(ConductorActivity.this, "failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void enviarGPS()
    {
        Map<String, Object> map = new HashMap<>();
        map.put("latitud", String.valueOf(latitud));
        map.put("longitud",String.valueOf(longitud));
        
        fDatabase.collection("usuarios").document(idUsuario).update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task2) {
                if (task2.isSuccessful())
                {

                    Toast.makeText(ConductorActivity.this, "Datos enviados a Firestore", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(ConductorActivity.this, "No se crearon los datos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
