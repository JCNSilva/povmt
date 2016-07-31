package les.ufcg.edu.br.povmt.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TimePicker;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import les.ufcg.edu.br.povmt.R;
import les.ufcg.edu.br.povmt.activities.MainActivity;
import les.ufcg.edu.br.povmt.activities.SplashActivity;
import les.ufcg.edu.br.povmt.database.DataSource;
import les.ufcg.edu.br.povmt.models.Atividade;
import les.ufcg.edu.br.povmt.models.TI;

/**
 * Created by Veruska on 27/07/2016.
 */
public class ConfigurationsFragment extends Fragment{
    public static final String PREFERENCE_NAME = "NOTF_ACTIVATED";
    public static final String NOTIFICATION = "ACTIVATED";
    public static final String HOUR = "HOUR";
    public static final String MINUTE = "MINUTE";
    private SharedPreferences.Editor editor;
    private boolean notificacaoAtiva = false;
    private Switch notificacao;
    private int horaNotificacao = 6;
    private int minutoNotificacao = 00;
    private TimePicker horario;
    private Button ok;
    private Button cancel;
    public static final String ACTION = "com.example.android.receivers.NOTIFICATION_ALARM";
    private SharedPreferences sharedPreferences;
    private MainActivity mainActivity;

    public ConfigurationsFragment(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_configuration, container, false);

        sharedPreferences = this.getActivity().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        initView(view);

        notificacao.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                setNotificacaoAtiva(isChecked);
                editor.putBoolean(NOTIFICATION, isChecked);
                if (isChecked) {
                    horario.setEnabled(isChecked);
                    mainActivity.refresh();
                } else if (!isChecked) {
                    mainActivity.cancelAlarm();
                    horario.setEnabled(isChecked);
                }
                editor.apply();
                editor.commit();
            }
        });

        horario.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                setHoraNotificacao(hourOfDay);
                setMinutoNotificacao(minute);
                editor.putInt(HOUR, hourOfDay);
                editor.putInt(MINUTE, minute);
                editor.apply();
                editor.commit();
                mainActivity.refresh();
            }
        });
        return view;
    }

    private void initView(View view){
        notificacao = (Switch) view.findViewById(R.id.switchNotificar);
        horario = (TimePicker) view.findViewById(R.id.timePickerHora);
        ok = (Button) view.findViewById(R.id.btnOk);
        cancel = (Button) view.findViewById(R.id.btnCancel);
        horario.setEnabled(false);

        setViews();
    }

    private void setViews() {
        boolean isChecked = sharedPreferences.getBoolean(NOTIFICATION, false);
        int hour = sharedPreferences.getInt(HOUR, 0);
        int minute = sharedPreferences.getInt(MINUTE, 0);
        horario.setHour(hour);
        horario.setMinute(minute);
        notificacao.setChecked(isChecked);
        setNotificacaoAtiva(isChecked);
        if (notificacao.isChecked()) {
            horario.setEnabled(true);
        }
    }

    public void setNotificacaoAtiva(boolean notificacaoAtiva) {
        this.notificacaoAtiva = notificacaoAtiva;
    }

    public boolean isNotificacaoAtiva(){
        return notificacaoAtiva;
    }

    public int getHoraNotificacao() {
        return horaNotificacao;
    }

    public void setHoraNotificacao(int horaNotificacao) {
        this.horaNotificacao = horaNotificacao;
    }

    public int getMinutoNotificacao() {
        return minutoNotificacao;
    }

    public void setMinutoNotificacao(int minutoNotificacao) {
        this.minutoNotificacao = minutoNotificacao;
    }
}
