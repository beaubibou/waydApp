package com.wayd.listadapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.wayd.R;
import com.wayd.bean.Message;
import com.wayd.bean.Outils;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends BaseAdapter {
    private final Context mContext;
    // Une liste de personnes
    private final List<Message> mListP;

    // Un mécanisme pour gérer l'affichage graphique depuis un layout XML
    private final LayoutInflater mInflater;

    private final ArrayList<MessageAdapterListener> mListListener = new ArrayList<>();

    public MessageAdapter(Context context, List<Message> aListP) {
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
        LinearLayout layoutItem;
        // (1) : Réutilisation des layouts
        if (convertView == null) {
            // Initialisation de notre item à partir du layout XML "personne_layout.xml"
            layoutItem = (LinearLayout) mInflater.inflate(R.layout.item_message, parent, false);
        } else {
            layoutItem = (LinearLayout) convertView;
        }

        // (2) : Récupération des TextView de notre layout
      //  TextView loginemetteur = (TextView) layoutItem.findViewById(R.id.id_nom);
        TextView corps = (TextView) layoutItem.findViewById(R.id.id_corps);
        TextView datecreation = (TextView) layoutItem.findViewById(R.id.id_datecreation);
        ImageView avatar = (ImageView) layoutItem.findViewById(R.id.avatar);

        LinearLayout imageLayout=(LinearLayout) layoutItem.findViewById(R.id.layoutavatar);
        LinearLayout layout = (LinearLayout) layoutItem.findViewById(R.id.id_singlemessage);
        LinearLayout enveloppe = (LinearLayout) layoutItem.findViewById(R.id.id_env_message);
        Message message = mListP.get(position);

        if (message.getIdemetteur() == Outils.personneConnectee.getId()) {

            enveloppe.setGravity(Gravity.RIGHT);
            layout.setBackgroundResource(R.drawable.bubble2);
            imageLayout.setGravity(Gravity.RIGHT);
           datecreation.setGravity(Gravity.RIGHT);
      //      loginemetteur.setTypeface(null, Typeface.NORMAL);

        } else


        {
            layout.setBackgroundResource(R.drawable.bubble1);
            enveloppe.setGravity(Gravity.LEFT);
            imageLayout.setGravity(Gravity.LEFT);
            datecreation.setGravity(Gravity.LEFT);
            if (!message.isLu()) {
        //        loginemetteur.setTypeface(null, Typeface.BOLD);
                corps.setTypeface(null, Typeface.BOLD);
            }
            else {
        //        loginemetteur.setTypeface(null, Typeface.NORMAL);
                corps.setTypeface(null, Typeface.NORMAL);
            }
        }

        String fromServerUnicodeDecoded = StringEscapeUtils.unescapeJava(message.getCorps());
        corps.setText(fromServerUnicodeDecoded);
    //    loginemetteur.setText(message.getPseudoemetteur());
        layoutItem.setTag(position);
        layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer position = (Integer) v.getTag();
                sendListener(mListP.get(position), position);

            }
        });


        layoutItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Integer position = (Integer) v.getTag();
                sendLongListener(mListP.get(position), position);
                return true;
            }
        });

        avatar.setTag(position);
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer position = (Integer) v.getTag();
                sendPhotoListener(mListP.get(position), position);
            }
        });
        datecreation.setText(message.getDateCreationStr());
        avatar.setImageDrawable(Outils.getAvatarDrawable(mContext,mListP.get(position).getPhoto()));
          return layoutItem;
    }

    public interface MessageAdapterListener {
        void onClickMessage(Message item, int position);
        void onLongClickMessage(Message item, int position);
        void onPhotoClickMessage(Message item, int position);


    }

    public void addListener(MessageAdapterListener aListener) {
        mListListener.add(aListener);
    }

    private void sendListener(Message item, int position) {
        for (int i = mListListener.size() - 1; i >= 0; i--) {
            mListListener.get(i).onClickMessage(item, position);
        }
    }

    private void sendLongListener(Message item, int position) {
        for (int i = mListListener.size() - 1; i >= 0; i--) {
            mListListener.get(i).onLongClickMessage(item, position);
        }
    }


    private void sendPhotoListener(Message item, int position) {
        for (int i = mListListener.size() - 1; i >= 0; i--) {
            mListListener.get(i).onPhotoClickMessage(item, position);
        }
    }




}
