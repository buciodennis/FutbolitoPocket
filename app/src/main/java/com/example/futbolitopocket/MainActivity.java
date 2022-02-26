package com.example.futbolitopocket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private final int anchoPelota = 100, anchoPorteria = 290;
    private final int altoPelota = 100, altoPorteria = 250;

    private float posX, xMax, acelerometroX, velocidadX = 0.0f;
    private float posY, yMax, acelerometroY, velocidadY = 0.0f;
    private float porteriaInicio, porteriaFinal;
    private float frameTime = 0.666f;


    private int scoreA, scoreB = 0;
    private boolean gol = false;

    private Bitmap pelota, porteriaA, porteriaB;

    private SensorManager sensorManager;
    private Sensor sensorACCELEROMETER;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    private void actualizarPosicion() {


    }


    private class MiVista extends View {

        public MiVista(Context context) {
            super(context);
        }
    }

}