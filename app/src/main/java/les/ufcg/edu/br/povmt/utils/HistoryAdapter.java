package les.ufcg.edu.br.povmt.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import les.ufcg.edu.br.povmt.R;
import les.ufcg.edu.br.povmt.database.TIPersister;
import les.ufcg.edu.br.povmt.models.Atividade;
import les.ufcg.edu.br.povmt.models.TI;

/**
 * Created by Julio on 20/07/2016.
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.AtividadeViewHolder> {

    private List<Atividade> mAtividades;
    private int semana;
    private Context context;

    public HistoryAdapter(Context context, List<Atividade> mAtividades, int semana) {
        this.mAtividades = mAtividades;
        this.semana = semana;
        this.context = context;
        Collections.sort(this.mAtividades);
    }

    @Override
    public AtividadeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.atividade_cview, parent, false);
        AtividadeViewHolder avh = new AtividadeViewHolder(v);
        return avh;
    }

    @Override
    public void onBindViewHolder(AtividadeViewHolder holder, int position) {
        Atividade atv = (Atividade) mAtividades.get(position);

        holder.nome.setText(atv.getNome());
//        holder.prioridade.setText(mAtividades.get(position).getPrioridade().toString());
//        holder.categoria.setText(mAtividades.get(position).getCategoria().toString());

        TIPersister tiPersister = new TIPersister(context);
        List<TI> tis = tiPersister.getTISemana(atv.getId(), semana);
        atv.setTiList(tis);

        int ti = atv.getTI();
        holder.tempoInvestido.setText(String.valueOf(ti) + " horas");
        if (getHorasInvestidas() == 0) {
            holder.proporcao.setText(0 + "%");
        } else {
            holder.proporcao.setText((ti/getHorasInvestidas() * 100) + "%");
        }
    }


    @Override
    public int getItemCount() {
        return mAtividades.size();
    }

    public static class AtividadeViewHolder extends RecyclerView.ViewHolder {
        private final TextView proporcao;
        public TextView categoria;
        public TextView prioridade;
        public TextView nome;
        public TextView tempoInvestido;

        public AtividadeViewHolder(View itemView) {
            super(itemView);
//            categoria = (TextView) itemView.findViewById(R.id.tv_tipo_atividade);
//            prioridade = (TextView) itemView.findViewById(R.id.tv_prioridade_atividade);
            nome = (TextView) itemView.findViewById(R.id.tv_nome_atividade);
            tempoInvestido = (TextView) itemView.findViewById(R.id.tv_tempo_atividade);
            proporcao = (TextView) itemView.findViewById(R.id.tv_proporcao_atividade);
        }
    }

    public List<Atividade> getmAtividades() {
        return mAtividades;
    }

    private float getHorasInvestidas() {
        float horasInvestidas = 0;
        List<Atividade> atividadesList = getmAtividades();
        for (int i = 0; i < atividadesList.size(); i++) {
            horasInvestidas += atividadesList.get(i).getTI();
        }
        return horasInvestidas;
    }
}
