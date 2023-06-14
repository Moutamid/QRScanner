package com.moutamid.qr.scanner.generator.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moutamid.qr.scanner.generator.Model.ButtonMainModel;
import com.moutamid.qr.scanner.generator.R;
import com.moutamid.qr.scanner.generator.interfaces.ButtonItemClickListener;

import java.util.List;

public class CardMainAdapter extends  RecyclerView.Adapter<CardMainAdapter.ButtonMainViewHolder>{
    private final List<Integer> btMainList;
    private Context mContext;
    private ButtonItemClickListener buttonItemClickListener;

    public CardMainAdapter(List<Integer> btMainList, Context context) {
        this.btMainList = btMainList;
        mContext = context;
    }

    @NonNull
    @Override
    public ButtonMainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.
                from(mContext).inflate(R.layout.business, parent,false);
        return new CardMainAdapter.ButtonMainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ButtonMainViewHolder holder, int position) {
        holder.imageView.setImageResource(btMainList.get(position));
    }

    @Override
    public int getItemCount() {
        return btMainList.size();
    }

    public class ButtonMainViewHolder extends RecyclerView.ViewHolder{

        private final TextView tvMain;
        private final ImageView imageView;

        @SuppressLint("CutPasteId")
        public ButtonMainViewHolder(@NonNull View v) {
            super(v);

            tvMain= v.findViewById(R.id.edit);
            imageView= v.findViewById(R.id.imageView);


                v.setOnClickListener(v1 -> {
                    if (buttonItemClickListener != null){
                        buttonItemClickListener.clickedItem(v,getAdapterPosition());
                    }
                });


        }
    }

    public void setButtonItemClickListener(ButtonItemClickListener itemClickListener){
        this.buttonItemClickListener = itemClickListener;

    }
}
