package com.unipi.panapost.a13033sms;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MessagesAdapter extends  RecyclerView.Adapter<MessagesAdapter.ViewHolder>{

    List<model> messages ;
    Context context;
    DBHelper dbHelper;


    public MessagesAdapter(List<model> messages, Context context) {
        this.messages = messages;
        this.context = context;
        dbHelper = new DBHelper(context);
    }

    @NonNull
    @Override
    //μέθοδος που καλείται απο τον RecyclerView όταν χρειάζεται ενα νέο RecyclerView.ViewHolder για την αναπαράσταση ενός νέου item
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.message_item,parent,false);
        //το νέο view "τοποθετείται" κάτω από τον "πρόγονό" του
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    //μέθοδος που καλείται απο τον RecyclerView για την εμφάνιση των δεδομένων
    //σε "συγκεκριμένη θέση", ανάλογα με την θέση στο ArrayList dataholder
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final  model model = messages.get(position);
        holder.textView.setText(model.getId());
        holder.editText.setText(model.getMessage());
        holder.button_Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = holder.textView.getText().toString();
                String message = holder.editText.getText().toString();
                if(message.isEmpty()){
                    Toast.makeText(context,R.string.fillall,Toast.LENGTH_LONG).show();
                }else {
                    dbHelper.updatemessage(new model(id, message));
                    notifyDataSetChanged();
                    ((Activity) context).finish();
                    context.startActivity(((Activity) context).getIntent());
                }
            }
        });
        holder.button_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.deletemessage(model.getId());
                messages.remove(position);
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }//μέθοδος για την επιστροφή τoυ συνολικού αριθμού των items που βρίσκονται στον adapter

    public  class ViewHolder extends  RecyclerView.ViewHolder{
        EditText editText;
        TextView textView;
        Button button_Edit, button_delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_id);
            editText = itemView.findViewById(R.id.edittext_name);
            button_Edit = itemView.findViewById(R.id.button_edit);
            button_delete = itemView.findViewById(R.id.button_delete);

        }
    }


}
