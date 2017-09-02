package com.wayd.listadapter;

import java.util.ArrayList;
import java.util.List;

import com.application.wayd.R;
import com.wayd.bean.Avis;
import com.wayd.bean.Outils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AvisAdapter extends BaseAdapter
{

    // Une liste de personnes
    private final List<Avis> mListP;
    private final Context mContext;


    // Un mécanisme pour gérer l'affichage graphique depuis un layout XML
    private final LayoutInflater mInflater;

    private final ArrayList<AvisAdapterListener> mListListener = new ArrayList<>();

    public AvisAdapter(Context context, List<Avis> aListP)
    {
        mContext = context;
        mListP = aListP;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount()
    {
        return mListP.size(); // DOCUMENTEZ_MOI Raccord de méthode auto-généré
    }

    @Override
    public Object getItem(int position)
    {
        return mListP.get(position); // DOCUMENTEZ_MOI Raccord de méthode auto-généré
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
            layoutItem = (RelativeLayout) mInflater.inflate(R.layout.item_avisprofil, parent, false);
        }
        else
        {
            layoutItem = (RelativeLayout) convertView;
        }
        Avis avis=mListP.get(position);
        // (2) : Récupération des TextView de notre layout
        RatingBar tv_note = (RatingBar) layoutItem.findViewById(R.id.note);
       //TextView tv_dateAvis = (TextView) layoutItem.findViewById(R.id.id_datecreation);
        TextView tv_notateur = (TextView) layoutItem.findViewById(R.id.pseudo);
        ImageView photo = (ImageView) layoutItem.findViewById(R.id.iconactivite);
        TextView TV_Titre= (TextView) layoutItem.findViewById(R.id.titreactivite);
        TextView TV_apercuavis= (TextView) layoutItem.findViewById(R.id.apercuavis);
        TextView TV_DateNotation= (TextView) layoutItem.findViewById(R.id.datenotation);


        // (3) : Renseignement des valeurs
        tv_note.setStepSize(0.5f);
        tv_note.setRating((float) avis.getNote());
        tv_note.setIsIndicator(true);
   //     tv_dateAvis.setText(avis.getDateNotationStr());
        TV_apercuavis.setText(avis.getAvisCourtUnicode());
        TV_Titre.setText(avis.getTitreactivite());
        tv_notateur.setText(avis.getPseudonotateur());
        TV_DateNotation.setText(avis.getDateNotationStr());
     //   Im_Photo.setImageBitmap(avis.getPhotonotateur());

             photo.setImageDrawable(Outils.getAvatarDrawable(mContext,avis.getPhotonotateur()));

        layoutItem.setTag(avis);
        
        // ------------ Début de l'ajout -------
        // On mémorise la position de la "Personne" dans le composant textview
       

        return layoutItem;

    }

	public interface AvisAdapterListener
    {
        void onClickAvis(Avis item, int position);
    }

    public void addListener(AvisAdapterListener aListener)
    {
        mListListener.add(aListener);
    }




}
