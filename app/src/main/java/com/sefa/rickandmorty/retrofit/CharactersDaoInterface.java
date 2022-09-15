package com.sefa.rickandmorty.retrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface CharactersDaoInterface
{
    String url = null;

    @GET("api/character")
    Call<AllCharacters> allCharacaters();

    @GET
    Call<AllCharacters> searchCharacters(@Url String url);
}
