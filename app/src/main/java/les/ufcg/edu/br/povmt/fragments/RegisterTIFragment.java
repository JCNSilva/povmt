package les.ufcg.edu.br.povmt.fragments;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import org.honorato.multistatetogglebutton.MultiStateToggleButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import les.ufcg.edu.br.povmt.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterTIFragment extends DialogFragment {
    private final int HOJE = 0;
    private final int ONTEM = 1;

    private MultiStateToggleButton dia;
    private TextView horas;
    private Spinner atividade;
    private TextView nome_atividade;
    private MultiStateToggleButton categoria;
    private Spinner prioridade;
    private TextView tag;
    private Button cancel;
    private Button ok;

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
                saveData();
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
    }

    private void saveData() {
        // DIA
        String dia_db = getDate(dia.getValue());

        //HORAS
        int horas_db = Integer.parseInt(String.valueOf(horas.getText()));

        //TODO
        //Pegar demais dados jogar no construtor de Atividade e depois persistir com o AtividadePersister
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
