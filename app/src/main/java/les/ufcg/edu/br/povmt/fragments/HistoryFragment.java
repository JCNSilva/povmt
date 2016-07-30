package les.ufcg.edu.br.povmt.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import les.ufcg.edu.br.povmt.R;
import les.ufcg.edu.br.povmt.activities.SplashActivity;
//import les.ufcg.edu.br.povmt.database.AtividadePersister;
import les.ufcg.edu.br.povmt.database.DataSource;
import les.ufcg.edu.br.povmt.models.Atividade;
import les.ufcg.edu.br.povmt.utils.AtividadeAdapter;
import les.ufcg.edu.br.povmt.utils.HistoryAdapter;
import les.ufcg.edu.br.povmt.utils.IonResume;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment implements IonResume {


    private DataSource atividadePersister;
    private ArrayList atividades;
    private String idUser;
    private RecyclerView listaAtividades1;
    private TextView horasInvestidas1;
    private RecyclerView listaAtividades2;
    private TextView horasInvestidas2;
    private RecyclerView listaAtividades3;
    private TextView horasInvestidas3;
    private RecyclerView listaAtividades;
    private AtividadeAdapter adapter;
    private SharedPreferences sharedPreferences;
    private HistoryAdapter adapter1;
    private HistoryAdapter adapter2;
    private HistoryAdapter adapter3;
    private LinearLayoutManager llm;
    private LinearLayoutManager llm1;
    private DataSource dataSource;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        atividadePersister = DataSource.getInstance(getContext());

        sharedPreferences = getContext().getSharedPreferences(SplashActivity.PREFERENCE_NAME, Context.MODE_PRIVATE);
        idUser = sharedPreferences.getString(SplashActivity.USER_ID, "");

        initViews(view);

        Log.d("Script", idUser);

        dataSource = DataSource.getInstance(getContext());
        atividadePersister = dataSource;
        atividades = (ArrayList) atividadePersister.getAtividades(idUser);

        adapter1 = new HistoryAdapter(getContext(), new ArrayList<Atividade>(atividades), getSemanaAtual());
        adapter2 = new HistoryAdapter(getContext(), new ArrayList<Atividade>(atividades), getSemanaPassada());
        adapter3 = new HistoryAdapter(getContext(), new ArrayList<Atividade>(atividades), getSemanaRetrasada());

        listaAtividades1.setAdapter(adapter1);
        listaAtividades2.setAdapter(adapter2);
        listaAtividades3.setAdapter(adapter3);


        return view;
    }

    private void initViews(View view) {
        //Semana 1
        listaAtividades1 = (RecyclerView) view.findViewById(R.id.rview_atividades1);
        horasInvestidas1 = (TextView) view.findViewById(R.id.tv_horasinv_semana1);
        llm1 = new LinearLayoutManager(getContext());
        listaAtividades1.setLayoutManager(llm1);

        //Semana 1
        listaAtividades2 = (RecyclerView) view.findViewById(R.id.rview_atividades2);
        horasInvestidas2 = (TextView) view.findViewById(R.id.tv_horasinv_semana2);
        LinearLayoutManager llm2 = new LinearLayoutManager(getContext());
        listaAtividades2.setLayoutManager(llm2);

        //Semana 1
        listaAtividades3 = (RecyclerView) view.findViewById(R.id.rview_atividades3);
        horasInvestidas3 = (TextView) view.findViewById(R.id.tv_horasinv_semana3);
        LinearLayoutManager llm3 = new LinearLayoutManager(getContext());
        listaAtividades3.setLayoutManager(llm3);
    }


    @Override
    public void onResume() {
        super.onResume();

//        atividadePersister = AtividadePersister.getInstance(getContext());
        dataSource = DataSource.getInstance(getContext());
        atividades = (ArrayList) atividadePersister.getAtividades(idUser);

        adapter1 = new HistoryAdapter(getContext(), new ArrayList<Atividade>(atividades), getSemanaAtual());
        adapter2 = new HistoryAdapter(getContext(), new ArrayList<Atividade>(atividades), getSemanaPassada());
        adapter3 = new HistoryAdapter(getContext(), new ArrayList<Atividade>(atividades), getSemanaRetrasada());

        listaAtividades1.setAdapter(adapter1);
        listaAtividades2.setAdapter(adapter2);
        listaAtividades3.setAdapter(adapter3);

        horasInvestidas1.setText(String.valueOf(getHorasInvestidas(getSemanaAtual())) + " " + getString(R.string.horas_investidas));
        horasInvestidas2.setText(String.valueOf(getHorasInvestidas(getSemanaPassada())) + " " + getString(R.string.horas_investidas));
        horasInvestidas3.setText(String.valueOf(getHorasInvestidas(getSemanaRetrasada())) + " " + getString(R.string.horas_investidas));

    }

    private int getHorasInvestidas(int semana) {
        int horasInvestidas = 0;
        List<Atividade> atividadesList = new ArrayList<>();

        if (semana == getSemanaAtual()) {
            atividadesList = adapter1.getmAtividades();
        } else if (semana == getSemanaPassada()) {
            atividadesList = adapter2.getmAtividades();
        } else if (semana == getSemanaRetrasada()) {
            atividadesList = adapter3.getmAtividades();
        }

        for (int i = 0; i < atividadesList.size(); i++) {
            horasInvestidas += atividadesList.get(i).getTI();
        }
        return horasInvestidas;
    }

    private int getSemanaAtual(){
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.WEEK_OF_YEAR);
    }

    private int getSemanaPassada(){
        return getSemanaAtual() - 1;
    }

    private int getSemanaRetrasada(){
        return getSemanaAtual() - 2;
    }

    @Override
    public void atualizaLista() {
        onResume();
    }
}
