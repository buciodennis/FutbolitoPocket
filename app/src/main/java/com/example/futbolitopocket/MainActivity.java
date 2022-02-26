package com.example.futbolitopocket;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private final int anchoPelota = 130, anchoPorteria = 330;
    private final int alturaPelota = 130, alturaPorteria = 300;
    //x
    private float velocidadX = 0.0f, posX, xMax, acelerometroX;
    //y
    private float velocidadY = 0.0f, posY, yMax, acelerometroY;
    private float porteriaInicio, porteriaFinal;
    private float frameTime = 0.666f;


    private int scoreA = 0, scoreB = 0;
    private boolean esGol = false;

    private Bitmap pelota, porteriaA, porteriaB;

    private SensorManager sensorManager;
    private Sensor sensorACCELEROMETER;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Tomar los tamaños de la pantalla
        Point tamanio = new Point();
        Display display = getWindowManager().getDefaultDisplay();
        display.getSize(tamanio);

        MiVista miVista = new MiVista(this);
        setContentView(miVista);


        xMax = (float) tamanio.x - anchoPelota;
        yMax = (float) tamanio.y - alturaPorteria;

        //posicion de la pelota inicial
        posX = ((xMax + anchoPelota) / 2) - (anchoPelota / 2);
        posY = ((yMax + anchoPelota) / 2) - (anchoPelota / 2);

        //Posiciones de las porterias
        porteriaInicio = ((xMax + anchoPelota) / 2) - (anchoPorteria / 2);
        porteriaFinal = porteriaInicio + anchoPorteria;


    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    private void actualizarPosicion() {
        //Calcular nueva posicion
        velocidadX += (acelerometroX * frameTime);
        velocidadY += (acelerometroY * frameTime);

        float xS = (velocidadX / 2) * frameTime;
        float yS = (velocidadY / 2) * frameTime;

        posX -= xS;
        posY -= yS;

        boolean anoto = false;

        int lPort = 50;

        //Verificar limites de la posicion en x
        if (posX > xMax) {
            posX = xMax;
        } else if (posX < 0) {
            posX = 0;
        }

        //Lados de las porterias
        else if((posX > porteriaInicio - anchoPelota && posX < porteriaInicio) && (posY > yMax - alturaPorteria
                || posY < alturaPorteria)){
            posX = porteriaInicio - anchoPelota;
        } else if((posX < porteriaFinal && posX > porteriaFinal - lPort) && (posY > yMax - alturaPorteria || posY < alturaPorteria)){
            posX = porteriaFinal;
            //Verificar si se anoto gol
        } else if(!esGol && (posX>porteriaInicio && posX < porteriaFinal) && (posY > yMax + alturaPelota - alturaPorteria ||
                posY < alturaPorteria - alturaPelota)){
            esGol = true;
            if(posY < alturaPorteria - alturaPelota) {
                scoreA++;
                anoto = true;
            }
            else if(posY > yMax + alturaPelota - alturaPorteria){
                scoreB++;
                anoto = false;
            }
            //Mostrar dialogo con indicadores
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            if(anoto){
                builder.setTitle("Anostaste!!!").setMessage("Anoto el equipo A\n" + "Equipo A: " + scoreA + "\n\n Equipo B: " + scoreB);
            }
            else{
                builder.setTitle("Anostaste!!!").setMessage("Anoto el equipo B\n" + "Equipo A: " + scoreA + "\n\n Equipo B: " + scoreB);
            }
            builder.setPositiveButton("Seguir Jugando", (dialog, id) -> {
                dialog.dismiss();
                posX = ((xMax + anchoPelota) / 2) - (anchoPelota / 2);
                posY = ((yMax + anchoPelota) / 2) - (anchoPelota / 2);
                esGol = false;
            });

            //reinicio de valores y posiciones iniciales
            builder.setNegativeButton("Reiniciar Juego", (dialog, id) -> {
                scoreA = 0;
                scoreB = 0;
                posX = ((xMax + anchoPelota) / 2) - (anchoPelota / 2);
                posY = ((yMax + anchoPelota) / 2) - (anchoPelota / 2);
                esGol = false;
                dialog.dismiss();
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        //Verificar limites de la posicion en y
        if (posY > yMax) {
            posY = yMax;
        } else if (posY < 0) {
            posY = 0;
        }

    }




    private class MiVista extends View {

        public MiVista(Context context) {
            super(context);
        }
    }

}