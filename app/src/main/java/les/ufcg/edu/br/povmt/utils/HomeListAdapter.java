package les.ufcg.edu.br.povmt.utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import les.ufcg.edu.br.povmt.R;
import les.ufcg.edu.br.povmt.database.TIPersister;
import les.ufcg.edu.br.povmt.models.Atividade;
import les.ufcg.edu.br.povmt.models.TI;


public class HomeListAdapter extends BaseAdapter {
    private final List<Atividade> atividades;
    private final Context context;

    public HomeListAdapter(Context context, List<Atividade> atividades) {
        this.context = context;
        this.atividades = atividades;
    }

    @Override
    public int getCount() {
        return atividades.size();
    }

    @Override
    public Atividade getItem(int item) {
        return atividades.get(item);
    }

    @Override
    public long getItemId(int item) {
        return item;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = mInflater.inflate(R.layout.home_list_layout, null);
        }
        TIPersister tiPersister = new TIPersister(context);

        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int week = cal.get(Calendar.WEEK_OF_YEAR);

        Atividade atv = (Atividade) atividades.get(position);

        List<TI> tis = tiPersister.getTISemana(atv.getId(), week);
        atv.setTiList(tis);

        TextView atividade = (TextView) view.findViewById(R.id.atividade_adapter);
        atividade.setText(atv.getNome());
        TextView ti = (TextView) view.findViewById(R.id.ti_adapter);
        ti.setText("Categoria: " + atv.getCategoria() + "\n Proridade: " + atv.getPrioridade() +
                "\nTI: " + atv.getTI());

        return view;
    }
}
