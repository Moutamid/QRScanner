package com.moutamid.qr.scanner.generator.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
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

        holder.bind(position);

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
        public ButtonMainViewHolder(@NonNull View itemName) {
            super(itemName);

            tvMain= itemName.findViewById(R.id.name);
            imageView= itemName.findViewById(R.id.flag);
            radioButton= itemName.findViewById(R.id.radio);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int clickedPosition = getAdapterPosition();
                    if (clickedPosition != RecyclerView.NO_POSITION) {
                        // Update the selected position and notify the adapter of the changes
                        setSelectedPosition(clickedPosition);
                        notifyDataSetChanged();
                    }
                }
            });

            radioButton.setOnClickListener(v -> {
                int clickedPosition = getAdapterPosition();
                if (clickedPosition != RecyclerView.NO_POSITION) {
                    // Update the selected position and notify the adapter of the changes
                    setSelectedPosition(clickedPosition);
                    notifyDataSetChanged();
                }
            });

            radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (buttonItemClickListener != null){
                        buttonItemClickListener.clickedItem(buttonView,getAdapterPosition());
                    }
                }
            });

        }

        void bind(int position) {
            radioButton.setChecked(position == selectedPosition);
        }

    }


    private int selectedPosition = -1;

    private void setSelectedPosition(int position) {
        selectedPosition = position;
    }

    public void setButtonItemClickListener(ButtonItemClickListener itemClickListener){
        this.buttonItemClickListener = itemClickListener;
    }
}
