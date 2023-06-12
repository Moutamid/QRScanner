package com.moutamid.qr.scanner.generator.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.moutamid.qr.scanner.generator.interfaces.ButtonItemClickListener;
import com.moutamid.qr.scanner.generator.Model.ButtonModel;
import com.moutamid.qr.scanner.generator.R;

import java.util.List;

public class ButtonResultAdapter extends RecyclerView.Adapter<ButtonResultAdapter.ButtonResultViewHolder>{

    private final List<ButtonModel> resultList;
    private final ButtonItemClickListener mListner;

    public ButtonResultAdapter(Context context,List<ButtonModel> resultList) {
        this.resultList = resultList;
        mListner = (ButtonItemClickListener) context;
    }

    @NonNull
    @Override
    public ButtonResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.
                from(parent.getContext()).inflate(R.layout.button_result_recycler_layout, parent,false);
        return new ButtonResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ButtonResultViewHolder holder, int position) {
        ButtonModel resultData = resultList.get(position);

        holder.tvButtonResult.setText(resultData.getBt_name());
        holder.buttonCard.setBackgroundColor(Color.parseColor(resultData.getColor()));

        holder.buttonCard.setOnClickListener(v -> mListner.clickedItemButton(v,holder.getAdapterPosition(),resultData.getBt_name()));
    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }

    public static class ButtonResultViewHolder extends RecyclerView.ViewHolder{

        private final TextView tvButtonResult;
        private final CardView buttonCard;

        public ButtonResultViewHolder(@NonNull View v) {
            super(v);

            tvButtonResult = v.findViewById(R.id.tv_button_result);
            buttonCard=v.findViewById(R.id.bt_save);


        }
    }
}
