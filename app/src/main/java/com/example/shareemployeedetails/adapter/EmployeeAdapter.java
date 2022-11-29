package com.example.shareemployeedetails.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shareemployeedetails.R;
import com.example.shareemployeedetails.models.EmployeeDetails;

import java.util.ArrayList;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.MyViewHolder> {

    Context context;
    ArrayList<EmployeeDetails> empDetailsArrayList;

    public EmployeeAdapter(Context context, ArrayList<EmployeeDetails> empDetailsArrayList) {
        this.context = context;
        this.empDetailsArrayList = empDetailsArrayList;
    }

    @NonNull
    @Override
    public EmployeeAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.items_empdet, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeAdapter.MyViewHolder holder, int position) {
        holder.txtEmpName.setText(empDetailsArrayList.get(position).getEmployeeName());
    }

    @Override
    public int getItemCount() {
        return empDetailsArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtEmpName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtEmpName = itemView.findViewById(R.id.txtEmpName);
        }
    }
}
