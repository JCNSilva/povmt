package les.ufcg.edu.br.povmt.utils;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.TaskStackBuilder;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import org.joda.time.Days;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

import les.ufcg.edu.br.povmt.R;
import les.ufcg.edu.br.povmt.activities.MainActivity;
import les.ufcg.edu.br.povmt.activities.SplashActivity;
import les.ufcg.edu.br.povmt.database.DataSource;
import les.ufcg.edu.br.povmt.fragments.ConfigurationsFragment;
import les.ufcg.edu.br.povmt.models.Atividade;
import les.ufcg.edu.br.povmt.models.TI;

/**
 * Created by treinamento-09 on 28/07/16.
 */
public class PovmtBroadcast extends BroadcastReceiver {
    private String idUser;
    private SharedPreferences sharedPreferences;
    private DataSource dataSource;
    private ArrayList atividades;
    private boolean isNotTISCadastrada;

    @Override
    public void onReceive(Context context, Intent intent) {
        isNotTISCadastrada = false;
        verificarCadastroDeTI(context);
        Log.d("Alarm Receiver", "onReceive called");
        if (intent.getAction().equals(MainActivity.ACTION)) {
            if (isNotTISCadastrada()) {
                Log.d("Alarm Receiver", "Sem TI");
                NotificationCompat.Builder notificationBuilder =
                        (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.logo)
                                .setContentTitle("POvMAT")
                                .setSmallIcon(R.drawable.logo, 4)

                                .setContentText("Ei! Você não cadastrou nenhuma TI ontem!");
                Intent resultIntent = new Intent(context, MainActivity.class);

                Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(1000);

                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addParentStack(MainActivity.class);
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                notificationBuilder.setContentIntent(resultPendingIntent);
                NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(1, notificationBuilder.build());
            }
        }
    }

    public void verificarCadastroDeTI(Context context) {
        sharedPreferences = context.getSharedPreferences(SplashActivity.PREFERENCE_NAME, Context.MODE_PRIVATE);
        idUser = sharedPreferences.getString(SplashActivity.USER_ID, "");
        dataSource = DataSource.getInstance(context);
        atividades = (ArrayList) dataSource.getAtividades(idUser);
        List<Atividade> listAtividades = new ArrayList<Atividade>(atividades);
        LocalDateTime hoje = new LocalDateTime().now();
        DateTimeFormatter format = DateTimeFormat.forPattern("dd/MM/yyyy");

        if (listAtividades.isEmpty()) {
            setNotTISCadastrada(true);
            return;
        } else {
            for (Atividade atividade : listAtividades) {
                List<TI> tis = atividade.getTiList();
                String data = tis.get(tis.size() - 1).getData();
                int diferenca = Days.daysBetween(hoje, LocalDateTime.parse(data, format)).getDays();
                if (diferenca >= 1 || tis.isEmpty()) {
                    Log.d("Verificado:", "Você não cadastrou ontem ou não tem atividade!");
                    setNotTISCadastrada(true);
                    return;
                }
            }
        }
    }

    public boolean isNotTISCadastrada() {
        return isNotTISCadastrada;
    }

    public void setNotTISCadastrada(boolean notTISCadastrada) {
        isNotTISCadastrada = notTISCadastrada;
    }
}
