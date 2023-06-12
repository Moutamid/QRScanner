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

import com.moutamid.qr.scanner.generator.interfaces.ButtonItemClickListener;
import com.moutamid.qr.scanner.generator.Model.ButtonMainModel;
import com.moutamid.qr.scanner.generator.R;

import java.util.List;

public class ButtonMainAdapter extends  RecyclerView.Adapter<ButtonMainAdapter.ButtonMainViewHolder>{
    private final List<ButtonMainModel> btMainList;
    private final ButtonItemClickListener mListner;
    private int select_position = 0;

    public ButtonMainAdapter(List<ButtonMainModel> btMainList, Context context) {
        this.btMainList = btMainList;
        mListner = (ButtonItemClickListener) context;
    }

    @NonNull
    @Override
    public ButtonMainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.
                from(parent.getContext()).inflate(R.layout.row_main_btn, parent,false);
        return new ButtonMainAdapter.ButtonMainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ButtonMainViewHolder holder, int position) {
        ButtonMainModel tvData = btMainList.get(position);

        holder.tvMain.setText(tvData.getBtName());
        holder.imageView.setImageResource(tvData.getImg());
        if (position == 2 || position == 3) {
            position=select_position;
            holder.tvMain.setTextColor(Color.parseColor("#000000"));
        } else {
            if (select_position == position) {
                holder.tvMain.setTextColor(Color.parseColor("#00ABFF"));
            }
        else{
                holder.tvMain.setTextColor(Color.parseColor("#000000"));
        }
    }
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

            tvMain= v.findViewById(R.id.tv_main);
            imageView= v.findViewById(R.id.img_main);


                v.setOnClickListener(v1 -> {
                    if (select_position != getAdapterPosition()) {
                        mListner.clickedItem(v1, getAdapterPosition());
                        if (getAdapterPosition() == 2 || getAdapterPosition() == 3) {

                        } else {
                            select_position = getAdapterPosition();
                        }
                        notifyDataSetChanged();
                    }
                });


        }
    }
}
