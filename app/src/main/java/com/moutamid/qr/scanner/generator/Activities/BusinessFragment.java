package com.moutamid.qr.scanner.generator.Activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.moutamid.qr.scanner.generator.R;
import com.moutamid.qr.scanner.generator.adapter.CardMainAdapter;

import java.util.ArrayList;
import java.util.List;

public class BusinessFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Integer> cardList;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.business_fragment, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        cardList = new ArrayList<>();
        loadData();
        return view;
    }

    private void loadData() {
        cardList.add(R.drawable.ic_gen_card_email_f_2);
        cardList.add(R.drawable.ic_gen_card_address_f_4);
        cardList.add(R.drawable.ic_gen_card_isbn_b_2);
        cardList.add(R.drawable.ic_gen_card_txt_f_2);
        cardList.add(R.drawable.ic_gen_card_geo_b_1);
        cardList.add(R.drawable.ic_gen_card_social_b_2);
        cardList.add(R.drawable.ic_gen_card_pro_b_3);
        cardList.add(R.drawable.ic_card_isbn_3);
        cardList.add(R.drawable.ic_gen_card_pro_b_1);
        cardList.add(R.drawable.ic_gen_card_sms_b_2);
        cardList.add(R.drawable.ic_gen_card_url_b_2);
        cardList.add(R.drawable.ic_gen_card_wifi_b_2);

        CardMainAdapter adapter = new CardMainAdapter(cardList,getActivity());
        recyclerView.setAdapter(adapter);
    }
}
