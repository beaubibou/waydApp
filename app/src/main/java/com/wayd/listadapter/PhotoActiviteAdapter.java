package com.wayd.listadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.application.wayd.R;
import com.wayd.bean.Outils;
import com.wayd.bean.Participant;
import com.wayd.bean.PhotoActivite;

import java.util.ArrayList;
import java.util.List;

public class PhotoActiviteAdapter extends BaseAdapter {

    // Une liste de personnes
    private final List<PhotoActivite> listPhotos;
    private final Context mContext;

    // Un mécanisme pour gérer l'affichage graphique depuis un layout XML
    private final LayoutInflater mInflater;

    private final ArrayList<PhotoActiviteAdapterListener> mListListener = new ArrayList<>();

    public PhotoActiviteAdapter(Context context, List<PhotoActivite> aListP) {
        mContext = context;
        listPhotos = aListP;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return listPhotos.size(); // DOCUMENTEZ_MOI Raccord de méthode auto-généré
    }

    @Override
    public Object getItem(int position) {
        return listPhotos.get(position); // DOCUMENTEZ_MOI Raccord de méthode auto-généré
    }

    @Override
    public long getItemId(int position) {
        return position;

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout layoutItem;
        // (1) : Réutilisation des layouts

        if (convertView == null) {
            // Initialisation de notre item à partir du layout XML "personne_layout.xml"
            layoutItem = (LinearLayout) mInflater.inflate(R.layout.item_photoactivite, parent, false);
        } else {
            layoutItem = (LinearLayout) convertView;
        }


       ImageView photo = (ImageView) layoutItem.findViewById(R.id.iconactivite);
       PhotoActivite photoActivite = listPhotos.get(position);
        photo.setImageBitmap(photoActivite.getPhotoBitmap());
         return layoutItem;
    }

    public interface PhotoActiviteAdapterListener {
         void onLongClickPartiticipant(Participant participant, int position);
         void onClickParticipant(Participant participant, int position);
    }

    public void addListener(PhotoActiviteAdapterListener aListener) {
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
