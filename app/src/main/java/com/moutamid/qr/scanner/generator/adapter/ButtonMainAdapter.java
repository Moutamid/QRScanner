package com.moutamid.qr.scanner.generator.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.moutamid.qr.scanner.generator.interfaces.ButtonItemClickListener;
import com.moutamid.qr.scanner.generator.Model.ButtonMainModel;
import com.moutamid.qr.scanner.generator.R;

import java.util.List;

public class ButtonMainAdapter extends  RecyclerView.Adapter<ButtonMainAdapter.ButtonMainViewHolder>{
    private String[] listItem;
    private Integer[] images;
    private ButtonItemClickListener buttonItemClickListener;
    private Context context;

    public ButtonMainAdapter(Context context,Integer[] images,String[] listItem) {

        this.context = context;
        this.images = images;
        this.listItem = listItem;
    }

    @NonNull
    @Override
    public ButtonMainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.
                from(parent.getContext()).inflate(R.layout.custom_language, parent,false);
        return new ButtonMainAdapter.ButtonMainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ButtonMainViewHolder holder, int position) {


        holder.tvMain.setText(listItem[position]);
        holder.imageView.setImageResource(images[position]);

    }

    @Override
    public int getItemCount() {
        return images.length;
    }

    public class ButtonMainViewHolder extends RecyclerView.ViewHolder{

        private TextView tvMain;
        private ImageView imageView;
        private RadioButton radioButton;

        @SuppressLint("CutPasteId")
        public ButtonMainViewHolder(@NonNull View v) {
            super(v);

            tvMain= v.findViewById(R.id.name);
            imageView= v.findViewById(R.id.flag);
            radioButton= v.findViewById(R.id.radio);
            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (buttonItemClickListener != null){
                        buttonItemClickListener.clickedItem(view,getAdapterPosition());
                    }
                }
            });

        }
    }

    public void setButtonItemClickListener(ButtonItemClickListener itemClickListener){
        this.buttonItemClickListener = itemClickListener;
    }
}
