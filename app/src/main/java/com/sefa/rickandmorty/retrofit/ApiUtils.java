package com.sefa.rickandmorty.retrofit;

public class ApiUtils
{
    public static final String baseUrl="https://rickandmortyapi.com/";

    public static CharactersDaoInterface getCharactersDaoInterface()
    {
        return RetrofitClient.getClient(baseUrl).create(CharactersDaoInterface.class);
    }
}
