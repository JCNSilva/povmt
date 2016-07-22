package les.ufcg.edu.br.povmt.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import les.ufcg.edu.br.povmt.R;
import les.ufcg.edu.br.povmt.activities.SplashActivity;
import les.ufcg.edu.br.povmt.database.AtividadePersister;
import les.ufcg.edu.br.povmt.database.TIPersister;
import les.ufcg.edu.br.povmt.models.Atividade;
import les.ufcg.edu.br.povmt.utils.AtividadeAdapter;
import les.ufcg.edu.br.povmt.utils.IonResume;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements IonResume {


    private String idUser;
    private AtividadePersister atividadePersister;
    private ArrayList atividades;
    private RecyclerView listaAtividades;
    private TextView listaVazia;
    private LinearLayout campoAtividades;
    private TextView horasInvestidas;
    private TIPersister tiPersister;
    public static AtividadeAdapter adapter;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        sharedPreferences = getContext().getSharedPreferences(SplashActivity.PREFERENCE_NAME, Context.MODE_PRIVATE);
        idUser = sharedPreferences.getString(SplashActivity.USER_ID, "");
//        idUser = 123456;
        atividadePersister = new AtividadePersister(getContext());
        atividades = (ArrayList) atividadePersister.getAtividades(idUser);
        listaAtividades = (RecyclerView) view.findViewById(R.id.rview_atividades);
        campoAtividades = (LinearLayout) view.findViewById(R.id.ll_atividades);
        listaVazia = (TextView) view.findViewById(R.id.sem_ti);
        horasInvestidas = (TextView) view.findViewById(R.id.tv_horasinv_semana);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        listaAtividades.setLayoutManager(llm);

        atividadePersister = new AtividadePersister(getContext());
        atividades = (ArrayList) atividadePersister.getAtividades(idUser);
        adapter = new AtividadeAdapter(new ArrayList<Atividade>(atividades));
        listaAtividades.setAdapter(adapter);

        if (atividades.isEmpty()) {
            listaVazia.setVisibility(View.VISIBLE);
            campoAtividades.setVisibility(View.GONE);
        } else {
            listaVazia.setVisibility(View.GONE);
            campoAtividades.setVisibility(View.VISIBLE);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        atividadePersister = new AtividadePersister(getContext());
        atividades = (ArrayList) atividadePersister.getAtividades(idUser);
        adapter = new AtividadeAdapter(new ArrayList<Atividade>(atividades));
        listaAtividades.setAdapter(adapter);

        //TODO Calcular TI na Semana
        horasInvestidas.setText(String.valueOf(getHorasInvestidas()) + " " + getString(R.string.horas_investidas));

    }

    @Override
    public void atualizaLista() {
        onResume();
    }

    private int getHorasInvestidas() {
        int horasInvestidas = 0;
        List<Atividade> atividadesList = adapter.getmAtividades();
        for (int i = 0; i < atividadesList.size(); i++) {
            horasInvestidas += atividadesList.get(i).getTI();
        }
        return horasInvestidas;
    }
}
