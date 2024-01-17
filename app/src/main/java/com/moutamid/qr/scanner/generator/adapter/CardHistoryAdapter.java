package com.moutamid.qr.scanner.generator.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.moutamid.qr.scanner.generator.Activities.CardGeneratedResult;
import com.moutamid.qr.scanner.generator.Model.CardHistoryModel;
import com.moutamid.qr.scanner.generator.R;
import com.moutamid.qr.scanner.generator.interfaces.HistoryItemClickListner;
import com.moutamid.qr.scanner.generator.qrscanner.History;
import com.moutamid.qr.scanner.generator.utils.formates.BusinessCard;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CardHistoryAdapter extends RecyclerView.Adapter<CardHistoryAdapter.HistoryViewHolder>  {
    private Context context;
    private final List<CardHistoryModel> historyDataList;
    private final HistoryItemClickListner mListner;

    public CardHistoryAdapter(Context context, List<CardHistoryModel> historyDataList, HistoryItemClickListner mListner) {
        this.context = context;
        this.historyDataList = historyDataList;
        this.mListner = mListner;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_history_layout, parent, false);
        return new CardHistoryAdapter.HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        CardHistoryModel model = historyDataList.get(position);

        String history = model.getHistory().getData();
        //String lines[] = history.split("\\r?\\n");

        String type= model.getHistory().getType();

        if (type.equals("card")){
            BusinessCard card = new BusinessCard();
            card.parseSchema(history);
            holder.tv1.setText(card.getTitle());
            holder.tv2.setText(card.getContent());

            SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM hh:mm");
            String date = formatter.format(card.getTimestamp()*1000);
            holder.date.setText(date);
            Log.d("date",""+card.getTimestamp());
        }

        holder.imgDelete.setOnClickListener(v -> {
            mListner.deleteSingleItem(model.getHistory(), holder.getAbsoluteAdapterPosition());
            historyDataList.remove(holder.getAbsoluteAdapterPosition());
        });

        holder.cardView.setOnClickListener((View v) -> {
            byte[] imageByte = bitmapToByteArray(model.getBitmap());
            byte[] imageByte2 = bitmapToByteArray(model.getBitmap2());

            Intent intent = new Intent(context, CardGeneratedResult.class);
            intent.putExtra("image1", imageByte);
            intent.putExtra("image2", imageByte2);
            intent.putExtra("saveData", 2);
            context.startActivity(intent);
        });
    }

    public static byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    @Override
    public int getItemCount() {
        return historyDataList.size();
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder{

        private final TextView tv1,tv2,date;
        private final CardView cardView;
        ImageView imgDelete;
        public HistoryViewHolder(@NonNull View v) {
            super(v);
            tv1 = v.findViewById(R.id.tv1);
            tv2 = v.findViewById(R.id.tv2);
            date = v.findViewById(R.id.date);
            cardView = v.findViewById(R.id.cardView_history);
            imgDelete = v.findViewById(R.id.btn_delete_item);

        }
    }

    }
