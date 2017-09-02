package com.wayd.listadapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.wayd.R;
import com.wayd.bean.Notification;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends BaseAdapter {

    // Une liste de personnes
    private final List<Notification> mListP;

    // Un mécanisme pour gérer l'affichage graphique depuis un layout XML
    private final LayoutInflater mInflater;

    private final ArrayList<NotificationAdapterListener> mListListener = new ArrayList<>();

    public NotificationAdapter(Context context, List<Notification> aListP) {
        mListP = aListP;
        mInflater = LayoutInflater.from(context);
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
            layoutItem = (LinearLayout) mInflater.inflate(R.layout.item_notification, parent, false);
        } else {
            layoutItem = (LinearLayout) convertView;
        }

        // (2) : Récupération des TextView de notre layout
        //   TextView loginemetteur = (TextView) layoutItem.findViewById(R.id.id_loginemetteur);
        TextView corps = (TextView) layoutItem.findViewById(R.id.id_corps);
        TextView datecreation = (TextView) layoutItem.findViewById(R.id.id_datecreation);

        Notification notification = mListP.get(position);


       //corps.setText(notification.getMessage());
        corps.setText(notification.getMessageUnicode());


        datecreation.setText(notification.getDateCreationStr());

        if (!notification.isLu())
            corps.setTypeface(null, Typeface.BOLD);
        else
            corps.setTypeface(null, Typeface.NORMAL);

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
            public boolean onLongClick(View view) {
                Integer position = (Integer) view.getTag();
                sendLongListener(mListP.get(position), position);
                return true;
            }
        });
        return layoutItem;
    }

    public interface NotificationAdapterListener {
        void onClickNotification(Notification item, int position);
        void onLongClickMessage(Notification item, int position);


    }

    public void addListener(NotificationAdapterListener aListener) {
        mListListener.add(aListener);
    }

    private void sendListener(Notification item, int position) {
        for (int i = mListListener.size() - 1; i >= 0; i--) {
            mListListener.get(i).onClickNotification(item, position);
        }
    }
    private void sendLongListener(Notification item, int position) {
        for (int i = mListListener.size() - 1; i >= 0; i--) {
            mListListener.get(i).onLongClickMessage(item, position);
        }
    }

}
