package com.wayd.listadapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.application.wayd.R;
import com.wayd.bean.Activite;
import com.wayd.bean.Outils;
import com.wayd.bean.Profil;

import java.util.ArrayList;
import java.util.List;

public class MesActiviteAdapter extends BaseAdapter
{

    // Une liste de personnes
    private final List<Activite> listActivites;

    // Le contexte dans lequel est présent notre adapter
    private final Context mContext;

    // Un mécanisme pour gérer l'affichage graphique depuis un layout XML
    private final LayoutInflater mInflater;

    private final ArrayList<ActiviteAdapterListener> mListListener = new ArrayList<>();

    public MesActiviteAdapter(Context context, List<Activite> aListP)
    {
        mContext = context;
        listActivites = aListP;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount()
    {
        return listActivites.size(); // DOCUMENTEZ_MOI Raccord de méthode auto-généré
    }

    @Override
    public Object getItem(int position)
    {
        return listActivites.get(position); // DOCUMENTEZ_MOI Raccord de méthode auto-généré
    }

    @Override
    public long getItemId(int position)
    {
        return position;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        RelativeLayout layoutItem;
        // (1) : Réutilisation des layouts
        if (convertView == null)
        {
            // Initialisation de notre item à partir du layout XML "personne_layout.xml"
            layoutItem = (RelativeLayout) mInflater.inflate(R.layout.item_mesactivite, parent, false);
        }
        else
        {
            layoutItem = (RelativeLayout) convertView;
        }

        // (2) : Récupération des TextView de notre layout
     //  RatingBar tv_note = (RatingBar) layoutItem.findViewById(R.id.RT_note);

        TextView nomorganisteur = (TextView) layoutItem.findViewById(R.id.pseudo);
        TextView titre = (TextView) layoutItem.findViewById(R.id.dateactivite);
        ImageView photo = (ImageView) layoutItem.findViewById(R.id.photo);
        TextView TV_tempsrestant= (TextView) layoutItem.findViewById(R.id.tempsrestant);
        RatingBar ratingBar = (RatingBar) layoutItem.findViewById(R.id.note);
      //  TextView TV_nbravis=(TextView) layoutItem.findViewById(R.id.id_nbravis);
        TextView TV_nbrwaydeur=(TextView) layoutItem.findViewById(R.id.nbrwaydeur);
        TextView TV_Distance=(TextView) layoutItem.findViewById(R.id.distance);
       // ImageView Img_information = (ImageView) layoutItem.findViewById(R.id.informationami);
        ImageView Img_message = (ImageView) layoutItem.findViewById(R.id.message);
        ImageView Img_Information = (ImageView) layoutItem.findViewById(R.id.information);
        ImageView iconActivite=(ImageView) layoutItem.findViewById(R.id.iconcActivite);
        Activite activite= listActivites.get(position);
        layoutItem.setTag(activite);
        nomorganisteur.setText(activite.getPseudoOrganisateur() );
        titre.setText(activite.getTitreUnicode());
        ratingBar.setMax(5);
        ratingBar.setStepSize(0.5f);
        ratingBar.setRating((float) activite.getNote());
        ratingBar.setIsIndicator(true);
        TV_tempsrestant.setText(activite.getTpsrestant());
        TV_Distance.setText(Outils.personneConnectee.getDistanceActiviteStr(activite));
        photo.setImageDrawable(Outils.getAvatarDrawable(mContext,activite.getPhoto()));
        iconActivite.setImageResource(Outils.getActiviteMipMap(activite.getIdTypeActite(),activite.getTypeUser()));

        Img_message.setTag(position);
        Img_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer position = (Integer) v.getTag();
                // On prÃ©vient les listeners qu'il y a eu un clic sur le TextView "TV_Nom".
                sendMessageListener(listActivites.get(position), position);

            }
        });


        Img_Information.setTag(position);
        Img_Information.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer position = (Integer) v.getTag();
                // On prÃ©vient les listeners qu'il y a eu un clic sur le TextView "TV_Nom".
                sendInformationListener(listActivites.get(position), position);

            }
        });

        if (activite.iscomplete()) {

            layoutItem.setBackgroundColor(Color.parseColor("#FFFFFF"));
            TV_nbrwaydeur.setTextColor(Color.parseColor("#FFAB00"));
            TV_nbrwaydeur.setText(R.string.MesActiviteAdapter_complet);

        } else {

            layoutItem.setBackgroundColor(Color.parseColor("#FFFFFF"));
            TV_nbrwaydeur.setText(activite.getNbrparticipantStr());
            TV_nbrwaydeur.setTextColor(Color.parseColor("#000000"));
        }

        if (activite.getTypeUser()== Profil.PRO)
            Img_message.setVisibility(View.INVISIBLE);
        else
            Img_message.setVisibility(View.VISIBLE);

        layoutItem.setTag(position);
        layoutItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Integer position = (Integer) v.getTag();
                // On prÃ©vient les listeners qu'il y a eu un clic sur le TextView "TV_Nom".
                sendActiviteLongListener(listActivites.get(position), position);
                return true;
            }
        });

        layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer position = (Integer) v.getTag();
                // On prÃ©vient les listeners qu'il y a eu un clic sur le TextView "TV_Nom".
                sendActiviteClickListener(listActivites.get(position), position);
            }
        });


        return layoutItem;

    }

    public interface ActiviteAdapterListener
    {
        void onLongClickActivite(Activite item, int position);
        void onClickMessage(Activite activite, int position);
        void onClickInformation(Activite activite, int position);
        void onClickAtivite(Activite item, int position);
    }

    public void addListener(ActiviteAdapterListener aListener)
    {
        mListListener.add(aListener);
    }

    private void sendActiviteLongListener(Activite activite, int position) {
        for (int i = mListListener.size() - 1; i >= 0; i--) {
            mListListener.get(i).onLongClickActivite(activite, position);
        }

    }
    private void sendActiviteClickListener(Activite activite, int position) {
        for (int i = mListListener.size() - 1; i >= 0; i--) {
            mListListener.get(i).onClickAtivite(activite, position);
        }

    }
    private void sendInformationListener(Activite activite, int position) {
        for (int i = mListListener.size() - 1; i >= 0; i--) {
            mListListener.get(i).onClickInformation(activite, position);
        }

    }

    private void sendMessageListener(Activite activite, int position) {
        for (int i = mListListener.size() - 1; i >= 0; i--) {
            mListListener.get(i).onClickMessage(activite, position);
        }
    }




}
