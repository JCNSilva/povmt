package les.ufcg.edu.br.povmt.fragments;
 import android.app.Activity;
 import android.app.AlertDialog;
 import android.content.DialogInterface;
 import android.graphics.drawable.ColorDrawable;
 import android.os.Bundle;
 import android.support.v4.app.DialogFragment;
 import android.util.Log;
 import android.view.LayoutInflater;
 import android.view.View;
 import android.view.ViewGroup;
 import android.widget.ArrayAdapter;
 import android.widget.Button;
 import android.widget.CalendarView;
 import android.widget.NumberPicker;
 import android.widget.Spinner;

 import java.util.ArrayList;
 import java.util.Calendar;
 import java.util.List;

 import les.ufcg.edu.br.povmt.R;
 import les.ufcg.edu.br.povmt.database.TIPersister;
 import les.ufcg.edu.br.povmt.models.Atividade;
 import les.ufcg.edu.br.povmt.models.Categoria;
 import les.ufcg.edu.br.povmt.models.Prioridade;
 import les.ufcg.edu.br.povmt.models.TI;


public class CreateTIFragment extends DialogFragment {
    private int numStyle;
    private int numTheme;
    private Calendar calendar;
    private TI ti;
    private TIPersister tiPersister;
    private Atividade atividade;


    public CreateTIFragment(int numStyle, int numTheme){
        this.numStyle = numStyle;
        this.numTheme = numTheme;
    }
    public CreateTIFragment(){
    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.i("Script", "onCreate()");

        int style;
        int theme;

        switch(numStyle){
            case 1: style = DialogFragment.STYLE_NO_TITLE; break;
            case 2: style = DialogFragment.STYLE_NO_INPUT; break;
            case 3: style = DialogFragment.STYLE_NO_FRAME; break;
            default: style = DialogFragment.STYLE_NORMAL; break;
        }

        switch(numTheme){
            case 1: theme = android.R.style.Theme_Holo_Light; break;
            case 2: theme = android.R.style.Theme_Holo_Dialog; break;
            default: theme = android.R.style.Theme_Holo_Light_DarkActionBar; break;
        }

        setStyle(style, theme);
        setCancelable(false);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        Log.i("Script", "onCreateView()");

        View view = inflater.inflate(R.layout.fragment_create_ti, container);
        final Spinner sItems = (Spinner) view.findViewById(R.id.spinnerPrioridade);
        final Spinner sItemsAtividade = (Spinner) view.findViewById(R.id.spinner);
        final CalendarView calendarView = (CalendarView) view.findViewById(R.id.editCalendar);
        final NumberPicker hour = (NumberPicker) view.findViewById(R.id.editHour);
        //TODO VER MELHOR FORMA DE PEGAR CATEGORIA

        List<String> spinnerArrayAtividade =  new ArrayList<String>();
        spinnerArrayAtividade.add("comer");
        spinnerArrayAtividade.add("dormir");
        List<String> spinnerArrayPrioridade =  new ArrayList<String>();
        spinnerArrayPrioridade.add("importante");
        spinnerArrayPrioridade.add("fraca");

        preencheCampoData(view, calendarView);
        preencheCampoHora(view, hour);
        preencheCampoSpinner(view, sItemsAtividade, spinnerArrayAtividade);
        preencheCampoSpinner(view, sItems, spinnerArrayPrioridade);

        Button btnExit = (Button) view.findViewById(R.id.btnCancel);
        btnExit.setOnClickListener(new Button.OnClickListener() {
           @Override
           public void onClick(View v) {
               dismiss();

               //((MainActivity) getActivity()).turnOffDialogFragment();
           }
       });

        Button btnOk = (Button) view.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                String atividade = sItemsAtividade.getSelectedItem().toString();
                String prioridade = sItems.getSelectedItem().toString();
                int hora = hour.getValue();
                final Calendar calendar = Calendar.getInstance();

                CalendarView.OnDateChangeListener myCalendarListener = new CalendarView.OnDateChangeListener() {
                    public void onSelectedDayChange(CalendarView view, int year, int month, int day) {

                        month = month + 1;
                        String newDate = year + "-" + month + "-" + day;
                        calendar.set(year, month, day);
                    }
                };
                calendarView.setOnDateChangeListener(myCalendarListener);

                long idAtividade = 1445l;
                //TODO RECUPERAR ID DO CAMPO
                if (atividade.equalsIgnoreCase("Nova Atividade")) {
                    idAtividade = criaAtividade("nome", Categoria.valueOf("lazer".toUpperCase()),
                            Prioridade.valueOf(prioridade.toUpperCase()));
                }
                criaTi(calendar, hora, "url", idAtividade);

                dismiss();
                showDialogSuccess("Create TI","TI created with success!");
                //((MainActivity) getActivity()).turnOffDialogFragment();
            }
        });

        return(view);
    }

    private void showDialogSuccess(String titulo, String msg){
        new AlertDialog.Builder(getContext())
                .setTitle(titulo)
                .setMessage(msg)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {}
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }

    private void criaTi(Calendar data, int hora, String url, long idAtividade){
        ti = new TI(1355l, data, hora, url);
        tiPersister = new TIPersister(getContext());
        tiPersister.inserirTI(ti, idAtividade);
    }

    private long criaAtividade(String nome, Categoria categoria, Prioridade prioridade){
        atividade = new Atividade(1344l, nome, categoria, prioridade);
        return atividade.getId();
    }

    private void preencheCampoSpinner(View view, Spinner spinner, List<String> spinnerArray){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getContext(), android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void preencheCampoData(View view, CalendarView calendarView){
        calendar = Calendar.getInstance();
        calendarView.setMaxDate(calculeMaxDate(calendar));
        calendarView.setMinDate(calculeMinDate(calendar));
    }

    private void preencheCampoHora(View view, NumberPicker hour){
        hour.setMaxValue(24);
        hour.setMinValue(0);
        hour.getWrapSelectorWheel();
    }

    private long calculeMaxDate(Calendar calendar){
            return (calendar.get(Calendar.YEAR) - 1969) * 31556952000L
                    - ((12 - (calendar.get(Calendar.MONTH)+1)) * 2629700000L)
                - ((31 - calendar.get(Calendar.DAY_OF_MONTH)) * 86400000);
        //TODO app trava ao deixar ate a data atual
    }

    private long calculeMinDate(Calendar calendar){
        return (calendar.get(Calendar.YEAR) - 1969) * 31556952000L
                - ((12 - (calendar.get(Calendar.MONTH)+1)) * 2629700000L)
                - ((31 - (calendar.get(Calendar.DAY_OF_MONTH)-2)) * 86400000);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        Log.i("Script", "onActivityCreated()");

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }


    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        Log.i("Script", "onAttach()");
    }


    @Override
    public void onCancel(DialogInterface dialog){
        super.onCancel(dialog);
        Log.i("Script", "onCancel()");
    }

	/*@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		super.onCreateDialog(savedInstanceState);
		Log.i("Script", "onCreateDialog()");

		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity())
			.setTitle("DialogFragment")
			.setIcon(R.drawable.ic_launcher)
			.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Toast.makeText(getActivity(), "Ok pressed", Toast.LENGTH_LONG).show();
				}
			}).setNegativeButton("Sair", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dismiss();
				}
			});

		return(alert.show());
	}*/


    @Override
    public void onDestroyView(){
        super.onDestroyView();
        Log.i("Script", "onDestroyView()");
    }


    @Override
    public void onDetach(){
        super.onDetach();
        Log.i("Script", "onDetach()");
    }


    @Override
    public void onDismiss(DialogInterface dialog){
        super.onDismiss(dialog);
        Log.i("Script", "onDismiss()");
    }


    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        Log.i("Script", "onSaveInstanceState()");
    }


    @Override
    public void onStart(){
        super.onStart();
        Log.i("Script", "onStart()");
    }


    @Override
    public void onStop(){
        super.onStop();
        Log.i("Script", "onStop()");
    }

}
