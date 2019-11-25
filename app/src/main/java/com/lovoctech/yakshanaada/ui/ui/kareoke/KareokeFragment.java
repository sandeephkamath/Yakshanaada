package com.lovoctech.yakshanaada.ui.ui.kareoke;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import butterknife.BindView;

import com.lovoctech.yakshanaada.KareokePlayer;

import butterknife.ButterKnife;

import com.lovoctech.yakshanaada.PlayerManager;
import com.lovoctech.yakshanaada.R;
import com.lovoctech.yakshanaada.RxBus;
import com.lovoctech.yakshanaada.YakshaNaadaApplication;
import com.lovoctech.yakshanaada.model.Shruthi;

import java.util.ArrayList;
import java.util.List;


public class KareokeFragment extends Fragment {

    private Spinner taalaSpinner;
    private KareokePlayer playerManager;
    private RxBus rxBus;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_kareoke, container, false);
        ButterKnife.bind(this, root);
        addTaalas(root);
        rxBus = ((YakshaNaadaApplication) getActivity().getApplication()).bus();

        rxBus.toObservable()
                .subscribe(object -> {
                    if (object instanceof Shruthi) {
                        playShruthi((Shruthi) object);
                    }
                });

        playerManager = KareokePlayer.getInstance();
        playerManager.init(getContext(), rxBus);


        Button button = (Button) root.findViewById(R.id.buttonNade);
        button.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //playerManager.release();
                Shruthi shruthi = new Shruthi(
                        R.raw.ds,
                        "DS",
                        "ಕಪ್ಪು 2",
                        "D#",
                        R.mipmap.yakshanaada
                );
                playerManager.playNade(shruthi, R.raw.twaritha_trivude_nade, 1.0f);

            }
        });

        Button buttonBidthige = (Button) root.findViewById(R.id.buttonBidthige);
        buttonBidthige.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Shruthi shruthi = new Shruthi(
                        R.raw.ds,
                        "DS",
                        "ಕಪ್ಪು 2",
                        "D#",
                        R.mipmap.yakshanaada
                );
                playerManager.playBidthige(R.raw.twaritha_trivude_bidthige, R.raw.twaritha_trivude_nade, 1.0f);
            }
        });


        return root;
    }

    private void addTaalas(View view) {
        List<String> taals = new ArrayList<String>();
        taals.add("Twaritha Trivude");
        taals.add("Nidhaana Trivude");
        taalaSpinner = (Spinner) view.findViewById(R.id.spinner);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_layout, taals);
        taalaSpinner.setAdapter(dataAdapter);
        taalaSpinner.setEnabled(true);
    }

    private void playShruthi(Shruthi shruthi) {
        //playerManager.release();
        //playerManager.setShruthi(shruthi);
    }

}