package com.app.comwallet.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.text.HtmlCompat;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import com.app.comwallet.ComApp;
import com.app.comwallet.R;
import com.app.comwallet.adapters.WalletAdapter;
import com.app.comwallet.database.WalletDao;
import com.app.comwallet.database.WalletDatabase;
import com.app.comwallet.databinding.DialogTextInputLayoutBinding;
import com.app.comwallet.databinding.FragmentBottomSheetWalletBinding;
import com.app.comwallet.models.Wallet;
import com.app.comwallet.utilities.AppUtils;
import com.app.comwallet.utilities.FragmentUtils;
import com.app.comwallet.utilities.WalletUtils;
import com.app.comwallet.views.CenteredToast;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class BottomSheetWalletFragment extends BottomSheetDialogFragment {


    FragmentBottomSheetWalletBinding binding;
    ExecutorService executor;
    WalletDao walletDao;
    WalletAdapter walletAdapter;

    UpdateSelectedWallet updateSelectedWallet;

    List<Wallet> wallets;

    public interface UpdateSelectedWallet {
        void selectedPosition(int pos);
    }

    public static BottomSheetWalletFragment newInstance(UpdateSelectedWallet updateSelectedWallet) {
        Bundle args = new Bundle();
        BottomSheetWalletFragment fragment = new BottomSheetWalletFragment();
        fragment.setArguments(args);
        fragment.updateSelectedWallet = updateSelectedWallet;
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentBottomSheetWalletBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext(), R.style.CustomBottomSheetDialog);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        dialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
        return dialog;
    }


    private void initViews() {
        // Initialize Room database and executor service
        WalletDatabase walletDatabase = ComApp.database;
        executor = Executors.newSingleThreadExecutor();
        walletDao = walletDatabase.walletDao();

        executor.execute(() -> {
            wallets = walletDao.getAllWallets();
            walletAdapter = new WalletAdapter(wallets, getContext(), WalletUtils.loadSelectedWalletId(getContext()), new WalletAdapter.OnWalletClickListener() {
                @Override
                public void onClick(int position) {
                    updateSelectedWallet.selectedPosition(position);
                    dismiss();
                }
            }, new WalletAdapter.OnWalletOptionClicked() {
                @Override
                public void copyAddress(String walletAddress) {
                    AppUtils.copyToClipboard(getContext(), walletAddress);
                    CenteredToast.show(getContext(), "Wallet Address Copied");
                }

                @Override
                public void revealKey(Wallet wallet) {
                    Bundle args = new Bundle();
                    args.putString("walletName",wallet.getName());
                    args.putString("walletPk",wallet.getPrivateKey());
                    FragmentUtils.addFragmentWithAnimation(getParentFragmentManager(), R.id.mainLy, DisclaimerRevealPrivateKeyFragment.class, R.anim.slide_in, R.anim.slide_out, R.anim.slide_in, R.anim.slide_out, args);
                    dismiss();
                }

                @Override
                public void rename(Wallet wallet) {
                    showRenameWalletDialog(wallet);
                }

                @Override
                public void delete(Wallet wallet) {
                    showDeleteWalletDialog(wallet);
                }
            });

            binding.walletsRv.setAdapter(walletAdapter);
        });

        binding.createWalletBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                FragmentUtils.addFragmentWithAnimation(getParentFragmentManager(), R.id.mainLy, CreateWalletFragment.class, R.anim.slide_in, R.anim.slide_out, R.anim.slide_in, R.anim.slide_out);
            }
        });

        binding.importWalletBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                FragmentUtils.addFragmentWithAnimation(getParentFragmentManager(), R.id.mainLy, ImportWalletFragment.class, R.anim.slide_in, R.anim.slide_out, R.anim.slide_in, R.anim.slide_out);
            }
        });
    }

    private void showDeleteWalletDialog(Wallet wallet) {
        new MaterialAlertDialogBuilder(getContext())
                .setTitle("Remove " + wallet.getName())
                .setMessage(Html.fromHtml("Are you sure you want to remove <b>" + wallet.getName().trim() + "</b> from your wallet? (Any funds associated with this wallet will not be removed)<br><br>MAKE SURE YOU HAVE A BACKUP OF YOUR PRIVATE SEED BEFORE REMOVING THIS WALLET!", HtmlCompat.FROM_HTML_MODE_LEGACY)).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        walletDao.deleteWallet(wallet);
                        // Fetch the updated list of wallets from the database
                        List<Wallet> updatedWallets = walletDao.getAllWallets(); // Or however you fetch wallets from Room
                        if (updatedWallets.isEmpty()) {

                        }

                        // Update the data source of the adapter
                        wallets.clear();
                        wallets.addAll(updatedWallets);

                        binding.walletsRv.post(new Runnable() {
                            @Override
                            public void run() {
                                walletAdapter.removeItem((int) wallet.getId() - 1);
                                CenteredToast.show(getContext(), "Wallet Deleted");
                            }
                        });
                    }
                });
            }
        }).show();
    }

    private void showRenameWalletDialog(Wallet wallet) {
        // Create and show the MaterialAlertDialog
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext()).setTitle("Rename Wallet").setNegativeButton("CANCEL", null).setPositiveButton("SAVE", null);

        // Inflate the layout for the dialog using view binding
        DialogTextInputLayoutBinding dialogBinding = DialogTextInputLayoutBinding.inflate(LayoutInflater.from(getContext()));
        dialogBinding.textInputLy.setHint("Name");
        dialogBinding.textInputLy.getEditText().setText(wallet.getName());
        builder.setView(dialogBinding.getRoot()); // Set custom view with EditText

        AlertDialog renameDialog = builder.create();
        renameDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                // Access dialog's views here
                Button positiveButton = renameDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button negativeButton = renameDialog.getButton(AlertDialog.BUTTON_NEGATIVE);

                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Do something with the input when positive button is clicked
                        String newWalletName = dialogBinding.textInputLy.getEditText().getText().toString().trim();
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                walletDao.renameWallet(wallet.getId(), newWalletName);
                                // Fetch the updated list of wallets from the database
                                List<Wallet> updatedWallets = walletDao.getAllWallets(); // Or however you fetch wallets from Room
                                // Update the data source of the adapter
                                wallets.clear();
                                wallets.addAll(updatedWallets);

                                binding.walletsRv.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        renameDialog.dismiss();
                                        walletAdapter.updateItem((int) wallet.getId() - 1);
                                        CenteredToast.show(getContext(), "Wallet Name Updated");
                                    }
                                });

                            }
                        });
                    }
                });

                negativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        renameDialog.dismiss(); // Dismiss the dialog
                    }
                });
            }
        });

        renameDialog.show();

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}