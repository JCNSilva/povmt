package les.ufcg.edu.br.povmt.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import les.ufcg.edu.br.povmt.R;
import les.ufcg.edu.br.povmt.database.AtividadePersister;
import les.ufcg.edu.br.povmt.database.TIPersister;
import les.ufcg.edu.br.povmt.models.Atividade;
import les.ufcg.edu.br.povmt.utils.AtividadeAdapter;
import les.ufcg.edu.br.povmt.utils.HomeListAdapter;
import les.ufcg.edu.br.povmt.utils.IonResume;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements IonResume {


    private long idUser;
    private AtividadePersister atividadePersister;
    private ArrayList atividades;
    private RecyclerView lista_atividades;
    private TextView lista_vazia;
    private LinearLayout campo_atividades;
    private TextView horas_investidas;
    private TIPersister tiPersister;
    public static AtividadeAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        idUser = 123456;
        atividadePersister = new AtividadePersister(getContext());
        atividades = (ArrayList) atividadePersister.getAtividades(idUser);
        lista_atividades = (RecyclerView) view.findViewById(R.id.rview_atividades);
        campo_atividades = (LinearLayout) view.findViewById(R.id.ll_atividades);
        lista_vazia = (TextView) view.findViewById(R.id.sem_ti);
        horas_investidas = (TextView) view.findViewById(R.id.tv_horasinv_semana);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        lista_atividades.setLayoutManager(llm);

        atividadePersister = new AtividadePersister(getContext());
        atividades = (ArrayList) atividadePersister.getAtividades(idUser);
        adapter = new AtividadeAdapter(new ArrayList<Atividade>(atividades));
        lista_atividades.setAdapter(adapter);

        if (atividades.isEmpty()) {
            lista_vazia.setVisibility(View.VISIBLE);
            campo_atividades.setVisibility(View.GONE);
        } else {
            lista_vazia.setVisibility(View.GONE);
            campo_atividades.setVisibility(View.VISIBLE);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        atividadePersister = new AtividadePersister(getContext());
        atividades = (ArrayList) atividadePersister.getAtividades(idUser);
        adapter = new AtividadeAdapter(new ArrayList<Atividade>(atividades));
        lista_atividades.setAdapter(adapter);

        //TODO Calcular TI na Semana
        horas_investidas.setText(String.valueOf(getHorasInvestidas()));

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
