package com.wayd.listadapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.application.wayd.R;
import com.wayd.bean.Discussion;
import com.wayd.bean.Outils;

import java.util.ArrayList;
import java.util.List;

public class DiscussionAdapter extends BaseAdapter {
    private final Context mContext;
    // Une liste de personnes
    private final List<Discussion> mListP;

    // Un mécanisme pour gérer l'affichage graphique depuis un layout XML
    private final LayoutInflater mInflater;
    private final ArrayList<DiscussionAdapterListener> mListListener = new ArrayList<>();

    public DiscussionAdapter(Context context, List<Discussion> aListP) {
        mContext = context;
        mListP = aListP;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mListP.size(); // DOCUMENTEZ_MOI Raccord de méthode auto-généré
    }

    @Override
    public Object getItem(int position) {
        return mListP.get(position); // DOCUMENTEZ_MOI Raccord de méthode auto-généré
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
            layoutItem = (RelativeLayout) mInflater.inflate(R.layout.item_discussion, parent, false);
        } else {
            layoutItem = (RelativeLayout) convertView;
        }

        // (2) : Récupération des TextView de notre layout
        TextView loginemetteur = (TextView) layoutItem.findViewById(R.id.pseudo);
        TextView corps = (TextView) layoutItem.findViewById(R.id.id_corps);
        TextView datecreation = (TextView) layoutItem.findViewById(R.id.id_datecreation);
        ImageView photo=(ImageView) layoutItem.findViewById(R.id.iconactivite);
        TextView TV_NonLus = (TextView) layoutItem.findViewById(R.id.nbrnonlus);
        Discussion discussion = mListP.get(position);
        loginemetteur.setText(mListP.get(position).getTitre());
        TV_NonLus.setText((mListP.get(position).getNbrnonluStr()));

        loginemetteur.setTag(position) ;

        layoutItem.setBackgroundColor(Color.WHITE);


        switch (discussion.getType()) {

            case Discussion.STAND_ALONE:

              //  photo.setImageDrawable(Outils.getAvatarDrawable(mContext,discussion.getPhoto()));
                photo.setImageDrawable(Outils.getAvatarDrawable(mContext,discussion.getPhoto()));
                break;

            case Discussion.GROUP_TALK:
                photo.setBackgroundColor(Color.parseColor("#FFFFFF"));
                photo.setImageResource(Outils.getActiviteMipMap(discussion.getIdtypeactivite()));
                break;
        }

     //  String fromServerUnicodeDecoded = StringEscapeUtils.unescapeJava(mListP.get(position).getCorpsCourtUnicode());

        corps.setText(mListP.get(position).getCorps());
      //  System.out.println("*****************************************taille max"+corps.getm);

      // corps.setText(mListP.get(position).getCorpsCourt());
        datecreation.setText(mListP.get(position).getDateCreationStr());
        layoutItem.setTag(position);
        layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer position = (Integer) v.getTag();
                // On prÃ©vient les listeners qu'il y a eu un clic sur le TextView "TV_Nom".
                sendListener(mListP.get(position), position);

            }
        });

        layoutItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Integer position = (Integer) v.getTag();
                     sendLongListener(mListP.get(position), position);
                return false;
            }
        });

    return layoutItem;

    }

    public interface DiscussionAdapterListener {
        void onClickDiscussion(Discussion item, int position);
        void onLongClickDiscussion(Discussion item, int position);

    }

    public void addListener(DiscussionAdapterListener aListener) {
        mListListener.add(aListener);
    }

    private void sendListener(Discussion item, int position) {
        for (int i = mListListener.size() - 1; i >= 0; i--) {
            mListListener.get(i).onClickDiscussion(item, position);
        }
    }
    private void sendLongListener(Discussion item, int position) {
        for (int i = mListListener.size() - 1; i >= 0; i--) {
            mListListener.get(i).onLongClickDiscussion(item, position);
        }
    }


}
