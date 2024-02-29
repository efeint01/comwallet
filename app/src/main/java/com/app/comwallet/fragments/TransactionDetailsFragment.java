package com.app.comwallet.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.comwallet.R;
import com.app.comwallet.databinding.FragmentTransactionDetailsBinding;
import com.app.comwallet.models.Transaction;
import com.app.comwallet.utilities.AppUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.Locale;
import java.util.Objects;


public class TransactionDetailsFragment extends Fragment {

    FragmentTransactionDetailsBinding binding;
    Transaction transaction;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentTransactionDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
    }

    private void initViews() {
        Bundle args = getArguments();
        if (args == null) return;

        binding.backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        transaction = args.getParcelable("transaction");
        if (transaction != null) {

            if (Objects.equals(transaction.getMethod(), "addStake")) {
                binding.methodImg.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.round_add_circle_24));
            } else if (Objects.equals(transaction.getMethod(), "removeStake")) {
                binding.methodImg.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.round_remove_circle_outline_24));
            } else if (Objects.equals(transaction.getMethod(), "transfer")) {
                binding.methodImg.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.round_swap_horiz_24));
            } else {
                binding.methodImg.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_commune));
            }

            binding.methodTw.setText(StringUtils.capitalize(StringUtils.capitalize(StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(transaction.getMethod()), StringUtils.SPACE))));

            if (!transaction.isSigned()) {
                binding.methodTw.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(getContext(), R.drawable.baseline_error_outline_24), null);
            }

            binding.amountTw.setText(AppUtils.formatAmount(Long.parseLong(transaction.getAmount())));
            binding.feeTw.setText(AppUtils.formatAmount(Long.parseLong(transaction.getFee().replace(",", ""))));
            binding.senderTw.setText(transaction.getSender());
            binding.receiverTw.setText(transaction.getReceiver());
            binding.blockNumberTw.setText(String.format(Locale.getDefault(), "%d", transaction.getBlockNumber()));
            binding.dateTw.setText(Transaction.getFormattedTimestampDate(transaction.getTimestamp()));
            binding.timeTw.setText(Transaction.getFormattedTimestampTime(transaction.getTimestamp()));
            binding.hashTw.setText(transaction.getHash());
            binding.nonceTw.setText(String.format(Locale.getDefault(), "%d", transaction.getNonce()));
        }

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}