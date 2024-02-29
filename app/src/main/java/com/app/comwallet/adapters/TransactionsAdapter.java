package com.app.comwallet.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.app.comwallet.R;
import com.app.comwallet.databinding.RowTransactionsItemBinding;
import com.app.comwallet.fragments.TransactionDetailsFragment;
import com.app.comwallet.models.Transaction;
import com.app.comwallet.utilities.AppUtils;


import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.ViewHolder> {

    Context context;
    ArrayList<Transaction> arrayList;
    String coinPrice;
    NumberFormat numberFormat;


    private final OnItemClicked onItemClicked;

    public interface OnItemClicked {
        void onClick(Transaction transaction);
    }

    public TransactionsAdapter(Context context, ArrayList<Transaction> arrayList, String coinPrice, OnItemClicked onItemClicked) {
        this.context = context;
        this.arrayList = arrayList;
        this.coinPrice = coinPrice;
        this.numberFormat = NumberFormat.getCurrencyInstance(Locale.US);
        this.onItemClicked = onItemClicked;
    }

    @NonNull
    @Override
    public TransactionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TransactionsAdapter.ViewHolder(RowTransactionsItemBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionsAdapter.ViewHolder holder, int position) {

        Transaction model = arrayList.get(position);
        holder.binding.methodTw.setText(StringUtils.capitalize(StringUtils.capitalize(StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(model.getMethod()), StringUtils.SPACE))));
        holder.binding.dateTw.setText(Transaction.getFormattedTimestamp(model.getTimestamp()));
        holder.binding.amountTw.setText(AppUtils.formatAmount(Long.parseLong(model.getAmount())));

        if (Objects.equals(model.getMethod(), "addStake")) {
            holder.binding.methodImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.round_add_circle_24));
        } else if (Objects.equals(model.getMethod(), "removeStake")) {
            holder.binding.methodImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.round_remove_circle_outline_24));
        } else if (Objects.equals(model.getMethod(), "transfer")) {
            holder.binding.methodImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.round_swap_horiz_24));
        } else {
            holder.binding.methodImg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.shiny_logo));
        }

        double comAmount = (double) Long.parseLong(model.getAmount()) / 1_000_000_000;

        double amountPrice = comAmount * Double.parseDouble(coinPrice);
        holder.binding.amountPriceTw.setText(numberFormat.format(amountPrice));

        if (!model.isSigned()) {
            // Set the drawable at the end of the TextView
            holder.binding.methodTw.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(holder.binding.getRoot().getContext(), R.drawable.baseline_error_outline_24), null);
        }

        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClicked.onClick(model);
            }
        });


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public void setArrayList(ArrayList<Transaction> arrayList) {
        this.arrayList = arrayList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final RowTransactionsItemBinding binding;

        public ViewHolder(@NonNull RowTransactionsItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
