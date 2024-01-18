package com.moutamid.qr.scanner.generator.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.consoliads.mediation.ConsoliAds;
import com.consoliads.mediation.constants.NativePlaceholderName;
import com.fxn.stash.Stash;
import com.google.android.material.button.MaterialButton;
import com.moutamid.qr.scanner.generator.Activities.BusinessFragment;
import com.moutamid.qr.scanner.generator.Activities.MainActivity;
import com.moutamid.qr.scanner.generator.Activities.QRScanFragment;
import com.moutamid.qr.scanner.generator.Activities.ScanResultActivity;
import com.moutamid.qr.scanner.generator.Constants;
import com.moutamid.qr.scanner.generator.Model.CardHistoryModel;
import com.moutamid.qr.scanner.generator.R;
import com.moutamid.qr.scanner.generator.adapter.CardHistoryAdapter;
import com.moutamid.qr.scanner.generator.adapter.HistoryAdapter;
import com.moutamid.qr.scanner.generator.adapter.ScanHistoryAdapter;
import com.moutamid.qr.scanner.generator.interfaces.HistoryItemClickListner;
import com.moutamid.qr.scanner.generator.qrscanner.History;
import com.moutamid.qr.scanner.generator.qrscanner.HistoryVM;
import com.moutamid.qr.scanner.generator.utils.formates.EMail;
import com.moutamid.qr.scanner.generator.utils.formates.GeoInfo;
import com.moutamid.qr.scanner.generator.utils.formates.IEvent;
import com.moutamid.qr.scanner.generator.utils.formates.SMS;
import com.moutamid.qr.scanner.generator.utils.formates.Social;
import com.moutamid.qr.scanner.generator.utils.formates.Telephone;
import com.moutamid.qr.scanner.generator.utils.formates.Url;
import com.moutamid.qr.scanner.generator.utils.formates.VCard;
import com.moutamid.qr.scanner.generator.utils.formates.Wifi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;


public class CardsFragment extends Fragment implements HistoryItemClickListner {

    private HistoryVM historyVM;
    private RecyclerView historyRecyclerView;
    private CardHistoryAdapter adapter;
    private LinearLayout tvIsEmpty;
    private RelativeLayout recyclerLayout;
    private boolean isEmpty = false;
    private SharedPreferences prefs;
    private MaterialButton deleteImg;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(requireActivity());

        boolean theme = prefs.getBoolean("theme",false);
        if (theme){
            AppCompatDelegate
                    .setDefaultNightMode(
                            AppCompatDelegate
                                    .MODE_NIGHT_YES);

        }else {

            AppCompatDelegate
                    .setDefaultNightMode(
                            AppCompatDelegate
                                    .MODE_NIGHT_NO);

        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.card_fragment, container, false);
        historyVM = new ViewModelProvider(CardsFragment.this).get(HistoryVM.class);
        historyRecyclerView = view.findViewById(R.id.history_recyclerview);
        historyRecyclerView.setHasFixedSize(false);
        tvIsEmpty = view.findViewById(R.id.tv_is_empty);
        deleteImg = view.findViewById(R.id.all_delete_history);
        OnButtonClickListener buttonClickListener = (OnButtonClickListener) requireContext();
        recyclerLayout = view.findViewById(R.id.recyclerLayout);
        Button scanNow = view.findViewById(R.id.scanNow);
        scanNow.setOnClickListener(v -> {
            buttonClickListener.onButtonClicked();
        });

        deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAllHistory();
            }
        });
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        getLocale();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getHistoryData();
    }

    public interface OnButtonClickListener {
        void onButtonClicked();
    }


    private void getLocale(){

        String lang = prefs.getString("lang","");
        String name = prefs.getString("lang_name","");
        //   languageTxt.setText(name);
        setLocale(lang,name);
    }

    private void setLocale(String lng,String name) {

        Locale locale = new Locale(lng);
        Locale.setDefault(locale);

        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getResources().updateConfiguration(configuration,getResources().getDisplayMetrics());

    }
    private void getHistoryData() {
        ArrayList<CardHistoryModel> historyList = Stash.getArrayList(Constants.CARD, CardHistoryModel.class);
        if (historyList.size() ==0 ){
            tvIsEmpty.setVisibility(View.VISIBLE);
            recyclerLayout.setVisibility(View.GONE);
            isEmpty = true;
        } else {
//            Collections.reverse(historyList);
            tvIsEmpty.setVisibility(View.GONE);
            recyclerLayout.setVisibility(View.VISIBLE);
            adapter = new CardHistoryAdapter(requireContext(), historyList, this);
            historyRecyclerView.setAdapter(adapter);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            mLayoutManager.setReverseLayout(true);
            mLayoutManager.setStackFromEnd(true);
            historyRecyclerView.setLayoutManager(mLayoutManager);
        }
    }

    @Override
    public void editItem(String type, String data) {

    }

    @Override
    public void clickedItem(View view, int position,String type,String history) {

    }

    @Override
    public void deleteSingleItem(History history, int i) {
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        adb.setTitle("Delete History");
        adb.setMessage("Are you sure you want to delete?");
        adb.setPositiveButton("Yes", (dialog, which) -> {
            ArrayList<CardHistoryModel> historyList = Stash.getArrayList(Constants.CARD, CardHistoryModel.class);
            historyList.remove(i);
            Stash.put(Constants.CARD, historyList);
            adapter.notifyItemRemoved(i);
            if (historyList.size() ==0 ){
                tvIsEmpty.setVisibility(View.VISIBLE);
                recyclerLayout.setVisibility(View.GONE);
                isEmpty = true;
            }
            if (!getPurchaseSharedPreference()) {
                ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
            }
        });
        adb.setNegativeButton("No", (dialog, which) -> {});
        adb.show();

    }

    public void backHistory(View view) {
        if (!getPurchaseSharedPreference()) {
            ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
        }

    }

    public void deleteAllHistory() {

        if (isEmpty){
            Toast.makeText(getActivity(), "Nothing to delete!", Toast.LENGTH_SHORT).show();
        } else{

            AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
            adb.setTitle("Delete History");
            adb.setMessage("Are you sure you want to delete all the history?");
            adb.setPositiveButton("Yes", (dialog, which) -> {
                ArrayList<CardHistoryModel> historyList = Stash.getArrayList(Constants.CARD, CardHistoryModel.class);
                historyList.clear();
                Stash.put(Constants.CARD, historyList);
//                historyVM.deleteAllHistory();
                adapter = new CardHistoryAdapter(requireContext(), historyList, this);
                historyRecyclerView.setAdapter(adapter);
                if (historyList.size() ==0 ){
                    tvIsEmpty.setVisibility(View.VISIBLE);
                    recyclerLayout.setVisibility(View.GONE);
                    isEmpty = true;
                }
                if (!getPurchaseSharedPreference()) {
                    ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
                }
            });
            adb.setNegativeButton("No", (dialog, which) -> {
            });
            adb.show();
        }
    }

    public boolean getPurchaseSharedPreference(){
        SharedPreferences prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        return prefs.getBoolean(this.getString(R.string.adsubscribed), false);

    }
}
