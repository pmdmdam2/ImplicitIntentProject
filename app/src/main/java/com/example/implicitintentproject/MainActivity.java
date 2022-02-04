package com.example.implicitintentproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.provider.CalendarContract;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Ejemplos de intent implícitos: compartir texto con otras aplicaciones,
 * crear una alarma, crear una entrada en el calendario y enviar un correo
 */
public class MainActivity extends AppCompatActivity {
    private final int REQUEST_CODE_CALENDAR_PERMISSION = 10;
    private final int REQUEST_CODE_ALARM_PERMISSION = 11;
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btSendText = findViewById(R.id.btSendText);
        btSendText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*addEvent(getString(R.string.entrada_calendario), "Aula 2º DAM",
                       System.currentTimeMillis() + 60000,
                System.currentTimeMillis() + 180000);*/
            }
        });
    }
    /**
     * Método de callback que es llamado después de requerir un permiso
     * @param requestCode Código de la petición, se usa para saber qué tipo de petición
     *                    se realizó y poder lanzar el intent correspondiente
     * @param permissions Permisos requeridos en la petición
     * @param grantResults Permisos concedidos de los permisos requeridos
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_CALENDAR_PERMISSION: {
                // si la solicitud del permiso se ha cancelado el array de resultados estará vacío
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    // permission concedido, se puede realizar la operación relacionada
                    //intent = new Intent(Intent.ACTION_INSERT);
                    //se comprueba si el dispositivo tiene posibilidad de realizar la acción solicitada por el intent
                    if (intent.resolveActivity(this.getPackageManager()) != null)
                        this.startActivity(intent);
                } else {
                    // permiso denegado
                    Toast.makeText(this,
                            R.string.permiso_agenda_denegado,Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
    public void getPermission(String permission, int requestCode){
        if (ContextCompat.checkSelfPermission(this, permission) !=
                PackageManager.PERMISSION_GRANTED) {
            /*2. Se comprueba si el permiso fue rechazado, devuelve true
            si el usuario rechazó la solicitud anteriormente y muestra
            false si un usuario rechazó un permiso y seleccionó la opción
            No volver a preguntar en el diálogo de solicitud de permiso, o
            si una política de dispositivo lo prohíbe*/
            if (ActivityCompat.
                    shouldShowRequestPermissionRationale(this,
                            permission)) {
                //Se informa al usuario de que no es posible ejecuta la acción solicitada
                // porque lo prohibe el dispositivo o porque se ha rechazado la solicitud.
                Toast.makeText(this, "explicación del permiso",
                        Toast.LENGTH_LONG).show();
            } else {
                //3. se requiere el permiso
                ActivityCompat.requestPermissions((AppCompatActivity) this,
                        new String[]{permission},
                        requestCode);
            }
        } else {
            //el permiso ya ha sido concedido, se llama al intent implícito
            // encargado de la marcación telefónica.
            //se comprueba si el dispositivo tiene posibilidad de realizar
            // la acción solicitada por el intent
            if (intent.
                    resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }
    }
    /**
     * Método para añadir una entrada en el calendario. Esta acción necesita un permiso
     * especial en el manifiesto
     * @param title Título de la entrada en el calendario
     * @param location Localización del evento del calendario
     * @param begin Fecha y hora de comienzo del evento
     * @param end Fecha y hora de finalización del evento
     */
    public void addEvent(String title, String location, long begin, long end) {
        intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, title)
                .putExtra(CalendarContract.Events.EVENT_LOCATION, location)

                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, begin)
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, end);
        this.getPermission(Manifest.permission.WRITE_CALENDAR,
                REQUEST_CODE_CALENDAR_PERMISSION);
    }
    /**
     * Método para crear una alarma en Android, con un texto asociado
     * a una hora y minutos especificados, es necesario un permiso específico en el manifiesto
     * @param message Mensaje de la alarma
     * @param hour Hora de la alarma
     * @param minutes Minutos de la alarma
     */
    public void createAlarm(String message, int hour, int minutes) {
        this.intent = new
                Intent(AlarmClock.ACTION_SET_ALARM)
                .putExtra(AlarmClock.EXTRA_MESSAGE, message)
                .putExtra(AlarmClock.EXTRA_HOUR, hour)
                .putExtra(AlarmClock.EXTRA_MINUTES, minutes);

        this.getPermission(Manifest.permission.SET_ALARM,
                REQUEST_CODE_ALARM_PERMISSION);
    }
    /**
     * Método para enviar un mensaje de texto tipo ShareSheet. Responderán a la intención
     * aquellas aplicaciones instaladas que puedan realizar el envío del
     * mensaje de texto. Similar a compartir la URL desde el navegador
     */
    public void shareMessage() {
        /*
        El uso más simple y común de Android Sharesheet es el envío de
        contenido de texto desde una actividad a otra. Por ejemplo, en la
        mayoría de los navegadores, se puede compartir la URL de la página
        que se muestra como texto mediante otra app. Este recurso es útil
        para compartir un artículo o un sitio web con amigos por correo electrónico
        o redes sociales. Este es un ejemplo de cómo hacerlo
         */
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.texto_compartir));
        sendIntent.setType("text/plain");
        this.intent = Intent.createChooser(sendIntent, null);

        if (this.intent.resolveActivity(getPackageManager()) != null)
            startActivity(this.intent);
    }

    /**
     * Envío de correo electrónico
     * @param addresses Dirección de los destinatarios
     * @param subject Asunto
     * @param attachment Fichero adjuntos
     */
    public void composeEmail(String[] addresses, String subject, Uri attachment) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_STREAM, attachment);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}