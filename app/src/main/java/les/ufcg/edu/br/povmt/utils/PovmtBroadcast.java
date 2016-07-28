package les.ufcg.edu.br.povmt.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import les.ufcg.edu.br.povmt.R;
import les.ufcg.edu.br.povmt.activities.MainActivity;

/**
 * Created by treinamento-09 on 28/07/16.
 */
public class PovmtBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(MainActivity.ACTION)) {
            Log.d("Alarm Receiver", "onReceive called");
            NotificationCompat.Builder notificationBuilder =
                    (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.logo)
                            .setContentTitle("POvMAT")
                            .setSmallIcon(R.drawable.logo, 4)

                            .setContentText("Ei! Você não cadastrou nenhuma TI ontem!");
            Intent resultIntent = new Intent(context, MainActivity.class);

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
