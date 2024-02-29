package com.app.comwallet.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.comwallet.R;
import com.app.comwallet.databinding.RowWalletItemBinding;
import com.app.comwallet.icon.IconGenerator;
import com.app.comwallet.models.Wallet;
import com.app.comwallet.schnorrkel.utils.HexUtils;
import com.app.comwallet.utilities.WalletUtils;

import java.util.List;

import io.emeraldpay.polkaj.ss58.SS58Codec;

public class WalletAdapter extends RecyclerView.Adapter<WalletAdapter.ViewHolder> {

    List<Wallet> wallets;
    Context context;

    SS58Codec ss58Codec;
    IconGenerator iconGenerator;
    private final OnWalletClickListener onWalletClickListener;
    private final OnWalletOptionClicked onWalletOptionClicked;

    long selectedWalletId;


    public interface OnWalletOptionClicked {
        void copyAddress(String walletAddress);
        void revealKey(Wallet wallet);
        void rename(Wallet wallet);
        void delete(Wallet wallet);
    }

    public WalletAdapter(List<Wallet> wallets, Context context, long selectedWalletId, OnWalletClickListener onWalletClickListener, OnWalletOptionClicked onWalletOptionClicked) {
        this.wallets = wallets;
        this.context = context;
        this.ss58Codec = new SS58Codec();
        this.iconGenerator = new IconGenerator();
        this.onWalletClickListener = onWalletClickListener;
        this.selectedWalletId = selectedWalletId;
        this.onWalletOptionClicked = onWalletOptionClicked;
    }

    public interface OnWalletClickListener {
        void onClick(int position);
    }

    @NonNull
    @Override
    public WalletAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WalletAdapter.ViewHolder(RowWalletItemBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull WalletAdapter.ViewHolder holder, int position) {

        Wallet wallet = wallets.get(position);
        String walletAddress = WalletUtils.encodeAddress(ss58Codec, wallet.getPublicKey());

        if (selectedWalletId == position + 1) {
            holder.binding.getRoot().setBackgroundResource(R.drawable.bg_selected_wallet);
        } else {
            holder.binding.getRoot().setBackgroundResource(android.R.color.transparent);
        }

        holder.binding.walletNameTw.setText(wallet.getName());
        holder.binding.walletAddressTw.setText(WalletUtils.formatWalletAddress(walletAddress));

        Drawable iconDrawable = iconGenerator.getSvgImage(HexUtils.hexToBytes(wallet.getPublicKey()), 50, true, 0);
        holder.binding.walletImg.setImageDrawable(iconDrawable);
        holder.binding.walletSettingsTw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMenu(wallet, walletAddress, context, v, holder.getAdapterPosition());
            }
        });

        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onWalletClickListener != null) {
                    onWalletClickListener.onClick(holder.getAdapterPosition() + 1);
                }
            }
        });

    }

    private void openMenu(Wallet wallet, String walletAddress, Context context, View view, int pos) {
        // Create an instance of your menu
        PopupMenu popupMenu = new PopupMenu(context, view);
        // Inflate the menu resource
        popupMenu.getMenuInflater().inflate(R.menu.wallet_menu, popupMenu.getMenu());

        //If item is 0 disable and hide delete option
        if (pos == 0) {
            popupMenu.getMenu().findItem(R.id.wallet_menu_delete).setVisible(false);
            popupMenu.getMenu().findItem(R.id.wallet_menu_delete).setEnabled(false);
        }

        // Set an item click listener on the popup menu
        popupMenu.setOnMenuItemClickListener(item -> {
            // Handle menu item clicks here
            int id = item.getItemId();
            if (id == R.id.wallet_menu_copy) {
                onWalletOptionClicked.copyAddress(walletAddress);
                return true;
            } else if (id == R.id.wallet_menu_reveal_key) {
                onWalletOptionClicked.revealKey(wallet);
                return true;
            } else if (id == R.id.wallet_menu_rename) {
                onWalletOptionClicked.rename(wallet);
                return true;
            } else if (id == R.id.wallet_menu_delete) {
                onWalletOptionClicked.delete(wallet);
                return true;
            }
            return false;
        });

        // Show the popup menu
        popupMenu.show();
    }

    public void updateItem(int position) {
        notifyItemChanged(position);
    }

    public void removeItem(int position) {
        wallets.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return wallets.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final RowWalletItemBinding binding;

        public ViewHolder(@NonNull RowWalletItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
