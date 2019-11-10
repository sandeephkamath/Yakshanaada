package com.lovoctech.yakshanaada.ui.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.lovoctech.yakshanaada.PlayerManager;
import com.lovoctech.yakshanaada.R;
import com.lovoctech.yakshanaada.RxBus;
import com.lovoctech.yakshanaada.YakshaNaadaApplication;
import com.lovoctech.yakshanaada.model.Event;
import com.lovoctech.yakshanaada.model.Shruthi;
import com.lovoctech.yakshanaada.repository.ShruthiRepository;
import com.lovoctech.yakshanaada.ui.ShruthiAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class HomeFragment extends Fragment {

    @BindView(R.id.shruthi_list)
    RecyclerView recyclerView;

    @BindView(R.id.shruthi_title)
    TextView shruthiTitle;

    @BindView(R.id.play_pause)
    ImageView playPauseBtn;

    @BindView(R.id.player_area)
    RelativeLayout playerArea;

    @BindView(R.id.adView)
    AdView adView;

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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, root);
        List<Shruthi> shruthis = ShruthiRepository.getShruthis();
        rxBus = ((YakshaNaadaApplication) getActivity().getApplication()).bus();

        ShruthiAdapter shruthiAdapter = new ShruthiAdapter(shruthis, rxBus);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(shruthiAdapter);

        MobileAds.initialize(getContext(), getString(R.string.admob_app_id));

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        rxBus.toObservable()
                .subscribe(object -> {
                    if (object instanceof Shruthi) {
                        playShruthi((Shruthi) object);
                    }
                });

        playerManager = PlayerManager.getInstance();
        playerManager.init(getContext(), rxBus);


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

    private void playShruthi(Shruthi shruthi) {
        playerManager.release();
        playerManager.setShruthi(shruthi);
        shruthiTitle.setText(shruthi.getTitle());
        playerEvent(new Event(shruthi, Event.PLAYING));
    }


}