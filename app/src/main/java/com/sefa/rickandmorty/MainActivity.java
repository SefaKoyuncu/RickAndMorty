package com.sefa.rickandmorty;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sefa.rickandmorty.databinding.ActivityMainBinding;
import com.sefa.rickandmorty.retrofit.AllCharacaters;
import com.sefa.rickandmorty.retrofit.ApiUtils;
import com.sefa.rickandmorty.retrofit.CharactersDaoInterface;
import com.sefa.rickandmorty.retrofit.Result;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private List<Characters> charactersList = new ArrayList<>();
    private List<Characters> charactersListForSearch = new ArrayList<>();
    private Adapter adapter;
    private final String api = "https://rickandmortyapi.com/api/character";
    private final String searchCharacterApi = "https://rickandmortyapi.com/api/character/?name=";

    private CharactersDaoInterface charactersDaoInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this, R.layout.activity_main);

        charactersDaoInterface= ApiUtils.getCharactersDaoInterface();

        allCharacters();

        charactersList.clear();
        buildRecyclerView();

        getAllCharacters(api);

        binding.textViewCancel.setOnClickListener(view ->
        {
            binding.editTextSearch.getText().clear();
            //binding.imageViewNotFound.setVisibility(View.INVISIBLE);
            hideKeyboard(this);
        });
        binding.imageViewClear.setOnClickListener(view -> {
            binding.editTextSearch.getText().clear();
            //binding.imageViewNotFound.setVisibility(View.INVISIBLE);
        });
        binding.editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                charactersListForSearch.clear();
                searchCharacter(editable.toString());
            }
        });

    }

    public void allCharacters()
    {
        charactersDaoInterface.allCharacaters().enqueue(new Callback<AllCharacaters>() {
            @Override
            public void onResponse(Call<AllCharacaters> call, retrofit2.Response<AllCharacaters> response) {

                List<Result> resultList=response.body().getResults();

                for (Result r:resultList)
                {
                    Log.e("id",String.valueOf(r.getId()));
                    Log.e("name",r.getName());
                }


            }


            @Override
            public void onFailure(Call<AllCharacaters> call, Throwable t) {

            }
        });
    }

    private void buildRecyclerView(){
        binding.rv.setHasFixedSize(true);
        binding.rv.setLayoutManager(new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false));
        adapter = null;
    }

    public void getAllCharacters(String apiURL){

        //binding.imageViewNotFound.setVisibility(View.INVISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, apiURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    Log.e("response",response);

                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject info = jsonObject.getJSONObject("info");
                    String next = info.getString("next");
                    int pages = info.getInt("pages");

                    JSONArray jsonArrayResults = jsonObject.getJSONArray("results");

                    for (int i = 0; i <jsonArrayResults.length(); i++){

                        JSONObject jsonObjectCharacter = jsonArrayResults.getJSONObject(i);
                        int id = jsonObjectCharacter.getInt("id");
                        String name = jsonObjectCharacter.getString("name");
                        String status = jsonObjectCharacter.getString("status");
                        String image = jsonObjectCharacter.getString("image");
                        JSONObject location = jsonObjectCharacter.getJSONObject("location");
                        String locationName = location.getString("name");

                        Characters c = new Characters(id,name,status,locationName,image);

                        charactersList.add(c);
                        adapter = new Adapter(MainActivity.this, charactersList);
                        binding.rv.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                    }

                    if (!next.equals("null")){
                        getAllCharacters(info.getString("next"));
                    }


                } catch (JSONException e ) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(),"Please Check Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });


        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }

    public void searchCharacter(String text){

        String searchCharacterURL = searchCharacterApi + text;

        if (binding.editTextSearch.getText().toString().equals("")){
            if (!charactersList.isEmpty()){
                adapter = new Adapter(MainActivity.this, charactersList);
                binding.rv.setAdapter(adapter);
            }
            else {
                charactersList.clear();
                getAllCharacters(api);
            }
        }
        else {
            charactersListForSearch.clear();

            StringRequest stringRequest = new StringRequest(Request.Method.GET, searchCharacterURL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                      //  binding.imageViewNotFound.setVisibility(View.INVISIBLE);
                        JSONObject jsonObject = new JSONObject(response);
                        JSONObject info = jsonObject.getJSONObject("info");
                        String next = info.getString("next");
                        int pages = info.getInt("pages");

                        JSONArray jsonArrayResults = jsonObject.getJSONArray("results");

                        for (int i = 0; i <jsonArrayResults.length(); i++){

                            JSONObject jsonObjectCharacter = jsonArrayResults.getJSONObject(i);
                            int id = jsonObjectCharacter.getInt("id");
                            String name = jsonObjectCharacter.getString("name");
                            String status = jsonObjectCharacter.getString("status");
                            String image = jsonObjectCharacter.getString("image");
                            JSONObject location = jsonObjectCharacter.getJSONObject("location");
                            String locationName = location.getString("name");

                            Characters c = new Characters(id,name,status,locationName,image);

                            charactersListForSearch.add(c);
                            adapter = new Adapter(MainActivity.this, charactersListForSearch);
                            binding.rv.setAdapter(adapter);

                        }

                    } catch (JSONException e ) {

                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    adapter.notifyDataSetChanged();
                    //binding.imageViewNotFound.setVisibility(View.VISIBLE);
                }
            });

            Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
        }
    }

    void startLoadingdialog() {

       /* // adding ALERT Dialog builder object and passing activity as parameter
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // layoutinflater object and use activity to get layout inflater
        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.loading, null));
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.show();*/
    }

    // dismiss method
    void dismissdialog() {
       // dialog.dismiss();
    }


    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}