package com.app.comwallet.activities;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.app.comwallet.fragments.BottomSheetWalletFragment;
import com.app.comwallet.ComApp;
import com.app.comwallet.R;
import com.app.comwallet.adapters.TransactionsAdapter;
import com.app.comwallet.database.WalletDao;
import com.app.comwallet.database.WalletDatabase;
import com.app.comwallet.databinding.ActivityMainBinding;
import com.app.comwallet.fragments.ReceiveFragment;
import com.app.comwallet.fragments.SearchValidatorFragment;
import com.app.comwallet.fragments.SendFragment;
import com.app.comwallet.fragments.TransactionDetailsFragment;
import com.app.comwallet.fragments.UnstakeFragment;
import com.app.comwallet.icon.IconGenerator;
import com.app.comwallet.models.Transaction;
import com.app.comwallet.models.Wallet;

import com.app.comwallet.schnorrkel.utils.HexUtils;
import com.app.comwallet.utilities.AppUtils;
import com.app.comwallet.utilities.FragmentUtils;
import com.app.comwallet.utilities.WalletUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.emeraldpay.polkaj.ss58.SS58Type;
import io.emeraldpay.polkaj.types.Address;


public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    RequestQueue requestQueue;

    String TAG = MainActivity.class.getSimpleName();

    WalletDao walletDao;
    ExecutorService executor;
    Wallet wallet;

    Address address;

    TransactionsAdapter transactionsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        requestQueue = Volley.newRequestQueue(this);

        // Initialize Room database and executor service
        WalletDatabase walletDatabase = ComApp.database;
        executor = Executors.newSingleThreadExecutor();
        walletDao = walletDatabase.walletDao();

        getWalletData();

        binding.sendTw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentUtils.addFragmentWithAnimation(getSupportFragmentManager(), binding.mainLy.getId(), SendFragment.class, R.anim.slide_in, R.anim.slide_out, R.anim.slide_in, R.anim.slide_out);
            }
        });
        binding.receiveTw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putString("walletAddress", address.toString());
                FragmentUtils.addFragmentWithAnimation(getSupportFragmentManager(), binding.mainLy.getId(), ReceiveFragment.class, R.anim.slide_in, R.anim.slide_out, R.anim.slide_in, R.anim.slide_out, args);
            }
        });
        binding.stakeTw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentUtils.addFragmentWithAnimation(getSupportFragmentManager(), binding.mainLy.getId(), SearchValidatorFragment.class, R.anim.slide_in, R.anim.slide_out, R.anim.slide_in, R.anim.slide_out);
            }
        });
        binding.unStakeTw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentUtils.addFragmentWithAnimation(getSupportFragmentManager(), binding.mainLy.getId(), UnstakeFragment.class, R.anim.slide_in, R.anim.slide_out, R.anim.slide_in, R.anim.slide_out);
            }
        });
        binding.walletImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetWalletFragment bottomSheetWalletFragment = BottomSheetWalletFragment.newInstance(new BottomSheetWalletFragment.UpdateSelectedWallet() {
                    @Override
                    public void selectedPosition(int pos) {
                        WalletUtils.saveSelectedWalletId(getApplicationContext(), pos);
                        getWalletData();
                    }
                });

                bottomSheetWalletFragment.show(getSupportFragmentManager(), BottomSheetWalletFragment.class.getSimpleName());
            }
        });

    }

    private void startBalanceCardAnim() {
        binding.cardLy.setBackgroundResource(R.drawable.gradient_animation);
        AnimationDrawable animationDrawable = (AnimationDrawable) binding.cardLy.getBackground();
        animationDrawable.setEnterFadeDuration(750);
        animationDrawable.setExitFadeDuration(1500);
        animationDrawable.start();
    }

    private void getWalletData() {
        binding.walletActionsLy.setVisibility(View.GONE);
        binding.transactionsRv.setVisibility(View.GONE);
        binding.emptyTransactionsView.getRoot().setVisibility(View.GONE);
        binding.shimmerTransactionsLy.setVisibility(View.VISIBLE);
        binding.balanceShimmerLy.setVisibility(View.VISIBLE);
        binding.balanceTw.setVisibility(View.GONE);
        binding.stakedBalanceLy.setVisibility(View.GONE);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                wallet = walletDao.getSelectedWallet(WalletUtils.loadSelectedWalletId(getApplicationContext()));
                address = new Address(SS58Type.Network.SUBSTRATE, HexUtils.hexToBytes(wallet.getPublicKey()));
                //address = Address.from("5GZBhMZZRMWCiqgqdDGZCGo16Kg5aUQUcpuUGWwSgHn9HbRC"); // manipulate address temporary
                generateIcon();
                getWalletBalance();
                getMarketInfo();
            }
        });

    }

    private void generateIcon() {
        IconGenerator iconGenerator = new IconGenerator();
        Drawable iconDrawable = iconGenerator.getSvgImage(address.getPubkey(), 50, true, 0);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.walletImg.setImageDrawable(iconDrawable);
            }
        });
    }

    private void getWalletBalance() {
        String url = "https://api.comstats.org/balance/?wallet=" + address.toString();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    long balance = response.getLong("balance");
                    long staked = response.getLong("staked");

                    Log.e(TAG, "balance: " + balance);
                    Log.e(TAG, "staked: " + staked);

                    binding.balanceShimmerLy.setVisibility(View.GONE);
                    binding.balanceTw.setVisibility(View.VISIBLE);
                    binding.balanceTw.setText(AppUtils.formatAmount(balance));
                    binding.stakedBalanceLy.setVisibility(View.VISIBLE);
                    binding.stakedBalanceTw.setText(AppUtils.formatAmount(staked));
                    binding.walletActionsLy.setVisibility(View.VISIBLE);

                    startBalanceCardAnim();

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: " + error.getMessage());
            }
        });

        requestQueue.add(jsonObjectRequest);

    }

    private void getMarketInfo() {
        String url = "https://api.comswap.io/orders/public/marketinfo";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String volume1d = response.getString("volume1d");
                    String volume7d = response.getString("volume7d");
                    String price = response.getString("price");
                    String marketcap = response.getString("marketCap");

                    getWalletTransactions(price);

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: " + error.getMessage());
            }
        });

        requestQueue.add(jsonObjectRequest);

    }

    private void getWalletTransactions(String coin_price) {
        String url = "https://api.explorer.comwallet.io/transactions/wallet/" + address.toString(); //+ "?page=2&limit=10";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, response -> {
            try {
                JSONArray dataArray = response.getJSONArray("data");

                //empty data
                if (dataArray.length() == 0) {
                    binding.shimmerTransactionsLy.setVisibility(View.GONE);
                    binding.emptyTransactionsView.getRoot().setVisibility(View.VISIBLE);
                    binding.transactionsRv.setVisibility(View.GONE);
                    return;
                }

                ArrayList<Transaction> transactions = new ArrayList<>();

                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject transactionObj = dataArray.getJSONObject(i);

                    int id = transactionObj.getInt("id");
                    String timestamp = transactionObj.getString("timestamp");
                    String method = transactionObj.getString("method");
                    String sender = transactionObj.getString("sender");
                    String receiver = transactionObj.getString("receiver");
                    String amount = transactionObj.getString("amount");
                    int blockNumber = transactionObj.getInt("blockNumber");
                    boolean isSigned = transactionObj.getBoolean("isSigned");
                    String hash = transactionObj.getString("hash");
                    int nonce = transactionObj.getInt("nonce");
                    String fee = transactionObj.getString("fee");

                    Transaction transaction = new Transaction(id, timestamp, method, sender, receiver, amount, blockNumber, isSigned, hash, nonce, fee);
                    transactions.add(transaction);
                }

                binding.shimmerTransactionsLy.setVisibility(View.GONE);
                Log.e(TAG, "getWalletTransactions: done");
                transactionsAdapter = new TransactionsAdapter(this, transactions, coin_price, new TransactionsAdapter.OnItemClicked() {
                    @Override
                    public void onClick(Transaction transaction) {
                        Bundle args = new Bundle();
                        args.putParcelable("transaction", transaction);
                        FragmentUtils.addFragmentWithAnimation(getSupportFragmentManager(), binding.mainLy.getId(), TransactionDetailsFragment.class, R.anim.slide_in, R.anim.slide_out, R.anim.slide_in, R.anim.slide_out, args);
                    }
                });

                binding.transactionsRv.setAdapter(transactionsAdapter);
                binding.transactionsRv.setVisibility(View.VISIBLE);

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: " + error.getMessage());
            }
        });

        requestQueue.add(jsonObjectRequest);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Shutdown ExecutorService when the activity is destroyed
        if (executor != null) {
            executor.shutdown();
        }
    }
}