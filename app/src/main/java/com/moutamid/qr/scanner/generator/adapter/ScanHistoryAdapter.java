package com.moutamid.qr.scanner.generator.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.moutamid.qr.scanner.generator.R;
import com.moutamid.qr.scanner.generator.interfaces.HistoryItemClickListner;
import com.moutamid.qr.scanner.generator.qrscanner.History;
import com.moutamid.qr.scanner.generator.utils.formates.EMail;
import com.moutamid.qr.scanner.generator.utils.formates.GeoInfo;
import com.moutamid.qr.scanner.generator.utils.formates.IEvent;
import com.moutamid.qr.scanner.generator.utils.formates.SMS;
import com.moutamid.qr.scanner.generator.utils.formates.Social;
import com.moutamid.qr.scanner.generator.utils.formates.Telephone;
import com.moutamid.qr.scanner.generator.utils.formates.Url;
import com.moutamid.qr.scanner.generator.utils.formates.VCard;
import com.moutamid.qr.scanner.generator.utils.formates.Wifi;

import java.util.List;

public class ScanHistoryAdapter extends RecyclerView.Adapter<ScanHistoryAdapter.HistoryViewHolder>  {

    private final List<History> historyDataList;
    private final HistoryItemClickListner mListner;
    public ScanHistoryAdapter(List<History> historyDataList, HistoryItemClickListner mListner) {
        this.historyDataList = historyDataList;
        this.mListner = mListner;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_row_layout, parent, false);
        return new ScanHistoryAdapter.HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {

        String history = historyDataList.get(position).getData();
        //String lines[] = history.split("\\r?\\n");

        String type= historyDataList.get(position).getType();

        switch (type) {
            case "barcode":
                holder.tv1.setText(history);
                holder.icon.setImageResource(R.drawable.barcode);
                break;
        }

        holder.cardView.setOnClickListener((View v) -> {
            mListner.clickedItem(v, holder.getAdapterPosition(), historyDataList.get(position).getType(), history);
        });
    }

    public History getHistory(int i){
        return historyDataList.get(i);
    }

    @Override
    public int getItemCount() {
        return historyDataList.size();
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder{

        private final TextView tv1;
        private final ImageView icon;
        private final CardView cardView;

        public HistoryViewHolder(@NonNull View v) {
            super(v);
            tv1 = v.findViewById(R.id.tv1);
            icon = v.findViewById(R.id.icon_image_history);
            cardView = v.findViewById(R.id.cardView_history);
            ImageView imgDelete = v.findViewById(R.id.btn_delete_item);

            imgDelete.setOnClickListener(v1 -> {
                mListner.deleteSingleItem(getHistory(getAdapterPosition()), getAdapterPosition());
            });

        }
    }

    }
