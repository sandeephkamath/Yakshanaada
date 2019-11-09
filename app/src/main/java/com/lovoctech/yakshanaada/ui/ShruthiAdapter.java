package com.lovoctech.yakshanaada.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lovoctech.yakshanaada.R;
import com.lovoctech.yakshanaada.RxBus;
import com.lovoctech.yakshanaada.model.Shruthi;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShruthiAdapter extends RecyclerView.Adapter<ShruthiAdapter.ShruthiHolder> {

    private List<Shruthi> shruthis;
    private RxBus rxBus;

    public ShruthiAdapter(List<Shruthi> shruthis, RxBus rxBus) {
        this.shruthis = shruthis;
        this.rxBus = rxBus;
    }

    @NonNull
    @Override
    public ShruthiHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_shruthi, parent, false);
        return new ShruthiHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ShruthiHolder holder, int position) {
        Shruthi shruthi = shruthis.get(position);
        holder.title.setText(shruthi.getTitle() + "\n" + shruthi.getDescription());
        holder.itemView.setOnClickListener(view -> rxBus.send(shruthi));
    }

    @Override
    public int getItemCount() {
        if (shruthis != null) {
            return shruthis.size();
        }
        return 0;
    }

    class ShruthiHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title)
        TextView title;

        ShruthiHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
