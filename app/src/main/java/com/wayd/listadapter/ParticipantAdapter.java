package com.wayd.listadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.application.wayd.R;
import com.wayd.bean.Outils;
import com.wayd.bean.Participant;

import java.util.ArrayList;
import java.util.List;

public class ParticipantAdapter extends BaseAdapter {

    // Une liste de personnes
    private final List<Participant> listparticipants;
    private final Context mContext;

    // Un mécanisme pour gérer l'affichage graphique depuis un layout XML
    private final LayoutInflater mInflater;

    private final ArrayList<ParticipantAdapterListener> mListListener = new ArrayList<>();

    public ParticipantAdapter(Context context, List<Participant> aListP) {
        mContext = context;
        listparticipants = aListP;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return listparticipants.size(); // DOCUMENTEZ_MOI Raccord de méthode auto-généré
    }

    @Override
    public Object getItem(int position) {
        return listparticipants.get(position); // DOCUMENTEZ_MOI Raccord de méthode auto-généré
    }

    @Override
    public long getItemId(int position) {
        return position;

    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RelativeLayout layoutItem;
        // (1) : Réutilisation des layouts

        if (convertView == null) {
            // Initialisation de notre item à partir du layout XML "personne_layout.xml"
            layoutItem = (RelativeLayout) mInflater.inflate(R.layout.item_participant, parent, false);
        } else {
            layoutItem = (RelativeLayout) convertView;
        }


        // (2) : Récupération des TextView de notre layout
        TextView nom = (TextView) layoutItem.findViewById(R.id.pseudo);
        ImageView photo = (ImageView) layoutItem.findViewById(R.id.iconactivite);
        RatingBar ratingBar = (RatingBar) layoutItem.findViewById(R.id.note);
        Participant participant = listparticipants.get(position);
        ratingBar.setMax(5);
        ratingBar.setStepSize(0.5f);
        ratingBar.setRating((float) participant.getNote());
       // photo.setImageBitmap(Outils.getPhotoPersonne(mContext, participant.getPhoto()));
        photo.setImageDrawable(Outils.getAvatarDrawable(mContext, participant.getPhoto()));
        nom.setText(participant.getPseudo());

        layoutItem.setTag(position);
        layoutItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Integer position = (Integer) v.getTag();
                sendParticipantLongClickListener(listparticipants.get(position), position);
                return true;
            }
        });

        layoutItem.setTag(position);
        layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer position = (Integer) v.getTag();
                sendParticipantClickListener(listparticipants.get(position), position);

            }
        });


        return layoutItem;
    }

    public interface ParticipantAdapterListener {
         void onLongClickPartiticipant(Participant participant ,int position);
         void onClickParticipant(Participant participant ,int position);
    }

    public void addListener(ParticipantAdapterListener aListener) {
       if (!mListListener.contains(aListener))
        mListListener.add(aListener);
    }

    private void sendParticipantClickListener(Participant participant, int position) {
        for (int i = mListListener.size() - 1; i >= 0; i--) {
            mListListener.get(i).onClickParticipant(participant, position);
        }

    }

    private void sendParticipantLongClickListener(Participant participant, int position) {
        for (int i = mListListener.size() - 1; i >= 0; i--) {
            mListListener.get(i).onLongClickPartiticipant(participant, position);
        }

    }

}
