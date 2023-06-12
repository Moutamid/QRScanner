package free.qr.code.scanner.generator.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.consoliads.mediation.ConsoliAds;
import com.consoliads.mediation.constants.NativePlaceholderName;

import free.qr.code.scanner.generator.Activities.ScanResultActivity;
import free.qr.code.scanner.generator.R;
import free.qr.code.scanner.generator.adapter.HistoryAdapter;
import free.qr.code.scanner.generator.interfaces.HistoryItemClickListner;
import free.qr.code.scanner.generator.qrscanner.History;
import free.qr.code.scanner.generator.qrscanner.HistoryVM;
import free.qr.code.scanner.generator.utils.formates.EMail;
import free.qr.code.scanner.generator.utils.formates.GeoInfo;
import free.qr.code.scanner.generator.utils.formates.IEvent;
import free.qr.code.scanner.generator.utils.formates.SMS;
import free.qr.code.scanner.generator.utils.formates.Social;
import free.qr.code.scanner.generator.utils.formates.Telephone;
import free.qr.code.scanner.generator.utils.formates.Url;
import free.qr.code.scanner.generator.utils.formates.VCard;
import free.qr.code.scanner.generator.utils.formates.Wifi;

public class CreateFragment extends Fragment implements HistoryItemClickListner {

    private HistoryVM historyVM;
    private RecyclerView historyRecyclerView;
    private HistoryAdapter adapter;
    private TextView tvIsEmpty;
    private boolean isEmpty = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.create_fragment, container, false);
        historyVM = new ViewModelProvider(CreateFragment.this).get(HistoryVM.class);
        historyRecyclerView = view.findViewById(R.id.history_recyclerview);
        historyRecyclerView.setHasFixedSize(true);
        tvIsEmpty = view.findViewById(R.id.tv_is_empty);
        getHistoryData();
        return view;
    }
    private void getHistoryData() {
        historyVM.getHistoryData().observe(getActivity(), histories -> {
            if (histories.size() == 0){
                tvIsEmpty.setVisibility(View.VISIBLE);
                isEmpty = true;
            }
            adapter = new HistoryAdapter(histories, this);
            historyRecyclerView.setAdapter(adapter);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            mLayoutManager.setReverseLayout(true);
            mLayoutManager.setStackFromEnd(true);
            historyRecyclerView.setLayoutManager(mLayoutManager);

        });
    }

    @Override
    public void clickedItem(View view, int position,String type,String history) {
        Intent intent = new Intent(getActivity(), ScanResultActivity.class);
        switch (type) {
            case "contact":
                VCard vCard = new VCard();
                vCard.parseSchema(history);
                intent.putExtra("type", "VCard");
                intent.putExtra("vCard", vCard);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                }
                break;
            case "email":
                EMail eMail = new EMail();
                eMail.parseSchema(history);
                intent.putExtra("type", "EMail");
                intent.putExtra("eMail", eMail);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                }
                break;
            case "event":
                IEvent iEvent = new IEvent();
                iEvent.parseSchema(history);
                intent.putExtra("type", "Event");
                intent.putExtra("event", iEvent);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                }
                break;
            case "location":
                GeoInfo geoInfo = new GeoInfo();
                geoInfo.parseSchema(history);
                intent.putExtra("type", "GeoInfo");
                intent.putExtra("geoInfo", geoInfo);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                }
                break;
            case "phone":
                Telephone telephone = new Telephone();
                telephone.parseSchema(history);
                intent.putExtra("type", "telephone");
                intent.putExtra("phone", telephone);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                }
                break;
            case "sms":
                SMS sms = new SMS();
                sms.parseSchema(history);
                intent.putExtra("type", "Sms");
                intent.putExtra("sms", sms);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                }
                break;
            case "text":
                intent.putExtra("type", "Text");
                intent.putExtra("text", history);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                }
                break;
            case "url":
                Url url = new Url();
                url.parseSchema(history);
                intent.putExtra("type", "url");
                intent.putExtra("Url", url);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                }
                break;
            case "wifi":
                Wifi wifi = new Wifi();
                wifi.parseSchema(history);
                intent.putExtra("type", "wifi");
                intent.putExtra("Wifi", wifi);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                }
                break;
            case "social":
                final Social social = new Social();
                social.setUrl(history);
                intent.putExtra("type", "Social");
                intent.putExtra("social", social);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                }
                break;
            case "barcode":
                intent.putExtra("type", "Barcode");
                intent.putExtra("barcode", history);
                startActivity(intent);
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                }
                break;
        }
    }

    @Override
    public void deleteSingleItem(History history, int i) {
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        adb.setTitle("Do you want to delete?");
        adb.setIcon(android.R.drawable.ic_dialog_alert);
        adb.setPositiveButton("OK", (dialog, which) -> {
            historyVM.deleteSingleItem(history);
            adapter.notifyItemRemoved(i);
            if (!getPurchaseSharedPreference()) {
                ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
            }
        });
        adb.setNegativeButton("Cancel", (dialog, which) -> {
        });
        adb.show();

    }

    public void backHistory(View view) {
        if (!getPurchaseSharedPreference()) {
            ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
        }

    }

    public void deleteAllHistory(View view) {

        if (isEmpty){
            Toast.makeText(getActivity(), "Nothing to delete!", Toast.LENGTH_SHORT).show();
        } else{

            AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
            adb.setTitle("Do you want to delete?");
            adb.setIcon(android.R.drawable.ic_dialog_alert);
            adb.setPositiveButton("OK", (dialog, which) -> {
                historyVM.deleteAllHistory();
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                }
            });
            adb.setNegativeButton("Cancel", (dialog, which) -> {
            });
            adb.show();
        }
    }

    public boolean getPurchaseSharedPreference(){
        SharedPreferences prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        return prefs.getBoolean(this.getString(R.string.adsubscribed), false);

    }
}
