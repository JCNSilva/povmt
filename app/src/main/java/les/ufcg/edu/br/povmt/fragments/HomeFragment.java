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
//import les.ufcg.edu.br.povmt.database.AtividadePersister;
import les.ufcg.edu.br.povmt.database.DataSource;
import les.ufcg.edu.br.povmt.models.Atividade;
import les.ufcg.edu.br.povmt.utils.AtividadeAdapter;
import les.ufcg.edu.br.povmt.utils.IonResume;
import les.ufcg.edu.br.povmt.utils.ServiceHandler;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements IonResume {

    private static String url = "http://lucasmatos.pythonanywhere.com/povmt/1/";
    List<Atividade> atividadeList;
    private static final int TRABALHO = 0;
    private static final int LAZER = 1;
    private String idUser;
//    private AtividadePersister atividadePersister;
    private ArrayList atividades;
    private RecyclerView listaAtividades;
    private TextView listaVazia;
    private LinearLayout campoAtividades;
    private TextView horasInvestidas;
    public static AtividadeAdapter adapter;
    private SharedPreferences sharedPreferences;
    private DataSource dataSource;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        sharedPreferences = getContext().getSharedPreferences(SplashActivity.PREFERENCE_NAME, Context.MODE_PRIVATE);
        idUser = sharedPreferences.getString(SplashActivity.USER_ID, "");
//        idUser = 123456;
//        atividadePersister = new AtividadePersister(getContext());
//        atividades = (ArrayList) atividadePersister.getAtividades(idUser);
        listaAtividades = (RecyclerView) view.findViewById(R.id.rview_atividades);
        campoAtividades = (LinearLayout) view.findViewById(R.id.ll_atividades);
        listaVazia = (TextView) view.findViewById(R.id.sem_ti);
        horasInvestidas = (TextView) view.findViewById(R.id.tv_horasinv_semana);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        listaAtividades.setLayoutManager(llm);

        new GetaAtividade().execute();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    public void refresh() {
        //            atividadePersister = AtividadePersister.getInstance(getContext());
        dataSource = DataSource.getInstance(getContext());
        atividades = (ArrayList) dataSource.getAtividades(idUser);
        adapter = new AtividadeAdapter(new ArrayList<Atividade>(atividades));
        listaAtividades.setAdapter(adapter);

        if (atividades.isEmpty()) {
            listaVazia.setVisibility(View.VISIBLE);
            campoAtividades.setVisibility(View.GONE);
        } else {
            listaVazia.setVisibility(View.GONE);
            campoAtividades.setVisibility(View.VISIBLE);
        }

        //TODO Calcular TI na Semana
        horasInvestidas.setText(String.valueOf(getHorasInvestidas()) + " " + getString(R.string.horas_investidas));
    }

    private int getHorasInvestidas() {
        int horasInvestidas = 0;
        List<Atividade> atividadesList = adapter.getmAtividades();
        for (int i = 0; i < atividadesList.size(); i++) {
            horasInvestidas += atividadesList.get(i).getTI();
        }
        return horasInvestidas;
    }


    /**
     * Async task class to get json by making HTTP call
     */
    private class GetaAtividade extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Carregando ...");
            pDialog.setCancelable(false);
            pDialog.show();*/

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            String json = sh.makeServiceCall(url, ServiceHandler.GET);

            if (json != null) {
                try {
                    JSONArray atividades = new JSONArray(json);

                    for (int i = 0; i < atividades.length(); i++) {
                        JSONObject d = atividades.getJSONObject(i);
                        Gson gson = new Gson();
                        //disciplinaList.add(gson.fromJson(d.toString(), Disciplina.class));
                        String TAG = "QuickNotesMainActivity";
                        Log.d(TAG, d.getString("id"));
                        Log.d(TAG, d.getString("nome"));
                        Log.d(TAG, d.getString("url_imagem"));
                        Log.d(TAG, d.getString("prioridade"));
                        Log.d(TAG, d.getString("categoria"));
                        Log.d(TAG, d.getString("categoria"));
                        Log.d(TAG, d.getString("id_usuario"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            /*if (pDialog.isShowing())
                pDialog.dismiss();*/

        }

    }
}
