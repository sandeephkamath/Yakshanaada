package com.lovoctech.yakshanaada.ui.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lovoctech.yakshanaada.PlayerManager;
import com.lovoctech.yakshanaada.R;
import com.lovoctech.yakshanaada.RxBus;
import com.lovoctech.yakshanaada.YakshaNaadaApplication;
import com.lovoctech.yakshanaada.model.Event;
import com.lovoctech.yakshanaada.model.Shruthi;
import com.lovoctech.yakshanaada.model.Tanpur;
import com.lovoctech.yakshanaada.repository.HindusthaniRepository;
import com.lovoctech.yakshanaada.ui.TanpurAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class HindusthaniFragment extends Fragment {

    @BindView(R.id.shruthi_list)
    RecyclerView recyclerView;

    @BindView(R.id.shruthi_title)
    TextView shruthiTitle;

    @BindView(R.id.play_pause)
    ImageView playPauseBtn;

    @BindView(R.id.player_area)
    RelativeLayout playerArea;

    @BindView(R.id.adView)
    FrameLayout adView;

    @BindView(R.id.pama)
    SwitchCompat pamaSwitch;

    @OnClick(R.id.play_pause)
    void onPlayPause() {
        if (playerManager.isPlaying()) {
            playerEvent(new Event(null, Event.PAUSED));
            playerManager.pause();
        } else {
            playerEvent(new Event(null, Event.PLAYING));
            playerManager.resume();
        }
    }


    private RxBus rxBus;
    private PlayerManager playerManager;
    private Tanpur tanpur;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_hindusthani, container, false);
        ButterKnife.bind(this, root);
        List<Tanpur> tanpurs = HindusthaniRepository.getAll();
        rxBus = ((YakshaNaadaApplication) getActivity().getApplication()).bus();

        TanpurAdapter tanpurAdapter = new TanpurAdapter(tanpurs, rxBus);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(tanpurAdapter);


        rxBus.toObservable()
                .subscribe(object -> {
                    if (object instanceof Tanpur) {
                        playTanpur((Tanpur) object);
                    }
                });

        playerManager = PlayerManager.getInstance();
        playerManager.init(getContext(), rxBus);

        pamaSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (playerManager.isPlaying()) {
                    playTanpur(tanpur);
                }
            }
        });

        return root;
    }

    private void playerEvent(Event event) {
        int imageResId = R.drawable.pause;
        switch (event.getStatus()) {
            case Event.PAUSED:
                imageResId = R.drawable.play;
                setPlayAreaVisibility(View.VISIBLE);
                break;
            case Event.STOPPED:
                setPlayAreaVisibility(View.GONE);
                break;
            case Event.PLAYING:
                imageResId = R.drawable.pause;
                setPlayAreaVisibility(View.VISIBLE);
                break;
        }
        if (playPauseBtn != null) {
            playPauseBtn.setImageResource(imageResId);
        }
    }

    private void setPlayAreaVisibility(int visibility) {
        if (playerArea != null && playerArea.getHandler() != null) {
            playerArea.getHandler().post(() -> {
                playerArea.setVisibility(visibility);
            });
        } else {
            if (playerArea != null) {
                playerArea.getHandler();
                playerArea.setVisibility(visibility);
            }
        }

    }

    private void playTanpur(Tanpur tanpur) {
        this.tanpur = tanpur;
        playerManager.release();
        Shruthi shruthi = new Shruthi(getRes(tanpur), tanpur.getMediaId(), tanpur.getTitle(), tanpur.getDescription(), 0);
        playerManager.setShruthi(shruthi);
        shruthiTitle.setText(tanpur.getTitle());
        playerEvent(new Event(null, Event.PLAYING));
    }

    private int getRes(Tanpur tanpur) {
        if (pamaSwitch.isChecked()) {
            return tanpur.getMa();
        }
        return tanpur.getPa();
    }
}