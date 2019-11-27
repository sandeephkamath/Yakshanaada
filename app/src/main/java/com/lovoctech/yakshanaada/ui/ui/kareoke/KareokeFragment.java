package com.lovoctech.yakshanaada.ui.ui.kareoke;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lovoctech.yakshanaada.KareokePlayer;
import com.lovoctech.yakshanaada.R;
import com.lovoctech.yakshanaada.RxBus;
import com.lovoctech.yakshanaada.YakshaNaadaApplication;
import com.lovoctech.yakshanaada.model.Shruthi;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;


public class KareokeFragment extends Fragment {

    @BindView(R.id.editText)
    EditText editText;

    @BindView(R.id.buttonNade)
    Button nadeButton;

    private KareokePlayer playerManager;
    private int selectedPosition;

    @BindView(R.id.spinner)
    Spinner taalaSpinner;

    @OnClick(R.id.buttonNade)
    void nadeClick() {
        Shruthi shruthi = new Shruthi(
                R.raw.ds,
                "DS",
                "ಕಪ್ಪು 2",
                "D#",
                R.mipmap.yakshanaada
        );
        playerManager.playNade(shruthi, R.raw.twaritha_trivude_nade, Float.parseFloat(editText.getText().toString()));
    }

    @OnClick(R.id.buttonBidthige)
    void bidthigeClick() {
        Shruthi shruthi = new Shruthi(
                R.raw.ds,
                "DS",
                "ಕಪ್ಪು 2",
                "D#",
                R.mipmap.yakshanaada
        );
        playerManager.playBidthige(R.raw.twaritha_trivude_bidthige, R.raw.twaritha_trivude_nade, Float.parseFloat(editText.getText().toString()));
    }

    @OnItemSelected(R.id.spinner)
    void onSpinnerItemSelected(int index) {
        selectedPosition = index;
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_kareoke, container, false);
        ButterKnife.bind(this, root);
        addTaalas();
        RxBus rxBus = ((YakshaNaadaApplication) Objects.requireNonNull(getActivity()).getApplication()).bus();

        playerManager = KareokePlayer.getInstance();
        playerManager.init(getContext(), rxBus);
        return root;
    }

    private void addTaalas() {
        List<String> taals = new ArrayList<>();
        taals.add("Twaritha Trivude");
        taals.add("Nidhaana Trivude");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(Objects.requireNonNull(getContext()), R.layout.spinner_layout, taals) {
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, null, parent);
                if (position == selectedPosition) {
                    view.setBackgroundColor(Color.BLACK);
                    ((TextView) view).setTextColor(Color.WHITE);
                } else {
                    view.setBackgroundColor(Color.WHITE);
                    ((TextView) view).setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        taalaSpinner.setAdapter(dataAdapter);
        taalaSpinner.setEnabled(true);
    }


}