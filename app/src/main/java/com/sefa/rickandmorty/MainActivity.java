package com.sefa.rickandmorty;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.VideoView;

import com.sefa.rickandmorty.databinding.ActivityMainBinding;
import com.sefa.rickandmorty.retrofit.AllCharacters;
import com.sefa.rickandmorty.retrofit.ApiUtils;
import com.sefa.rickandmorty.retrofit.CharactersDaoInterface;
import com.sefa.rickandmorty.retrofit.Result;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private List<Result> allresultList = new ArrayList<>();
    private Adapter adapter;
    private CharactersDaoInterface charactersDaoInterface;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding= DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setMainActivityNesnesi(this);

        charactersDaoInterface= ApiUtils.getCharactersDaoInterface();

        dialog = new Dialog(this,android.R.style.Theme_Black_NoTitleBar);

        allCharacters();
        allresultList.clear();
        buildRecyclerView();
        startLoadingdialog();

        binding.editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                searchCharacters(editable.toString());
            }
        });
    }

    public void allCharacters(){
        charactersDaoInterface.allCharacaters().enqueue(new Callback<AllCharacters>() {
            @Override
            public void onResponse(Call<AllCharacters> call, retrofit2.Response<AllCharacters> response)
            {
                dismissdialog();

                List<Result> resultList=response.body().getResults();
                allresultList=resultList;
                adapter = new Adapter(MainActivity.this, resultList);
                binding.rv.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<AllCharacters> call, Throwable t) {
                dialog.dismiss();
            }
        });
    }

    public void searchCharacters(String text){
        if (binding.editTextSearch.getText().toString().equals("")) {
            if (!allresultList.isEmpty()) {
                adapter = new Adapter(MainActivity.this, allresultList);
                binding.rv.setAdapter(adapter);
            }
            else
            {
                allresultList.clear();
                allCharacters();
            }
        }
        else{
            charactersDaoInterface.searchCharacters("api/character/?name=" + text).enqueue(new Callback<AllCharacters>()
            {
                @Override
                public void onResponse(Call<AllCharacters> call, retrofit2.Response<AllCharacters> response) {

                    if ( response.isSuccessful())
                    {
                        binding.rv.setVisibility(View.VISIBLE);
                        binding.animationView.setVisibility(View.INVISIBLE);


                        List<Result> resultList = response.body().getResults();
                        adapter = new Adapter(MainActivity.this, resultList);
                        binding.rv.setAdapter(adapter);
                    }
                    else
                    {
                        binding.rv.setVisibility(View.INVISIBLE);
                        binding.animationView.setVisibility(View.VISIBLE);
                        hideKeyboard(MainActivity.this);
                    }
                }

                @Override
                public void onFailure(Call<AllCharacters> call, Throwable t) {

                }
            });
        }
    }

    private void buildRecyclerView(){
        binding.rv.setHasFixedSize(true);
        binding.rv.setLayoutManager(new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false));
        adapter = null;
    }

    void startLoadingdialog(){
        dialog.setContentView(R.layout.loading_video);
        VideoView videoView=dialog.findViewById(R.id.videoView);
        videoView.setVideoPath("android.resource://"+getPackageName()+"/"+R.raw.rickandmorty);
        videoView.start();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    void dismissdialog(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable()
        {
            public void run()
            {
                dialog.dismiss();
            }
        }, 2400);
    }

    public static void hideKeyboard(Activity activity){
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null)
        {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void cancelClick(){
        binding.editTextSearch.getText().clear();
        binding.rv.setVisibility(View.VISIBLE);
        binding.animationView.setVisibility(View.INVISIBLE);
        hideKeyboard(this);
    }

    public void clearClick(){
        binding.editTextSearch.getText().clear();
        binding.rv.setVisibility(View.VISIBLE);
        binding.animationView.setVisibility(View.INVISIBLE);
    }
}