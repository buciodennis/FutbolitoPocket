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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;

public class MainActivity extends AppCompatActivity implements SensorEventListener {


    //x
    private float velocidadX = 0.0f, posX, xMax, acelerometroX;
    //y
    private float velocidadY = 0.0f, posY, yMax, acelerometroY;

    private float porteriaInicio, porteriaFinal;
    private float frameTime = 0.666f;
    private final int anchoPelota = 130, anchoPorteria = 330;
    private final int alturaPelota = 130, alturaPorteria = 300;
    private int scoreA = 0, scoreB = 0;
    private boolean esGol = false;

    private Bitmap pelota, porteriaA, porteriaB, cancha;
    DisplayMetrics metrics; int width = 0, height = 0;

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

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorACCELEROMETER = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER && !esGol) {
            acelerometroX = sensorEvent.values[0];
            acelerometroY = -sensorEvent.values[1];
            actualizarPosicion();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(sensorACCELEROMETER!=null){
            sensorManager.registerListener(this, sensorACCELEROMETER, SensorManager.SENSOR_DELAY_GAME);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(sensorACCELEROMETER!=null){
            sensorManager.unregisterListener(this);
        }

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
            metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            height = metrics.heightPixels;
            width = metrics.widthPixels;

            //Creacion de la pelota
            Bitmap pelotaSrc = BitmapFactory.decodeResource(getResources(), R.drawable.pelota);
            pelota = Bitmap.createScaledBitmap(pelotaSrc, anchoPelota, alturaPelota, true);

            //Creacion de las porterias
            Bitmap porteriaSrcS = BitmapFactory.decodeResource(getResources(), R.drawable.porteria_up);
            porteriaA = Bitmap.createScaledBitmap(porteriaSrcS, anchoPorteria, alturaPorteria, true);
            Bitmap porteriaSrcI = BitmapFactory.decodeResource(getResources(), R.drawable.porteria);
            porteriaB = Bitmap.createScaledBitmap(porteriaSrcI, anchoPorteria, alturaPorteria, true);

            //Creacion del fondo
            Bitmap canchaSrc = BitmapFactory.decodeResource(getResources(), R.drawable.cancha);
            cancha = Bitmap.createScaledBitmap(canchaSrc, width, height-50, true);
            Log.d("Tamaños", height + " " + width);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawBitmap(cancha, 0, -50, null);


            //Dibujar porterias
            canvas.drawBitmap(porteriaA, porteriaInicio, -50, null);
            canvas.drawBitmap(porteriaB, porteriaInicio, yMax-30 + alturaPelota - 200, null);

            //Dibujar pelota
            canvas.drawBitmap(pelota, posX, posY, null);

            //Dibujar cancha
            canvas.drawBitmap(pelota, posX, posY, null);

            invalidate();
        }
    }

}