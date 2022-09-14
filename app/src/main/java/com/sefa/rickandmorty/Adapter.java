package com.sefa.rickandmorty;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.CardViewHolder> {

    private Context mContext;
    private List<Characters> charactersList;


    public Adapter(Context mContext, List<Characters> charactersList) {
        this.mContext = mContext;
        this.charactersList = charactersList;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_design_character, parent, false);

        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        Characters character = charactersList.get(position);

        holder.textViewCharacterName.setText(character.getName());
      //  Picasso.with(mContext).load(character.getImage()).into(holder.imageViewCharacter);

        Picasso.get().load(character.getImage()).into(holder.imageViewCharacter);


       /* holder.cLayout.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, CharacterDetailsActivity.class);
            intent.putExtra("Movie Id", character.getId());
            intent.putExtra("Character Name",character.getName());
            intent.putExtra("Character Status", character.getStatus());
            intent.putExtra("Character Location", character.getLocation());
            intent.putExtra("Character Image", character.getImage());
            mContext.startActivity(intent);
        });*/
    }

    @Override
    public int getItemCount() {
        return charactersList.size();
    }

    public class CardViewHolder extends RecyclerView.ViewHolder {

        public ConstraintLayout cLayout;
        public TextView textViewCharacterName;
        public ImageView imageViewCharacter;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            cLayout = itemView.findViewById(R.id.cLayout);
            imageViewCharacter = itemView.findViewById(R.id.imageViewCharacter);
            textViewCharacterName = itemView.findViewById(R.id.textViewCharacterName);

        }
    }

}