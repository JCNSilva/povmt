package les.ufcg.edu.br.povmt.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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

import java.util.Calendar;

import les.ufcg.edu.br.povmt.R;

/**
 * Created by Veruska on 27/07/2016.
 */
public class ConfigurationsFragment extends Fragment{

    private boolean notificacaoAtiva = true;
    private Switch notificacao;
    private int horaNotificacao = 21;
    private int minutoNotificacao = 47;
    private TimePicker horario;
    private Button ok;
    private Button cancel;

    public ConfigurationsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_configuration, container, false);

        initView(view);

        notificacao.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                setNotificacaoAtiva(isChecked);
                if (isChecked) {

                    horario.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                        public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                            setHoraNotificacao(hourOfDay);
                            setMinutoNotificacao(minute);
                        }
                    });
                }
            }

        });
        return view;
    }

    private void initView(View view){

        notificacao = (Switch) view.findViewById(R.id.switchNotificar);
        horario = (TimePicker) view.findViewById(R.id.timePickerHora);
        ok = (Button) view.findViewById(R.id.btnOk);
        cancel = (Button) view.findViewById(R.id.btnCancel);
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
