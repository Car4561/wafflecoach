package com.peru.wafflecoach;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AdapterPsychotherapist extends RecyclerView.Adapter<AdapterPsychotherapist.viewHolder> {

        private JSONArray items;
        private Context context;

        // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
        public static class viewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public LinearLayout lytPsychotherapist;
        public TextView txtName;
        public TextView txtLine;

        public TextView txtPrice;
        public ImageView imgUser;

        public LinearLayout lytFavorites;
        public ImageView imgFavorites;
        public TextView txtFavorites;

        public viewHolder(LinearLayout view) {
            super(view);
            this.lytPsychotherapist = view.findViewById(R.id.lyt_item_psychotherapist);
            this.txtName = view.findViewById(R.id.txt_name);
            this.txtLine = view.findViewById(R.id.txt_line);
            this.imgUser = view.findViewById(R.id.img_specialist);
            this.txtPrice = view.findViewById(R.id.txt_price);

            this.lytFavorites = view.findViewById(R.id.lyt_favorites);
            this.imgFavorites = view.findViewById(R.id.img_favorites);
            this.txtFavorites = view.findViewById(R.id.txt_favorites);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AdapterPsychotherapist(Context _context, JSONArray _items) {
        items = _items;
        context = _context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        return new viewHolder((LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_psychotherapist, parent, false));
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final viewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        //holder.textView.setText(items[position]);
        try {
            final JSONObject item = items.getJSONObject(position);
            ServiceGlobal.console(item.toString());
            JSONObject attributes = item.getJSONObject("attributes");
            ServiceGlobal.console(attributes.toString());
           final String fullName = attributes.getString("name") + " " + attributes.getString("surname");

     //       final String fullName = attributes.get("name").toString().split(" ")[0] + " " + attributes.get("surname").toString().split(" ")[0];

            holder.txtName.setText(fullName);
            holder.txtLine.setText(attributes.getString("line"));
            if(item.has("favorites")){
                holder.txtFavorites.setText(item.getString("favorites"));
            }else{
                holder.txtFavorites.setText("0");
            }
            if(item.has("isFavorite") && item.getString("isFavorite").equals("1")){
                holder.imgFavorites.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_favorite));
            }
            holder.txtPrice.setText("S/ " + attributes.getString("price"));
            String urlImage = "";
            if(attributes.has("photo"))
                urlImage = attributes.getString("photo");
            if(!urlImage.equals(""))

                ServiceGlobal.loadCircleImage(urlImage, holder.imgUser);
            else
                ServiceGlobal.loadCircleImage("https://www.cobiapp.com/assets/images/blankprofile.png", holder.imgUser);

            holder.lytPsychotherapist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ActivityPsychotherapist.class);
                    intent.putExtra("psychotherapist", item.toString());
                    context.startActivity(intent);
                }
            });
            holder.imgUser.post(new Runnable() {
                @Override
                public void run() {
                    Log.d("tamaño", String.valueOf(holder.imgUser.getClipBounds())+", "+String.valueOf(holder.imgUser.getMeasuredWidth()    )+"POS: ");


                }
            });
            holder.lytFavorites.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ServiceRest serviceRest = new ServiceRest(context);
                    try {
                        if(context.getResources().getDrawable(R.drawable.icon_favorite).getConstantState() == holder.imgFavorites.getDrawable().getConstantState()){
                            holder.imgFavorites.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_favorite_off));
                            Integer countFavorites = Integer.valueOf(holder.txtFavorites.getText().toString());
                            countFavorites = countFavorites - 1;
                            holder.txtFavorites.setText(countFavorites.toString());
                            serviceRest.delete("favorite?psychotherapist=" + item.getString("id"), new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    //ServiceGlobal.alerta(context, "deleted");
                                }
                            });
                        }else{
                            holder.imgFavorites.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_favorite));
                            Integer countFavorites = Integer.valueOf(holder.txtFavorites.getText().toString());
                            countFavorites = countFavorites + 1;
                            holder.txtFavorites.setText(countFavorites.toString());
                            JSONObject bodyPost = new JSONObject();
                            bodyPost.put("idPsychotherapist", item.getString("id"));
                            serviceRest.post("favorite", bodyPost, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    //ServiceGlobal.alerta(context, "done");
                                }
                            });
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.length();
    }
}