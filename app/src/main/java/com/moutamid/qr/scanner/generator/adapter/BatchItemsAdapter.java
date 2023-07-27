package com.moutamid.qr.scanner.generator.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moutamid.qr.scanner.generator.Model.ResultModel;
import com.moutamid.qr.scanner.generator.R;
import com.moutamid.qr.scanner.generator.interfaces.BatchItemClick;

import java.util.ArrayList;

public class BatchItemsAdapter extends RecyclerView.Adapter<BatchItemsAdapter.BatchVH> {
    Context context;
    ArrayList<ResultModel> list;
    BatchItemClick click;

    public BatchItemsAdapter(Context context, ArrayList<ResultModel> list, BatchItemClick click) {
        this.context = context;
        this.list = list;
        this.click = click;
    }

    @NonNull
    @Override
    public BatchVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BatchVH(LayoutInflater.from(context).inflate(R.layout.batch_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BatchVH holder, int position) {
        ResultModel model = list.get(holder.getAbsoluteAdapterPosition());

        if (model.getFormat() == -1){
           holder.qrBar.setText("QrCode");
        } else {
            holder.qrBar.setText("Barcode");
        }

        holder.result.setText(model.getRawData());

        holder.itemView.setOnClickListener(v -> {
            click.onClick(list.get(holder.getAbsoluteAdapterPosition()));
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class BatchVH extends RecyclerView.ViewHolder {
        TextView result, qrBar;
        public BatchVH(@NonNull View itemView) {
            super(itemView);
            result = itemView.findViewById(R.id.result);
            qrBar = itemView.findViewById(R.id.qrBar);
        }
    }

}
