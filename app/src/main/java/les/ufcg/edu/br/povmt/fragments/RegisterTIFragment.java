package les.ufcg.edu.br.povmt.fragments;


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

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONObject;

import org.honorato.multistatetogglebutton.MultiStateToggleButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import les.ufcg.edu.br.povmt.R;
import les.ufcg.edu.br.povmt.activities.SplashActivity;
//import les.ufcg.edu.br.povmt.database.AtividadePersister;
import les.ufcg.edu.br.povmt.database.DataSource;
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
    private static final String TAG = "RegisterTIFragment";

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

    private RequestQueue requestQueue;

    public static final String PREFERENCE_NAME = "USER_PREFERENCE";
    private MultiStateToggleButton dia;
    private TextView horas;
    private Spinner atividade;
    private TextView nomeAtividade;
    private MultiStateToggleButton categoria;
    private Spinner prioridade;
    private TextView tag;
    private Button cancel;
    private Button ok;

    private int horasDB;
    private String nomeAtividadeDB;
    private String tagAtividadeDB;
    private String atividadeEscolhida;

    private ArrayList<Atividade> atividades;
    private LinearLayout layoutNewActivity;
    private Atividade mAtividade;
    private SharedPreferences sharedPreferences;
    private String idUser;
    private IonResume homeFragment;
    private CharSequence hrs;
    private DataSource dataSource;

    public RegisterTIFragment(IonResume homeFragment) {
        this.homeFragment = homeFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register_ti, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        requestQueue = DataSource.getInstance(getContext()).getRequestQueue();

        sharedPreferences = getContext().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        idUser = sharedPreferences.getString(SplashActivity.USER_ID, "");
//        idUser = 123456;
//        atividadePersister = AtividadePersister.getInstance(getContext());
        dataSource = DataSource.getInstance(getContext());
        atividades = (ArrayList<Atividade>) dataSource.getAtividades(idUser);


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
                    layoutNewActivity.setVisibility(View.VISIBLE);
                } else {
                    layoutNewActivity.setVisibility(View.GONE);
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

        nomeAtividade = (TextView) view.findViewById(R.id.nome_atv);
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

        layoutNewActivity = (LinearLayout) view.findViewById(R.id.layout_new_activity);
    }

    //Prepara dados para BD
    private void prepareData() throws InputException {
        String dia_db = getDate(dia.getValue());
        int semana_db = getWeek(dia_db);
        hrs = horas.getText();

        if (hrs.length() > 0) {
            horasDB = Integer.parseInt(String.valueOf(horas.getText()));
        } else {
            throw new InputException("Hora deve ser um valor entre 0 e 24");
        }

        if( horasDB <= 0 || horasDB > 24 || hrs == null){
            throw new InputException("Hora deve ser um valor entre 0 e 24");
        }

        TI ti_db = new TI(dia_db, semana_db, horasDB);
        Atividade atividade_db = null;
        int operation = INVALIDO;

        atividadeEscolhida = atividade.getSelectedItem().toString();
        if (atividadeEscolhida.equals(getString(R.string.nova_atividade))) {
            nomeAtividadeDB = nomeAtividade.getText().toString();
            if(nomeAtividadeDB.trim().equals("")){
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
            atividade_db = new Atividade(nomeAtividadeDB, categoria_db, prioridade_db, null);
        } else {
            operation = ATUALIZAR;
            atividade_db = mAtividade;
        }

        saveData(operation, atividade_db, ti_db);
    }

    //Salva no BD
    private void saveData(int operation, final Atividade atv, final TI ti) {

        if(operation == ATUALIZAR){
            inserirERefletirTI(atv, ti);

        } else if (operation == INSERIR){
            inserirERefletirAtividade(atv);
            inserirERefletirTI(atv, ti);
        }
    }

    private void inserirERefletirAtividade(final Atividade atv) {
        dataSource.inserirAtividade(atv, idUser);

        final String URL_CRIA_ATIVIDADE = "http://lucasmatos.pythonanywhere.com/povmt/" + idUser + "/";
        final String URL_GET_ATIVIDADE = "http://lucasmatos.pythonanywhere.com/povmt/atividade/" + atv.getId();

        final Response.Listener<JSONObject> getAtividadeResponseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    if(response.has("data_created") && !response.isNull("data_created")){
                        String dataPersistido = response.getString("data_created");
                        DataSource.getInstance(getContext())
                                .setDataSincronizacaoAtividade(atv.getId(), dataPersistido);

                        Log.d(TAG, "" + DataSource.getInstance(getContext())
                                .getDataSincronizacaoAtividade(atv.getId()));
                    } else {
                        Log.w(TAG, "A resposta veio sem data");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "Erro ao converter dados");
                }
            }
        };

        final Response.ErrorListener genericErrorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handleVolleyError(error);
            }
        };

        final JsonObjectRequest getAtividadeRequest = new JsonObjectRequest(Request.Method.GET, URL_GET_ATIVIDADE, null,
                getAtividadeResponseListener, genericErrorListener);

        final Response.Listener<String> criaAtividadeResponseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, response);
                requestQueue.add(getAtividadeRequest);
            }
        };

        final StringRequest criaAtividadeRequest = new StringRequest(Request.Method.POST, URL_CRIA_ATIVIDADE,
                criaAtividadeResponseListener, genericErrorListener) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("id", nullSafe(String.valueOf(atv.getId())));
                params.put("nome", nullSafe(atv.getNome()));
                params.put("categoria", nullSafe(atv.getCategoria().toString()));
                params.put("prioridade", nullSafe(atv.getPrioridade().toString()));
                params.put("id_usuario", nullSafe(idUser));


                for(String key: params.keySet()){
                    Log.e(TAG+" Ativ", key+":"+params.get(key));
                }

                return params;
            }
        };

        requestQueue.add(criaAtividadeRequest);
    }



    private void inserirERefletirTI(final Atividade atv, final TI ti) {
        dataSource.inserirTI(ti, atv.getId());

        final String URL_CRIA_TI = "http://lucasmatos.pythonanywhere.com/povmt/tilist/" + atv.getId() + "/";
        final String URL_GET_TI = "http://lucasmatos.pythonanywhere.com/povmt/tiedit/" + ti.getId()+ "/" + atv.getId();

        final Response.Listener<JSONObject> getTIResponseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    if(response.has("data_created") && !response.isNull("data_created")){
                        String dataPersistido = response.getString("data_created");
                        DataSource.getInstance(getContext())
                                .setDataSincronizacaoTI(ti.getId(), dataPersistido);

                        Log.d(TAG, "" + DataSource.getInstance(getContext())
                                .getDataSincronizacaoTI(ti.getId()));
                    } else {
                        Log.w(TAG, "A resposta veio sem data");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "Erro ao converter dados");
                }
            }
        };

        final Response.ErrorListener genericErrorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handleVolleyError(error);
            }
        };

        final JsonObjectRequest getTIRequest = new JsonObjectRequest(Request.Method.GET, URL_GET_TI, null,
                getTIResponseListener, genericErrorListener);

        final Response.Listener<String> criaTIResponseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                requestQueue.add(getTIRequest);
            }
        };

        final StringRequest inserirTIRequest = new StringRequest(Request.Method.POST, URL_CRIA_TI,
                criaTIResponseListener, genericErrorListener) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", nullSafe(String.valueOf(ti.getId())));
                params.put("data", nullSafe(ti.getData()));
                params.put("semana", nullSafe(String.valueOf(ti.getSemana())));
                params.put("horas", nullSafe(String.valueOf(ti.getHoras())));
                params.put("id_atividade", nullSafe(String.valueOf(atv.getId())));

                for(String key: params.keySet()){
                    Log.e(TAG+" TI", key+":"+params.get(key));
                }

                return params;
            }
        };

        requestQueue.add(inserirTIRequest);
    }

    private String nullSafe(String string) {
        if(string == null) return "";
        else return string;
    }

    private void handleVolleyError(VolleyError error) {
        NetworkResponse response = error.networkResponse;
        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
            Log.e(TAG, "Sem resposta!");
        } else if (error instanceof AuthFailureError) {
            Log.e(TAG, "Erro de autenticacao!");
        } else if (error instanceof ServerError) {
            Log.e(TAG, "Erro de servidor!");
        } else if (error instanceof NetworkError) {
            Log.e(TAG, "Erro de rede!");
        } else if (error instanceof ParseError) {
            Log.e(TAG, "Erro ao converter resposta!");
        } else {
            Log.e(TAG, "Erro desconhecido!");
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
