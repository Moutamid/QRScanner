package com.moutamid.qr.scanner.generator.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.moutamid.qr.scanner.generator.R;
import java.util.List;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ResultViewHolder> {

    private final List<String> resultList;

    public ResultAdapter(List<String> resultList) {
        this.resultList = resultList;
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.
                from(parent.getContext()).inflate(R.layout.result_data_layout, parent,false);
        return new ResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {

        String resultData = resultList.get(position);
        holder.tvResult.setText(resultData);

    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }

    public static class ResultViewHolder extends RecyclerView.ViewHolder{

        private final TextView tvResult;

        public ResultViewHolder(@NonNull View v) {
            super(v);

            tvResult = v.findViewById(R.id.tv_result);

        }
    }

}