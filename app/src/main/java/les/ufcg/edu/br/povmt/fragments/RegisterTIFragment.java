package les.ufcg.edu.br.povmt.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import android.widget.Toast;

import org.honorato.multistatetogglebutton.MultiStateToggleButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import les.ufcg.edu.br.povmt.R;
import les.ufcg.edu.br.povmt.activities.SplashActivity;
import les.ufcg.edu.br.povmt.database.AtividadePersister;
import les.ufcg.edu.br.povmt.database.TIPersister;
import les.ufcg.edu.br.povmt.models.Atividade;
import les.ufcg.edu.br.povmt.models.Categoria;
import les.ufcg.edu.br.povmt.models.InputException;
import les.ufcg.edu.br.povmt.models.Prioridade;
import les.ufcg.edu.br.povmt.models.TI;
import les.ufcg.edu.br.povmt.utils.IonResume;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterTIFragment extends DialogFragment {
    private static final int INVALIDO = -1;

    private static final int TRABALHO = 0;
    private static final int LAZER = 1;

    private static final int ATUALIZAR = 0;
    private static final int INSERIR = 1;

    private static final int HOJE = 0;
    private static final int ONTEM = 1;

    private static final int BAIXA = 0;
    private static final int MEDIA = 1;
    private static final int ALTA = 2;

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

    private int horas_db;
    private String nomeAtividade_db;
    private String tagAtividade_db;
    private String atividade_escolhida;

    private ArrayList<Atividade> atividades;
    private AtividadePersister atividadePersister;
    private LinearLayout layout_new_activity;
    private Atividade mAtividade;
    private SharedPreferences sharedPreferences;
    private long idUser;
    private TIPersister tiPersister;
    private IonResume homeFragment;

    public RegisterTIFragment(IonResume homeFragment) {
        this.homeFragment = homeFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register_ti, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        sharedPreferences = getContext().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        idUser = sharedPreferences.getLong(SplashActivity.USER_ID, 0);
//        idUser = 123456;
        atividadePersister = new AtividadePersister(getContext());
        atividades = (ArrayList<Atividade>) atividadePersister.getAtividades(idUser);

        tiPersister = new TIPersister(getContext());

        initViews(view);
        listeners();

        atividades.add(new Atividade("Nova Atividade"));
        ArrayAdapter<Atividade> adapter =
                new ArrayAdapter<Atividade>(getContext(),
                android.R.layout.simple_spinner_item, atividades);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        atividade.setAdapter(adapter);
        return view;
    }

    private void listeners() {
        atividade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mAtividade = (Atividade) adapterView.getSelectedItem();

                if (mAtividade.toString().equals(getString(R.string.nova_atividade))) {
                    layout_new_activity.setVisibility(View.VISIBLE);
                } else {
                    layout_new_activity.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    prepareData();
                    homeFragment.atualizaLista();
                    dismiss();
                }catch(InputException e){
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void initViews(View view) {
        boolean[] states = {true, false};

        dia = (MultiStateToggleButton) view.findViewById(R.id.switch_dia);
        dia.setStates(states);
        horas = (TextView) view.findViewById(R.id.ti_hours);

        atividade = (Spinner) view.findViewById(R.id.atividade_spinner);

        nome_atividade = (TextView) view.findViewById(R.id.nome_atv);
        categoria = (MultiStateToggleButton) view.findViewById(R.id.switch_categoria);
        categoria.setStates(states);
        prioridade = (Spinner) view.findViewById(R.id.prioridade_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.prioridades, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioridade.setAdapter(adapter);
        tag = (TextView) view.findViewById(R.id.tag);

        cancel = (Button) view.findViewById(R.id.btnCancel);
        ok = (Button) view.findViewById(R.id.btnOk);

        layout_new_activity = (LinearLayout) view.findViewById(R.id.layout_new_activity);
    }

    //Prepara dados para BD
    private void prepareData() throws InputException {
        String dia_db = getDate(dia.getValue());
        int semana_db = getWeek(dia_db);
        horas_db = Integer.parseInt(String.valueOf(horas.getText()));
        if( horas_db <= 0 || horas_db > 24){
            throw new InputException("Hora deve ser um valor entre 0 e 24");
        }

        TI ti_db = new TI(dia_db, semana_db, horas_db);
        Atividade atividade_db = null;
        int operation = INVALIDO;

        atividade_escolhida = atividade.getSelectedItem().toString();
        if (atividade_escolhida.equals(getString(R.string.nova_atividade))) {
            nomeAtividade_db = nome_atividade.getText().toString();
            if(nomeAtividade_db.trim().equals("")){
                throw new InputException("Nome da Atividade Inválido!");
            }
            Categoria categoria_db = null;
            if(categoria.getValue() == TRABALHO) {
                categoria_db = Categoria.TRABALHO;
            } else if (categoria.getValue() == LAZER) {
                categoria_db = Categoria.LAZER;
            }

            Prioridade prioridade_db = null;
            int item_prioridade = prioridade.getSelectedItemPosition();
            if(item_prioridade == BAIXA) {
                prioridade_db = Prioridade.BAIXA;
            } else if (item_prioridade == MEDIA) {
                prioridade_db = Prioridade.MEDIA;
            } else if (item_prioridade == ALTA) {
                prioridade_db = Prioridade.ALTA;
            }

            operation = INSERIR;
            atividade_db = new Atividade(nomeAtividade_db, categoria_db, prioridade_db, null);
        } else {
            operation = ATUALIZAR;
            atividade_db = mAtividade;
        }

        saveData(operation, atividade_db, ti_db);
    }

    //Salva no BD
    private void saveData(int operation, Atividade atv, TI ti) {
       if(operation == ATUALIZAR){
           tiPersister.inserirTI(ti, atv.getId());
//           atv.addTI(ti);
           atividadePersister.atualizarAtividade(atv, idUser);
       }else if (operation == INSERIR){
//           atv.addTI(ti);
           atividadePersister.inserirAtividade(atv, idUser);
           tiPersister.inserirTI(ti, atv.getId());
       }
    }

    private int getWeek(String day) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            Date date = df.parse(day);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int week = cal.get(Calendar.WEEK_OF_YEAR);
            return week;
        } catch (ParseException e) {
            return 0;
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

//        //String para Date
//        Date date = null;
//        try {
//            date = new SimpleDateFormat("dd/MM/yyyy").parse(data_completa);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

        return data_completa;
    }
}
