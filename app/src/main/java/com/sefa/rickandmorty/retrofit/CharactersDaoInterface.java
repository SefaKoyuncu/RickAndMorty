package com.sefa.rickandmorty.retrofit;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CharactersDaoInterface
{
    @GET("api/character")
    Call<AllCharacaters> allCharacaters();
}
