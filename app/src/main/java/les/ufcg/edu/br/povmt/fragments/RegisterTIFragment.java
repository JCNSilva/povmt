package les.ufcg.edu.br.povmt.fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.honorato.multistatetogglebutton.MultiStateToggleButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import les.ufcg.edu.br.povmt.R;
import les.ufcg.edu.br.povmt.database.AtividadePersister;
import les.ufcg.edu.br.povmt.models.Atividade;
import les.ufcg.edu.br.povmt.models.TI;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterTIFragment extends DialogFragment {
    private final int HOJE = 0;
    private final int ONTEM = 1;
    public static final String PREFERENCE_NAME = "USER_PREFERENCE";
    private MultiStateToggleButton dia;
    private TextView horas;
    private Spinner atividade;
    private TextView nome_atividade;
    private MultiStateToggleButton categoria;
    private Spinner prioridade;
    private TextView tag;
    private Button cancel;
    private Button ok;
    private String nome;
    private Atividade atualizada = null;
    private Atividade novaAtividade;
    private TI tiNovaAtividade;

    private AtividadePersister listaAtividade = new AtividadePersister(getContext());
    SharedPreferences sharedPreferences = getContext().getSharedPreferences(PREFERENCE_NAME, getContext().MODE_PRIVATE);
    private ArrayList<Atividade> atividades = (ArrayList)listaAtividade.getAtividades(Long.parseLong(sharedPreferences.getString("USER_ID","")));

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register_ti, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        initViews(view);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataBase();
                dismiss();
            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }

    public void initViews(View view) {
        dia = (MultiStateToggleButton) view.findViewById(R.id.switch_dia);
        horas = (TextView) view.findViewById(R.id.ti_hours);

        atividade = (Spinner) view.findViewById(R.id.atividade_spinner);

        nome_atividade = (TextView) view.findViewById(R.id.nome_atv);
        categoria = (MultiStateToggleButton) view.findViewById(R.id.switch_categoria);
        prioridade = (Spinner) view.findViewById(R.id.prioridade_spinner);
        tag = (TextView) view.findViewById(R.id.tag);

        cancel = (Button) view.findViewById(R.id.btnCancel);
        ok = (Button) view.findViewById(R.id.btnOk);
        // cria a lista so com os nomes das atividades para o spinner
        List<String> nomesAtividades = new ArrayList<String>();
        for (Atividade ativ:atividades) {
         nomesAtividades.add(ativ.getNome());
        }
        nomesAtividades.add("Nova Atividade");
        //Ppopular spinner
        //ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.fragment_register_ti, nomesAtividades);
        //ArrayAdapter<String> spinnerArrayAdapter = arrayAdapter;
    }
    //metodo para selecionar do spinner
    public void onItemSelected(AdapterView<?> parent, View v, int posicao, long id) {
        nome = parent.getItemAtPosition(posicao).toString();
       if(nome.equals("Nova Atividade")){
           LinearLayout layoutActivity = (LinearLayout) getView().findViewById(R.id.layout_new_activity);
           layoutActivity.setVisibility(View.VISIBLE);
           saveNewData();
       }else{
           saveData(nome);
       }

    }
    //atualiza atividade pelo nome
    private void saveData(String nome) {

        // DIA
        String dia_db = getDate(dia.getValue());

        //HORAS
        int horas_db = Integer.parseInt(String.valueOf(horas.getText()));
        //TODO
        //Pegar demais dados jogar no construtor de Atividade e depois persistir com o AtividadePersister
        for (Atividade ativ:atividades) {
            if (ativ.getNome().equals(nome)){
                atualizada = ativ;
            }
        }
        Calendar cal = Calendar.getInstance();
        cal.set(Integer.parseInt(dia_db.split("/")[2]), Integer.parseInt(dia_db.split("/")[1]), Integer.parseInt(dia_db.split("/")[0]));
        TI tempoInvestido = new TI(cal ,horas_db,"");
        atualizada.addTI(tempoInvestido);

    }
     //cria nova ativiade
    private void saveNewData() {
        // DIA
        String dia_db = getDate(dia.getValue());

        //HORAS
        int horas_db = Integer.parseInt(String.valueOf(horas.getText()));
        //TODO
        //Pegar demais dados jogar no construtor de Atividade e depois persistir com o AtividadePersister
        //preencher o spinner de categoria
        //Atividade novaAtividade= new Atividade(nome_atividade.toString(), categoria.getTexts().toString(), "");
        Calendar cal = Calendar.getInstance();
        cal.set(Integer.parseInt(dia_db.split("/")[2]), Integer.parseInt(dia_db.split("/")[1]), Integer.parseInt(dia_db.split("/")[0]));
        TI tiNovaAtividade = new TI(cal,  horas_db,"");
        novaAtividade.addTI(tiNovaAtividade);

    }
    //salva no db
    private void dataBase() {
        AtividadePersister atividadepersistida = new AtividadePersister(getContext());
       if(!atualizada.equals(null)){
           atividadepersistida.atualizarAtividade(atualizada, Long.parseLong(sharedPreferences.getString("USER_ID","")));
       }else{
           atividadepersistida.inserirAtividade(novaAtividade, Long.parseLong(sharedPreferences.getString("USER_ID","")));
       }
    }


    private String getDate(int day) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Calendar  cal = Calendar.getInstance();
        String data_completa = null;

        if (day == HOJE) {
            Date data = new Date();
            cal.setTime(data);
            Date data_atual = cal.getTime();
            data_completa = dateFormat.format(data_atual);
        } else if (day == ONTEM) {
            cal.add(Calendar.DATE, -1);
            Date data_atual = cal.getTime();
            data_completa = dateFormat.format(data_atual);
        }

        return data_completa;
    }
}
