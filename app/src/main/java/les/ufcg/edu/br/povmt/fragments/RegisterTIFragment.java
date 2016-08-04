package les.ufcg.edu.br.povmt.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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

import org.json.JSONObject;

import org.honorato.multistatetogglebutton.MultiStateToggleButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
//                showEneableMessageIfNeeded();
                if (!isNetworkAvailable()) {
                    displayPromptForEnablingInternet();
                } else {
                    try{
                        prepareData();
                        homeFragment.refresh();
                        dismiss();
                    }catch(InputException e){
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
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
            inserirTI(atv, ti);

        } else if (operation == INSERIR){
            inserirAtividadeTI(atv, ti);
        }
    }

    private void inserirAtividadeTI(final Atividade atv, final TI ti) {
        final String URL_CRIA_ATIVIDADE = "http://lucasmatos.pythonanywhere.com/povmt/" + idUser + "/";
        final Response.ErrorListener genericErrorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handleVolleyError(error);
            }
        };

        final Response.Listener<JSONObject> criaAtividadeResponseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    if(response.has("ati_id") && !response.isNull("ati_id")){
                        atv.setId(response.getLong("ati_id"));
                        dataSource.inserirAtividade(atv, idUser);
                        inserirTI(atv, ti);
                    } else {
                        Log.w(TAG, "A resposta veio sem data");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "Erro ao converter dados");
                }
            }
        };

        JSONObject params = new JSONObject();
        try {
            params.put("nome", nullSafe(atv.getNome()));
            params.put("prioridade", nullSafe(String.valueOf(atv.getPrioridade())));
            params.put("categoria", nullSafe(String.valueOf(atv.getCategoria())));
            params.put("id_usuario", nullSafe(idUser));
        } catch (Exception e) {
            e.printStackTrace();
        }

        final JsonObjectRequest criaAtividadeRequest = new JsonObjectRequest(Request.Method.POST, URL_CRIA_ATIVIDADE, params,
                criaAtividadeResponseListener, genericErrorListener);

        requestQueue.add(criaAtividadeRequest);
    }


    private void inserirTI(final Atividade atv, final TI ti) {
        final String URL_CRIA_TI = "http://lucasmatos.pythonanywhere.com/povmt/tilist/" + atv.getId() + "/";
        final Response.ErrorListener genericErrorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handleVolleyError(error);
            }
        };

        final Response.Listener<JSONObject> criaTIResponseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    if(response.has("ti_id") && !response.isNull("ti_id")){
                        ti.setId(response.getLong("ti_id"));
                        dataSource.inserirTI(ti, atv.getId());
                    } else {
                        Log.w(TAG, "A resposta veio sem data");
                    }
                    homeFragment.refresh();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "Erro ao converter dados");
                }
            }
        };

        final JSONObject params = new JSONObject();
        try {
            params.put("data", nullSafe(ti.getData()));
            params.put("semana", nullSafe(String.valueOf(ti.getSemana())));
            params.put("horas", nullSafe(String.valueOf(ti.getHoras())));
            params.put("id_atividade", nullSafe(String.valueOf(atv.getId())));
        } catch (Exception e) {
            e.printStackTrace();
        }

        final JsonObjectRequest inserirTIRequest = new JsonObjectRequest(Request.Method.POST, URL_CRIA_TI, params,
                criaTIResponseListener, genericErrorListener);/* {
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
        };*/

        requestQueue.add(inserirTIRequest);
    }


    /*private void inserirERefletirTI(final Atividade atv, final TI ti) {
        dataSource.inserirTI(ti, atv.getId());
        inserirTI(atv, ti);
    }*/

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

    /** Method to show message internet if  is disabled
     */
    public void showEneableMessageIfNeeded() {
        if (!isNetworkAvailable()) {
            displayPromptForEnablingInternet();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    /** Method to show the display to enabling internet
     */
    private void displayPromptForEnablingInternet() {
        final AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());
        final String actionWifiSettings = Settings.ACTION_WIFI_SETTINGS;
        final String actionWirelessSettings = Settings.ACTION_WIRELESS_SETTINGS;
        final String message = getString(R.string.enable_network);

        builder.setMessage(message)
                .setPositiveButton(getString(R.string.bt_wifi),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int idButton) {
                                getActivity().startActivity(new Intent(actionWifiSettings));
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(getString(R.string.bt_mobile_network),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int idButton) {
                                getActivity().startActivity(new Intent(actionWirelessSettings));
                                dialog.dismiss();
                            }
                        })
                .setNeutralButton(getString(R.string.bt_cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int idButton) {
                                dialog.cancel();
                            }
                        });
        builder.create().show();
    }
}
