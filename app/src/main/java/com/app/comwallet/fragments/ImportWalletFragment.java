package com.app.comwallet.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.app.comwallet.ComApp;
import com.app.comwallet.R;
import com.app.comwallet.models.Wallet;
import com.app.comwallet.database.WalletDao;
import com.app.comwallet.database.WalletDatabase;
import com.app.comwallet.activities.MainActivity;
import com.app.comwallet.databinding.FragmentImportWalletBinding;
import com.app.comwallet.schnorrkel.sign.ExpansionMode;
import com.app.comwallet.schnorrkel.sign.KeyPair;
import com.app.comwallet.schnorrkel.sign.PrivateKey;
import com.app.comwallet.schnorrkel.sign.PublicKey;
import com.app.comwallet.schnorrkel.utils.HexUtils;
import com.app.comwallet.utilities.EnglishWordListUtils;
import com.app.comwallet.utilities.SecretWordTokenizer;
import com.app.comwallet.utilities.WalletUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.github.novacrypto.bip39.MnemonicValidator;
import io.github.novacrypto.bip39.SeedCalculator;
import io.github.novacrypto.bip39.Validation.InvalidChecksumException;
import io.github.novacrypto.bip39.Validation.InvalidWordCountException;
import io.github.novacrypto.bip39.Validation.UnexpectedWhiteSpaceException;
import io.github.novacrypto.bip39.Validation.WordNotFoundException;
import io.github.novacrypto.bip39.wordlists.English;


public class ImportWalletFragment extends Fragment {


    FragmentImportWalletBinding binding;
    String TAG = ImportWalletFragment.class.getSimpleName();
    private ExecutorService executor;
    private WalletDao walletDao;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentImportWalletBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
    }

    private void initViews() {
        // Initialize Room database and executor service
        WalletDatabase walletDatabase = ComApp.database;
        walletDao = walletDatabase.walletDao();
        executor = Executors.newSingleThreadExecutor();

        //Set auto complete textviews bip39 word list
        SecretWordTokenizer secretWordTokenizer = new SecretWordTokenizer(' ');
        ArrayAdapter<String> wordsAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_recovery_phrase_selection, getWordList());

        binding.secretWordTw.setAdapter(wordsAdapter);
        binding.secretWordTw2.setAdapter(wordsAdapter);
        binding.secretWordTw3.setAdapter(wordsAdapter);
        binding.secretWordTw4.setAdapter(wordsAdapter);
        binding.secretWordTw5.setAdapter(wordsAdapter);
        binding.secretWordTw6.setAdapter(wordsAdapter);
        binding.secretWordTw7.setAdapter(wordsAdapter);
        binding.secretWordTw8.setAdapter(wordsAdapter);
        binding.secretWordTw9.setAdapter(wordsAdapter);
        binding.secretWordTw10.setAdapter(wordsAdapter);
        binding.secretWordTw11.setAdapter(wordsAdapter);
        binding.secretWordTw12.setAdapter(wordsAdapter);

        binding.secretWordTw.setTokenizer(secretWordTokenizer);
        binding.secretWordTw2.setTokenizer(secretWordTokenizer);
        binding.secretWordTw3.setTokenizer(secretWordTokenizer);
        binding.secretWordTw4.setTokenizer(secretWordTokenizer);
        binding.secretWordTw5.setTokenizer(secretWordTokenizer);
        binding.secretWordTw6.setTokenizer(secretWordTokenizer);
        binding.secretWordTw7.setTokenizer(secretWordTokenizer);
        binding.secretWordTw8.setTokenizer(secretWordTokenizer);
        binding.secretWordTw9.setTokenizer(secretWordTokenizer);
        binding.secretWordTw10.setTokenizer(secretWordTokenizer);
        binding.secretWordTw11.setTokenizer(secretWordTokenizer);
        binding.secretWordTw12.setTokenizer(secretWordTokenizer);

        binding.secretWordTw.addTextChangedListener(secretWordWatcher);
        binding.secretWordTw2.addTextChangedListener(secretWordWatcher);
        binding.secretWordTw3.addTextChangedListener(secretWordWatcher);
        binding.secretWordTw4.addTextChangedListener(secretWordWatcher);
        binding.secretWordTw5.addTextChangedListener(secretWordWatcher);
        binding.secretWordTw6.addTextChangedListener(secretWordWatcher);
        binding.secretWordTw7.addTextChangedListener(secretWordWatcher);
        binding.secretWordTw8.addTextChangedListener(secretWordWatcher);
        binding.secretWordTw9.addTextChangedListener(secretWordWatcher);
        binding.secretWordTw10.addTextChangedListener(secretWordWatcher);
        binding.secretWordTw11.addTextChangedListener(secretWordWatcher);
        binding.secretWordTw12.addTextChangedListener(secretWordWatcher);

        setBackspaceListener(binding.secretWordTw, binding.secretWordTw2);
        setBackspaceListener(binding.secretWordTw2, binding.secretWordTw3);
        setBackspaceListener(binding.secretWordTw3, binding.secretWordTw4);
        setBackspaceListener(binding.secretWordTw4, binding.secretWordTw5);
        setBackspaceListener(binding.secretWordTw5, binding.secretWordTw6);
        setBackspaceListener(binding.secretWordTw6, binding.secretWordTw7);
        setBackspaceListener(binding.secretWordTw7, binding.secretWordTw8);
        setBackspaceListener(binding.secretWordTw8, binding.secretWordTw9);
        setBackspaceListener(binding.secretWordTw9, binding.secretWordTw10);
        setBackspaceListener(binding.secretWordTw10, binding.secretWordTw11);
        setBackspaceListener(binding.secretWordTw11, binding.secretWordTw12);

        binding.importBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mnemonic_phrase = getSecretWordsText();
                addWallet(mnemonic_phrase);
            }
        });

        binding.backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });
    }

    private String[] getWordList() {
        return EnglishWordListUtils.words;
    }

    private String getSecretWordsText() {
        // Get text from your EditText fields and concatenate it into a single string
        String secretWords = binding.secretWordTw.getText().toString().trim() + " " +
                binding.secretWordTw2.getText().toString().trim() + " " +
                binding.secretWordTw3.getText().toString().trim() + " " +
                binding.secretWordTw4.getText().toString().trim() + " " +
                binding.secretWordTw5.getText().toString().trim() + " " +
                binding.secretWordTw6.getText().toString().trim() + " " +
                binding.secretWordTw7.getText().toString().trim() + " " +
                binding.secretWordTw8.getText().toString().trim() + " " +
                binding.secretWordTw9.getText().toString().trim() + " " +
                binding.secretWordTw10.getText().toString().trim() + " " +
                binding.secretWordTw11.getText().toString().trim() + " " +
                binding.secretWordTw12.getText().toString().trim() + " ";
        return secretWords.trim();
    }


    public void setBackspaceListener(final EditText previousEditText, final EditText currentEditText) {
        currentEditText.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                // Check if the current EditText is empty
                if (currentEditText.getText().toString().isEmpty()) {
                    // Move focus to the previous EditText
                    previousEditText.requestFocus();
                    return true; // Consume the event
                }
            }
            return false; // Allow default backspace behavior
        });
    }


    TextWatcher secretWordWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            enableButtonIfValidWords();
        }
    };

    private void enableButtonIfValidWords() {
        boolean allFieldsFilled = checkAllFieldsFilled();
        // Enable the button if all fields are filled and words are valid
        binding.importBtn.setEnabled(allFieldsFilled && isValidWords(getAllEditTextValues()));
        if (allFieldsFilled) {
            binding.errorTw.setVisibility(isValidWords(getAllEditTextValues()) ? View.GONE : View.VISIBLE);
        }

    }

    private boolean checkAllFieldsFilled() {
        // Check if all secret word EditText fields are not empty
        return !TextUtils.isEmpty(binding.secretWordTw2.getText()) &&
                !TextUtils.isEmpty(binding.secretWordTw3.getText()) &&
                !TextUtils.isEmpty(binding.secretWordTw4.getText()) &&
                !TextUtils.isEmpty(binding.secretWordTw5.getText()) &&
                !TextUtils.isEmpty(binding.secretWordTw6.getText()) &&
                !TextUtils.isEmpty(binding.secretWordTw7.getText()) &&
                !TextUtils.isEmpty(binding.secretWordTw8.getText()) &&
                !TextUtils.isEmpty(binding.secretWordTw9.getText()) &&
                !TextUtils.isEmpty(binding.secretWordTw10.getText()) &&
                !TextUtils.isEmpty(binding.secretWordTw11.getText()) &&
                !TextUtils.isEmpty(binding.secretWordTw12.getText());
    }

    private List<String> getAllEditTextValues() {
        // Get all the secret word EditText values and store them in a list
        List<String> editTextValues = new ArrayList<>();
        editTextValues.add(binding.secretWordTw.getText().toString());
        editTextValues.add(binding.secretWordTw2.getText().toString());
        editTextValues.add(binding.secretWordTw3.getText().toString());
        editTextValues.add(binding.secretWordTw4.getText().toString());
        editTextValues.add(binding.secretWordTw5.getText().toString());
        editTextValues.add(binding.secretWordTw6.getText().toString());
        editTextValues.add(binding.secretWordTw7.getText().toString());
        editTextValues.add(binding.secretWordTw8.getText().toString());
        editTextValues.add(binding.secretWordTw9.getText().toString());
        editTextValues.add(binding.secretWordTw10.getText().toString());
        editTextValues.add(binding.secretWordTw11.getText().toString());
        editTextValues.add(binding.secretWordTw12.getText().toString());

        return editTextValues;
    }

    private boolean isValidWords(List<String> words) {
        try {
            MnemonicValidator
                    .ofWordList(English.INSTANCE)
                    .validate(words);
            return true;
        } catch (InvalidChecksumException e) {
            Log.e(TAG, "InvalidChecksumException: " + e.getMessage());
            binding.errorTw.setText(e.getMessage());
        } catch (InvalidWordCountException e) {
            Log.e(TAG, "InvalidWordCountException: " + e.getMessage());
            binding.errorTw.setText(e.getMessage());
        } catch (WordNotFoundException e) {
            Log.e(TAG, "WordNotFoundException: " + e.getMessage());
            binding.errorTw.setText(e.getMessage());
        } catch (UnexpectedWhiteSpaceException e) {
            Log.e(TAG, "UnexpectedWhiteSpaceException: " + e.getMessage());
            binding.errorTw.setText(e.getMessage());
        }

        return false;
    }

    private void addWallet(String mnemonic_phrase) {


        byte[] seed = new SeedCalculator().calculateSeed(mnemonic_phrase, "");
        KeyPair keyPair = KeyPair.fromSecretSeed(seed, ExpansionMode.Ed25519);
        PrivateKey privateKey = keyPair.getPrivateKey();
        PublicKey publicKey = keyPair.getPublicKey();
        // Insert the wallet into the database
        executor.execute(() -> {
            Wallet wallet = new Wallet(Wallet.generateWalletName(walletDao), mnemonic_phrase, HexUtils.bytesToHex(publicKey.toPublicKey()), HexUtils.bytesToHex(privateKey.getKey()));
            walletDao.insertWallet(wallet);
            Wallet lastInsertedWallet = walletDao.getLastInsertedWallet();

            long lastInsertedId = lastInsertedWallet.getId();
            WalletUtils.saveSelectedWalletId(getContext(),lastInsertedId);

            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            getActivity().finish();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}