package com.moutamid.qr.scanner.generator.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.moutamid.qr.scanner.generator.interfaces.ButtonItemClickListener;

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
        cardList.add(R.drawable.ic_card_calender_1);
        cardList.add(R.drawable.ic_card_tel_1);
        cardList.add(R.drawable.ic_card_email_2);
        cardList.add(R.drawable.ic_card_txt_2);
        cardList.add(R.drawable.ic_card_geo_2);
        cardList.add(R.drawable.ic_card_url_2);
        cardList.add(R.drawable.ic_card_url_3);
        cardList.add(R.drawable.ic_card_wifi_1);
        cardList.add(R.drawable.ic_card_social_2);
        cardList.add(R.drawable.ic_card_social_3);
        cardList.add(R.drawable.ic_card_sms_2);
        cardList.add(R.drawable.ic_card_email_3);

        CardMainAdapter adapter = new CardMainAdapter(cardList,getActivity());
        recyclerView.setAdapter(adapter);
        adapter.setButtonItemClickListener(new ButtonItemClickListener() {
            @Override
            public void clickedItem(View view, int position) {
                if (position == 0) {
                    Intent intent = new Intent(getActivity(), CardCalendarActivity.class);
                    startActivity(intent);
                }
                if (position == 1) {
                    Intent intent = new Intent(getActivity(), CardTelActivity.class);
                    startActivity(intent);
                }
                if (position == 2) {
                    Intent intent = new Intent(getActivity(), CardEmailActivity.class);
                    startActivity(intent);
                }
                if (position == 3) {
                    Intent intent = new Intent(getActivity(), CardTxtActivity.class);
                    startActivity(intent);
                }
                if (position == 4) {
                    Intent intent = new Intent(getActivity(), CardGeoActivity.class);
                    startActivity(intent);
                }
                if (position == 5) {
                    Intent intent = new Intent(getActivity(), CardUrlActivity.class);
                    startActivity(intent);
                }
                if (position == 6) {
                    Intent intent = new Intent(getActivity(), CardUrl2Activity.class);
                    startActivity(intent);
                }
                if (position == 7) {
                    Intent intent = new Intent(getActivity(), CardWiFiActivity.class);
                    startActivity(intent);
                }
                if (position == 8) {
                    Intent intent = new Intent(getActivity(), CardSocialActivity.class);
                    startActivity(intent);
                }
                if (position == 9) {
                    Intent intent = new Intent(getActivity(), CardSocial2Activity.class);
                    startActivity(intent);
                }
                if (position == 10) {
                    Intent intent = new Intent(getActivity(), CardSMSActivity.class);
                    startActivity(intent);
                }
                if (position == 11) {
                    Intent intent = new Intent(getActivity(), CardEmail2Activity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void clickedItemButton(View view, int position, String type) {

            }
        });
    }
}
