package les.ufcg.edu.br.povmt.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import les.ufcg.edu.br.povmt.R;

/**
 * Created by Veruska on 27/07/2016.
 */
public class ConfigurationsFragment extends Fragment{


    public ConfigurationsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_configuration, container, false);
    }
}
