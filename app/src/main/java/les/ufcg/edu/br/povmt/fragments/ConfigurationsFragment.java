package les.ufcg.edu.br.povmt.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;

import les.ufcg.edu.br.povmt.R;

/**
 * Created by Veruska on 27/07/2016.
 */
public class ConfigurationsFragment extends Fragment{
    public static boolean notificacaoAtiva = true;

    public ConfigurationsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_configuration, container, false);
    }

    public static void setNotificacaoAtiva(boolean notificacaoAtiva) {
        ConfigurationsFragment.notificacaoAtiva = notificacaoAtiva;
    }
}
