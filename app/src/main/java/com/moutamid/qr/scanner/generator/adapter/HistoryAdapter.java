package com.moutamid.qr.scanner.generator.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.moutamid.qr.scanner.generator.interfaces.HistoryItemClickListner;
import com.moutamid.qr.scanner.generator.qrscanner.History;

import com.moutamid.qr.scanner.generator.R;

import com.moutamid.qr.scanner.generator.utils.formates.EMail;
import com.moutamid.qr.scanner.generator.utils.formates.GeoInfo;
import com.moutamid.qr.scanner.generator.utils.formates.IEvent;
import com.moutamid.qr.scanner.generator.utils.formates.SMS;
import com.moutamid.qr.scanner.generator.utils.formates.Social;
import com.moutamid.qr.scanner.generator.utils.formates.Spotify;
import com.moutamid.qr.scanner.generator.utils.formates.Telephone;
import com.moutamid.qr.scanner.generator.utils.formates.Url;
import com.moutamid.qr.scanner.generator.utils.formates.VCard;
import com.moutamid.qr.scanner.generator.utils.formates.Wifi;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>  {
    private Context context;
    private final List<History> historyDataList;
    private final HistoryItemClickListner mListner;

    public HistoryAdapter(Context context, List<History> historyDataList, HistoryItemClickListner mListner) {
        this.context = context;
        this.historyDataList = historyDataList;
        this.mListner = mListner;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_row_layout, parent, false);
        return new HistoryAdapter.HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {

        String history = historyDataList.get(position).getData();
        //String lines[] = history.split("\\r?\\n");

        String type= historyDataList.get(position).getType();
        boolean scan = historyDataList.get(position).isScan();
        switch (type) {
            case "contact": {
                VCard vCard = new VCard();
                vCard.parseSchema(history);
                String data = "";
                if (vCard.getName() != null) {
                    data = vCard.getName() + "\n";
                }
                if (vCard.getEmail() != null) {
                    data = data + vCard.getEmail() + "\n";
                }
                if (vCard.getPhoneNumber() != null) {
                    data = data + vCard.getPhoneNumber() + "\n";
                }
                if (vCard.getCompany() != null) {
                    data = data + vCard.getCompany() + "\n";
                }
                if (vCard.getNote() != null) {
                    data = data + vCard.getNote() + "\n";
                }
                if (vCard.getAddress() != null) {
                    data = data + vCard.getAddress() + "\n";
                }
                if (vCard.getTitle() != null) {
                    data = data + vCard.getTitle() + "\n";
                }
                if (vCard.getWebsite() != null) {
                    data = data + vCard.getWebsite();
                }
                holder.tv1.setText(data);
                holder.icon.setImageResource(R.drawable.contact);
                break;
            }
            case "email": {
                EMail eMail = new EMail();
                eMail.parseSchema(history);
                String data = "";
                if (eMail.getEmail() != null) {
                    data = eMail.getEmail() + "\n";
                }
                if (eMail.getMailSubject() != null) {
                    data = data + eMail.getMailSubject() + "\n";
                }
                if (eMail.getMailBody() != null) {
                    data = data + eMail.getMailBody();
                }
                holder.tv1.setText(data);
                holder.icon.setImageResource(R.drawable.email);
                break;
            }
            case "event": {
                IEvent iEvent = new IEvent();
                iEvent.parseSchema(history);
                String data = "";
                if (iEvent.getUid() != null) {
                    data = iEvent.getUid() + "\n";
                }
                if (iEvent.getSummary() != null) {
                    data = data + iEvent.getSummary() + "\n";
                }
                if (iEvent.getStart() != null) {
                    data = data + iEvent.getStart() + "\n";
                }
                if (iEvent.getEnd() != null) {
                    data = data + iEvent.getEnd() + "\n";
                }
                if (iEvent.getStamp() != null) {
                    data = data + iEvent.getStamp() + "\n";
                }
                if (iEvent.getOrganizer() != null) {
                    data = data + iEvent.getOrganizer();
                }
                holder.tv1.setText(data);
                holder.icon.setImageResource(R.drawable.event);
                break;
            }
            case "location": {
                GeoInfo geoInfo = new GeoInfo();
                geoInfo.parseSchema(history);
                String data = "";
                if (geoInfo.getPoints().get(0) != null) {
                    data = geoInfo.getPoints().get(0) + "\n";
                }
                if (geoInfo.getPoints().get(1) != null) {
                    data = data + geoInfo.getPoints().get(1) + "\n";
                }
                if (geoInfo.getPoints().get(2) != null) {
                    data = data + geoInfo.getPoints().get(2);
                }
                holder.tv1.setText(data);
                holder.icon.setImageResource(R.drawable.location);
                break;
            }
            case "phone": {
                Telephone telephone = new Telephone();
                telephone.parseSchema(history);
                String data = "";
                if (telephone.getTelephone() != null) {
                    data = telephone.getTelephone();
                }
                holder.tv1.setText(data);
                holder.icon.setImageResource(R.drawable.phone);
                break;
            }
            case "sms": {
                SMS sms = new SMS();
                sms.parseSchema(history);
                String data = "";
                if (sms.getNumber() != null) {
                    data = sms.getNumber() + "\n";
                }
                if (sms.getSubject() != null) {
                    data = data + sms.getSubject();
                }
                holder.tv1.setText(data);
                holder.icon.setImageResource(R.drawable.sms);
                break;
            }
            case "whatsapp": {
                Telephone telephone = new Telephone();
                telephone.parseSchema(history);
                String data = "";
                if (telephone.getTelephone() != null) {
                    data = telephone.getTelephone();
                }
                holder.tv1.setText(data);
                holder.icon.setImageResource(R.drawable.whatsapp);
                break;
            }
            case "spotify": {
                Spotify telephone = new Spotify();
                telephone.parseSchema(history);
                String data = "";
                if (telephone.getName() != null) {
                    data = telephone.getName();
                }
                if (telephone.getSong() != null) {
                    data = telephone.getSong();
                }
                holder.tv1.setText(data);
                holder.icon.setImageResource(R.drawable.spotify);
                break;
            }
            case "viber": {
                Telephone telephone = new Telephone();
                telephone.parseSchema(history);
                String data = "";
                if (telephone.getTelephone() != null) {
                    data = telephone.getTelephone();
                }
                holder.tv1.setText(data);
                holder.icon.setImageResource(R.drawable.viber);
                break;
            }
            case "barcode":
                holder.tv1.setText(history);
                holder.icon.setImageResource(R.drawable.barcode);
                break;
            case "text":
                holder.tv1.setText(history);
                holder.icon.setImageResource(R.drawable.text);
                break;
            case "clipboard":
                holder.tv1.setText(history);
                holder.icon.setImageResource(R.drawable.clipboard);
                break;
            case "url": {
                Url url = new Url();
                url.parseSchema(history);
                String data = "";
                if (url.getUrl() != null) {
                    data = url.getUrl();
                }
                holder.tv1.setText(data);
                holder.icon.setImageResource(R.drawable.url);
                break;
            }
            case "wifi": {
                Wifi wifi = new Wifi();
                wifi.parseSchema(history);
                String data = "";
                if (wifi.getSsid() != null) {
                    data = wifi.getSsid() + "\n";
                }
                if (wifi.getPsk() != null) {
                    data = data + wifi.getPsk() + "\n";
                }
                if (wifi.getAuthentication() != null) {
                    data = data + wifi.getAuthentication();
                }
                holder.tv1.setText(data);
                holder.icon.setImageResource(R.drawable.wifi);
                break;
            }
            case "youtube":
                Social social = new Social();
                if (!history.isEmpty()) {
                    social.parseSchema(history);
                    String data = "";
                    if (social.getUrl() != null) {
                        data = social.getUrl();
                    }
                    holder.tv1.setText(data);
                    holder.icon.setImageResource(R.drawable.youtube);
                }
                break;
            case "insta":
                Social social1 = new Social();
                if (!history.isEmpty()) {
                    social1.parseSchema(history);
                    String data = "";
                    if (social1.getUrl() != null) {
                        data = social1.getUrl();
                    }
                    holder.tv1.setText(data);
                    holder.icon.setImageResource(R.drawable.instagram);
                }
                break;
            case "paypal":
                Social social2 = new Social();
                if (!history.isEmpty()) {
                    social2.parseSchema(history);
                    String data = "";
                    if (social2.getUrl() != null) {
                        data = social2.getUrl();
                    }
                    holder.tv1.setText(data);
                    holder.icon.setImageResource(R.drawable.paypal);
                }
                break;
            case "facebook":
                Social social3 = new Social();
                if (!history.isEmpty()) {
                    social3.parseSchema(history);
                    String data = "";
                    if (social3.getUrl() != null) {
                        data = social3.getUrl();
                    }
                    holder.tv1.setText(data);
                    holder.icon.setImageResource(R.drawable.facebook);
                }
                break;
            case "twitter":
                Social social4 = new Social();
                if (!history.isEmpty()) {


                    social4.parseSchema(history);
                    String data = "";
                    if (social4.getUrl() != null) {
                        data = social4.getUrl();
                    }
                    holder.tv1.setText(data);
                    holder.icon.setImageResource(R.drawable.twitter);
                }
                break;
        }

        holder.cardView.setOnLongClickListener(v -> {
            Log.d("onBindViewHolder", "onBindViewHolder: " + history);
            new AlertDialog.Builder(context)
                    .setMessage("Do you want to edit this item")
                    .setCancelable(true)
                    .setPositiveButton("Yes", (dialog, which) -> {
                        dialog.dismiss();
                        mListner.editItem(historyDataList.get(position).getType(), history);
                    }).setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
            return true;
        });

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
                historyDataList.remove(getAdapterPosition());
            });

        }
    }

    }
