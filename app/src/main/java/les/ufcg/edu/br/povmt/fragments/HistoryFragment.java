package les.ufcg.edu.br.povmt.fragments;


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
import les.ufcg.edu.br.povmt.database.AtividadePersister;
import les.ufcg.edu.br.povmt.models.Atividade;
import les.ufcg.edu.br.povmt.utils.AtividadeAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {


    private AtividadePersister atividadePersister;
    private ArrayList atividades;
    private int idUser;
    private RecyclerView lista_atividades1;
    private TextView horas_investidas1;
    private RecyclerView lista_atividades2;
    private TextView horas_investidas2;
    private RecyclerView lista_atividades3;
    private TextView horas_investidas3;
    private RecyclerView lista_atividades;
    private AtividadeAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

//        idUser = 123456;
//        atividadePersister = new AtividadePersister(getContext());
//        atividades = (ArrayList) atividadePersister.getAtividades(idUser);
//
//
//        LinearLayoutManager llm = new LinearLayoutManager(getContext());
//        lista_atividades.setLayoutManager(llm);
//
//        atividadePersister = new AtividadePersister(getContext());
//        atividades = (ArrayList) atividadePersister.getAtividades(idUser);
//        adapter = new AtividadeAdapter(new ArrayList<Atividade>(atividades));
//        lista_atividades.setAdapter(adapter);

        initViews(view);
        return view;
    }

    private void initViews(View view) {
        //Semana 1
        lista_atividades1 = (RecyclerView) view.findViewById(R.id.rview_atividades1);
        horas_investidas1 = (TextView) view.findViewById(R.id.tv_horasinv_semana1);

        //Semana 1
        lista_atividades2 = (RecyclerView) view.findViewById(R.id.rview_atividades2);
        horas_investidas2 = (TextView) view.findViewById(R.id.tv_horasinv_semana2);

        //Semana 1
        lista_atividades3 = (RecyclerView) view.findViewById(R.id.rview_atividades3);
        horas_investidas3 = (TextView) view.findViewById(R.id.tv_horasinv_semana3);
    }


//    @Override
//    public void onResume() {
//        super.onResume();
////        atividadePersister = new AtividadePersister(getContext());
////        atividades = (ArrayList) atividadePersister.getAtividades(idUser);
////        adapter = new AtividadeAdapter(new ArrayList<Atividade>(atividades));
////        lista_atividades.setAdapter(adapter);
////
////        //TODO Calcular TI na Semana
////        horas_investidas.setText(String.valueOf(getHorasInvestidas()) + " " + getString(R.string.horas_investidas));
//
//    }
//
//    @Override
//    public void atualizaLista() {
//        onResume();
//    }

    private int getHorasInvestidas() {
        int horasInvestidas = 0;
        List<Atividade> atividadesList = adapter.getmAtividades();
        for (int i = 0; i < atividadesList.size(); i++) {
            horasInvestidas += atividadesList.get(i).getTI();
        }
        return horasInvestidas;
    }
}
