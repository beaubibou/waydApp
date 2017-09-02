package com.wayd.listadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.application.wayd.R;
import com.wayd.bean.Outils;
import com.wayd.bean.Preference;

import java.util.ArrayList;
import java.util.List;

public class PreferencesAdapter extends BaseAdapter
{
    // Une liste de personnes
    private final List<Preference> mListP;

    // Un mécanisme pour gérer l'affichage graphique depuis un layout XML
    private final LayoutInflater mInflater;

    private final ArrayList<PreferenceAdapterListener> mListListener = new ArrayList<>();

    public PreferencesAdapter(Context context, List<Preference> aListP)
    {
        mListP = aListP;
        mInflater = LayoutInflater.from(context);
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
            layoutItem = (RelativeLayout) mInflater.inflate(R.layout.item_preference, parent, false);
        }
        else
        {
            layoutItem = (RelativeLayout) convertView;
        }

        Preference preference = mListP.get(position);
        TextView nom= (TextView) layoutItem.findViewById(R.id.nompref);
        ImageView iconActivite=(ImageView) layoutItem.findViewById(R.id.iconcActivite);

        nom.setText(preference.getNom());
        iconActivite.setImageResource(Outils.getActiviteMipMap(preference.getIdTypeactivite()));
        Switch active= (Switch) layoutItem.findViewById(R.id.activepref);
        active.setTag(preference);
        active.setChecked(preference.isActive());
        active.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                ((Preference)compoundButton.getTag()).setActive(b);
            }
        });

        return layoutItem;

    }

    public interface PreferenceAdapterListener
    {

    }






}
