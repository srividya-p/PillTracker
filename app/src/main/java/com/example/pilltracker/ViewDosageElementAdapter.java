package com.example.pilltracker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.radiobutton.MaterialRadioButton;

import java.util.ArrayList;
import java.util.List;

public class ViewDosageElementAdapter extends ArrayAdapter<String> {

    private Activity context = new Activity();
    private List<String> names = new ArrayList<>();
    private List<String> descriptions = new ArrayList<>();
    private List<String> statuses = new ArrayList<>();

    public ViewDosageElementAdapter(Activity context, List<String> names, List<String> descriptions, List<String> statuses){
        super(context, R.layout.activity_dosage_list_element, names);

        this.context = context;
        this.names = names;
        this.descriptions = descriptions;
        this.statuses = statuses;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.activity_dosage_list_element, null, true);

        TextView name = rowView.findViewById(R.id.mName);
        TextView desc = rowView.findViewById(R.id.mDesc);
        MaterialButton addAgain = rowView.findViewById(R.id.mAddAgain);
        MaterialButton status = rowView.findViewById(R.id.mStatus);

        name.setText(names.get(position));
        desc.setText(descriptions.get(position));
        status.setText(statuses.get(position));

        if(statuses.get(position).equals("Ongoing")){
            status.setTextColor(Color.parseColor("#e6574a"));
            addAgain.setVisibility(View.GONE);
        } else if(statuses.get(position).equals("Generic")) {
            status.setTextColor(Color.parseColor("#0000FF"));
            addAgain.setVisibility(View.GONE);
        } else {
            status.setTextColor(Color.parseColor("#006600"));
        }

        addAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addMedicineIntent = new Intent(context, AddMedicineActivity.class);
                addMedicineIntent.putExtra("name", names.get(position));
                addMedicineIntent.putExtra("desc", descriptions.get(position));
                context.startActivity(addMedicineIntent);
            }
        });

        return rowView;
    }
}
