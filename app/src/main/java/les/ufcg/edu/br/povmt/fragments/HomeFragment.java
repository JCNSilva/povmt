package les.ufcg.edu.br.povmt.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import les.ufcg.edu.br.povmt.R;
import les.ufcg.edu.br.povmt.database.AtividadePersister;
import les.ufcg.edu.br.povmt.database.TIPersister;
import les.ufcg.edu.br.povmt.models.Atividade;
import les.ufcg.edu.br.povmt.utils.HomeListAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    private long idUser;
    private AtividadePersister atividadePersister;
    private ArrayList atividades;
    private ListView lista_atividades;
    private TextView lista_vazia;
    private LinearLayout tabela;
    private TIPersister tiPersister;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        idUser = 123456;

        initViews(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        atividadePersister = new AtividadePersister(getContext());
        atividades = (ArrayList) atividadePersister.getAtividades(idUser);

        updateUI();

        HomeListAdapter adapter = new HomeListAdapter(getContext(), atividades);
        lista_atividades.setAdapter(adapter);
    }

    private void updateUI() {
        if (atividades.isEmpty()) {
            lista_vazia.setVisibility(View.VISIBLE);
            tabela.setVisibility(View.GONE);
        } else {
            lista_vazia.setVisibility(View.GONE);
            tabela.setVisibility(View.VISIBLE);
        }
    }
    public void initViews(View view){
        lista_atividades = (ListView) view.findViewById(R.id.lista_atividades);
        lista_vazia = (TextView) view.findViewById(R.id.sem_ti);
        tabela = (LinearLayout) view.findViewById(R.id.tabela);
    }
}
