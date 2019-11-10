package com.lovoctech.yakshanaada.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lovoctech.yakshanaada.R;
import com.lovoctech.yakshanaada.RxBus;
import com.lovoctech.yakshanaada.model.Tanpur;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TanpurAdapter extends RecyclerView.Adapter<TanpurAdapter.TanpurHolder> {

    private List<Tanpur> tanpurs;
    private RxBus rxBus;

    public TanpurAdapter(List<Tanpur> shruthis, RxBus rxBus) {
        this.tanpurs = shruthis;
        this.rxBus = rxBus;
    }

    @NonNull
    @Override
    public TanpurHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_shruthi, parent, false);
        return new TanpurHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TanpurHolder holder, int position) {
        Tanpur tanpur = tanpurs.get(position);
        holder.title.setText(tanpur.getTitle());
        holder.itemView.setOnClickListener(view -> rxBus.send(tanpur));
    }

    @Override
    public int getItemCount() {
        if (tanpurs != null) {
            return tanpurs.size();
        }
        return 0;
    }

    class TanpurHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title)
        TextView title;

        TanpurHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
