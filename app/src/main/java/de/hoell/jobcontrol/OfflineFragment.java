package de.hoell.jobcontrol;

/**
 * Created by Hoell on 16.10.2014.
 */

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class OfflineFragment extends Fragment {

    public OfflineFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_offline, container, false);

        return rootView;
    }
}