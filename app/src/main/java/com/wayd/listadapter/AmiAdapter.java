package com.wayd.listadapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.application.wayd.R;
import com.wayd.bean.Ami;
import com.wayd.bean.Outils;

import java.util.ArrayList;
import java.util.List;

public class AmiAdapter extends BaseAdapter {

    // Une liste de personnes
    private final List<Ami> listCompleteAmi;
    private final List<Ami> listFiltreeAmi = new ArrayList<>();
    private final Context mContext;
    private String achercher = "";

    // Un mécanisme pour gérer l'affichage graphique depuis un layout XML
    private final LayoutInflater mInflater;

    private final ArrayList<AmiAdapterListener> mListListener = new ArrayList<>();

    public AmiAdapter(Context context, List<Ami> aListP) {
        mContext = context;
        listCompleteAmi = aListP;
        mInflater = LayoutInflater.from(mContext);
    }

    public void setFiltre() {
        listFiltreeAmi.clear();
        for (Ami ami : listCompleteAmi) {
            listFiltreeAmi.add(ami);

        }

    }

    @Override
    public int getCount() {
        return listFiltreeAmi.size(); // DOCUMENTEZ_MOI Raccord de méthode auto-généré
    }

    @Override
    public Object getItem(int position) {
        return listFiltreeAmi.get(position); // DOCUMENTEZ_MOI Raccord de méthode auto-généré
    }

    @Override
    public long getItemId(int position) {
        return position;

    }

    public void setSearch(AutoCompleteTextView searchtext) {

        searchtext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                achercher = charSequence.toString().toLowerCase();
                appliqueFiltre();
                notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void appliqueFiltre() {
        listFiltreeAmi.clear();
        for (Ami ami : listCompleteAmi) {
            if (ami.getPseudo().toLowerCase().contains(achercher))
                listFiltreeAmi.add(ami);

        }


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RelativeLayout layoutItem;
        // (1) : Réutilisation des layouts

        if (convertView == null) {
            // Initialisation de notre item à partir du layout XML "personne_layout.xml"
            layoutItem = (RelativeLayout) mInflater.inflate(R.layout.item_amis, parent, false);
        } else {
            layoutItem = (RelativeLayout) convertView;
        }

        // (2) : Récupération des TextView de notre layout
        TextView nom = (TextView) layoutItem.findViewById(R.id.pseudo);
        TextView amidepuis = (TextView) layoutItem.findViewById(R.id.amidepuis);
        ImageView photo = (ImageView) layoutItem.findViewById(R.id.iconactivite);
        ImageView Img_information = (ImageView) layoutItem.findViewById(R.id.informationami);
        ImageView Img_message = (ImageView) layoutItem.findViewById(R.id.messageami);
        RatingBar ratingBar = (RatingBar) layoutItem.findViewById(R.id.note);
        Ami ami = listFiltreeAmi.get(position);
        ratingBar.setMax(5);
        ratingBar.setStepSize(0.5f);
        ratingBar.setRating((float) ami.getNote());
        //  photo.setImageBitmap(Outils.getPhotoPersonne(mContext, ami.getPhoto()));
        photo.setImageDrawable(Outils.getAvatarDrawable(mContext, ami.getPhoto()));
        nom.setText(ami.getPseudo());
        Img_information.setTag(position);
        Img_information.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer position = (Integer) v.getTag();
                // On prÃ©vient les listeners qu'il y a eu un clic sur le TextView "TV_Nom".
                sendInforamtionListener(listFiltreeAmi.get(position), position);
            }
        });

        Img_message.setTag(position);
        Img_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer position = (Integer) v.getTag();
                // On prÃ©vient les listeners qu'il y a eu un clic sur le TextView "TV_Nom".
                sendMessageListener(listFiltreeAmi.get(position), position);
            }
        });
        layoutItem.setTag(position);
        layoutItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Integer position = (Integer) v.getTag();
                // On prÃ©vient les listeners qu'il y a eu un clic sur le TextView "TV_Nom".
                sendAmiLongListener(listFiltreeAmi.get(position), position);
                return true;
            }
        });

        layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer position = (Integer) v.getTag();
                // On prÃ©vient les listeners qu'il y a eu un clic sur le TextView "TV_Nom".
                sendAmiClickListener(listFiltreeAmi.get(position), position);
            }
        });

        amidepuis.setText(ami.getDepuisle());
        return layoutItem;
    }

    public interface AmiAdapterListener {
        void onClickAmi(Ami ami, int position);

        void onLongClickAmi(Ami ami, int position);

        void onClickInformation(Ami ami, int position);

        void onClickMessage(Ami ami, int position);

    }

    public void addListener(AmiAdapterListener aListener) {
        mListListener.add(aListener);
    }


    private void sendAmiLongListener(Ami ami, int position) {
        for (int i = mListListener.size() - 1; i >= 0; i--) {
            mListListener.get(i).onLongClickAmi(ami, position);
        }

    }

    private void sendMessageListener(Ami ami, int position) {
        for (int i = mListListener.size() - 1; i >= 0; i--) {
            mListListener.get(i).onClickMessage(ami, position);
        }
    }

    private void sendInforamtionListener(Ami ami, int position) {
        for (int i = mListListener.size() - 1; i >= 0; i--) {
            mListListener.get(i).onClickInformation(ami, position);
        }
    }

    private void sendAmiClickListener(Ami ami, int position) {
        for (int i = mListListener.size() - 1; i >= 0; i--) {
            mListListener.get(i).onClickAmi(ami, position);
        }


    }


}
