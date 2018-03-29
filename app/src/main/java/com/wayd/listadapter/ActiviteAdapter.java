package com.wayd.listadapter;

import java.util.ArrayList;
import java.util.List;

import com.application.wayd.R;
import com.wayd.bean.Activite;
import com.wayd.bean.Outils;
import com.wayd.bean.Profil;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ActiviteAdapter extends BaseAdapter {

    // Une liste de personnes
    private final List<Activite> listActivite;

    // Le contexte dans lequel est présent notre adapter
    private final Context mContext;

    // Un mécanisme pour gérer l'affichage graphique depuis un layout XML
    private final LayoutInflater mInflater;

    private final ArrayList<ActiviteAdapterListener> mListListener = new ArrayList<>();

    public ActiviteAdapter(Context context, List<Activite> aListP) {
        mContext = context;
        listActivite = aListP;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return listActivite.size(); // DOCUMENTEZ_MOI Raccord de méthode auto-généré
    }

    @Override
    public Object getItem(int position) {
        return listActivite.get(position); // DOCUMENTEZ_MOI Raccord de méthode auto-généré
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
            layoutItem = (RelativeLayout) mInflater.inflate(R.layout.item_activitenew, parent, false);
        } else {
            layoutItem = (RelativeLayout) convertView;
        }

        // (2) : Récupération des TextView de notre layout
        //  RatingBar tv_note = (RatingBar) layoutItem.findViewById(R.id.RT_note);

        TextView nomorganisteur = (TextView) layoutItem.findViewById(R.id.pseudo);
        TextView adresse = (TextView) layoutItem.findViewById(R.id.adresse);
        TextView date = (TextView) layoutItem.findViewById(R.id.dateactivite);
        ImageView photo = (ImageView) layoutItem.findViewById(R.id.photoactivite);
        TextView TV_tempsrestant = (TextView) layoutItem.findViewById(R.id.tempsrestant);
       // RatingBar ratingBar = (RatingBar) layoutItem.findViewById(R.id.note);
       // TextView TV_nbrwaydeur = (TextView) layoutItem.findViewById(R.id.amidepuis);
        TextView TV_Distance = (TextView) layoutItem.findViewById(R.id.distance);
        ImageView iconActivite=(ImageView) layoutItem.findViewById(R.id.iconactivite);
        Activite activite = listActivite.get(position);
        layoutItem.setTag(activite);
        nomorganisteur.setText(activite.getPseudoOrganisateur());

        adresse.setText(activite.getAdresse());

        //ratingBar.setMax(5);
       // ratingBar.setStepSize(0.5f);
       // ratingBar.setRating((float) activite.getNote());
      //  ratingBar.setIsIndicator(true);
        TV_tempsrestant.setText(activite.getTpsrestant());
        TV_Distance.setText(Outils.personneConnectee.getDistanceActiviteStr(activite));
      //  photo.setImageDrawable(Outils.getAvatarDrawable(mContext, activite.getPhoto()));
        photo.setBackground(new BitmapDrawable(mContext.getResources(), activite.getPhoto()));
        iconActivite.setImageResource(Outils.getActiviteMipMap(activite.getIdTypeActite(),activite.getTypeUser()));
        date.setText(activite.getDatedebutStr());
        if (activite.getTypeUser()== Profil.CARPEDIEM) {
          //  ratingBar.setVisibility(View.INVISIBLE);
          //  adresse.setText(activite.getLibelle().replace("&#039;","'"));
            nomorganisteur.setText(activite.getPseudoOrganisateur().replace("&#039;","'"));

        }
        if (activite.isFromWaydeur()){

        if (activite.iscomplete()) {
            layoutItem.setBackgroundColor(Color.parseColor("#FFFFFF"));
        //    TV_nbrwaydeur.setTextColor(Color.parseColor("#FFAB00"));
          //  TV_nbrwaydeur.setText(R.string.ActiviteAdapter_complet);
        } else {
            layoutItem.setBackgroundColor(Color.parseColor("#FFFFFF"));
         //   TV_nbrwaydeur.setText(activite.getNbrparticipantStr());
         //   TV_nbrwaydeur.setTextColor(Color.parseColor("#000000"));
        }

        }


        return layoutItem;

    }

    public interface ActiviteAdapterListener {

    }



    public void addListener(ActiviteAdapterListener aListener) {
        mListListener.add(aListener);
    }


}
