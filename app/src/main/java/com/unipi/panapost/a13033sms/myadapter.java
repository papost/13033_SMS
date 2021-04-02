package com.unipi.panapost.a13033sms;

import android.app.Application;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class myadapter extends RecyclerView.Adapter<myadapter.myviewholder> {
    private List<model> dataholder;
    public int selectedStartPosition = 0;

    public myadapter(ArrayList<model> dataholder) {
        this.dataholder = dataholder;
    }


    @NonNull
    @Override
    //μέθοδος που καλείται απο τον RecyclerView όταν χρειάζεται ενα νέο RecyclerView.ViewHolder για την αναπαράσταση ενός νέου item
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singlerow,parent,false);
        //το νέο view "τοποθετείται" κάτω από τον "πρόγονό" του
        return new myviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position) {//μέθοδος που καλείται απο τον RecyclerView για την εμφάνιση των δεδομένων
        //σε "συγκεκριμένη θέση", ανάλογα με την θέση στο ArrayList dataholder
        holder.id.setText(dataholder.get(position).getId());
        holder.message.setText(dataholder.get(position).getMessage());
        holder.radioButton.setChecked(position==selectedStartPosition);//check a certain radio button
        holder.radioButton.setTag(position);
        holder.radioButton.setOnClickListener(v -> {//onclick listener in order to check and get the position of selected radio button
            selectedStartPosition = (int) v.getTag();
            notifyDataSetChanged();
        });

    }

    @Override
    public int getItemCount() {
        return dataholder.size();
    }//μέθοδος για την επιστροφή τoυ συνολικού αριθμού των items που βρίσκονται στον adapter

    public class myviewholder extends RecyclerView.ViewHolder  {
        TextView id,message;
        RadioButton radioButton;
        public myviewholder(@NonNull View itemView) {
            super(itemView);
            id=itemView.findViewById(R.id.id);
            message=itemView.findViewById(R.id.message);
            radioButton=itemView.findViewById(R.id.radio);
        }
    }
    public model getSelected(){//get the position of selected position
        if (selectedStartPosition!=-1)
            return dataholder.get(selectedStartPosition);
        return null;
    }
}
