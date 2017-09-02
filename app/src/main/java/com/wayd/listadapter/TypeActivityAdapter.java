package com.wayd.listadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.wayd.R;
import com.wayd.bean.TypeActivite;

import java.util.ArrayList;
import java.util.List;

public class TypeActivityAdapter extends BaseAdapter
{

    // Une liste de personnes
    private final List<TypeActivite> mListP;

    // Un mécanisme pour gérer l'affichage graphique depuis un layout XML
    private final LayoutInflater mInflater;

    private final ArrayList<TypeActiviteAdapterListener> mListListener = new ArrayList<>();

    public TypeActivityAdapter(Context context, List<TypeActivite> aListP)
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
        LinearLayout layoutItem;
        // (1) : Réutilisation des layouts
        if (convertView == null)
        {
            // Initialisation de notre item à partir du layout XML "personne_layout.xml"
            layoutItem = (LinearLayout) mInflater.inflate(R.layout.item_typeactivite, parent, false);
        }
        else
        {
            layoutItem = (LinearLayout) convertView;
        }

        layoutItem.setTag((mListP.get(position)));
        TextView nomtypeactivite = (TextView) layoutItem.findViewById(R.id.id_typeactivitenom);
        nomtypeactivite.setText(mListP.get(position).getNom());
        return layoutItem;

    }

	public interface TypeActiviteAdapterListener
    {

    }

    public void addListener(TypeActiviteAdapterListener aListener)
    {
        mListListener.add(aListener);
    }




}
