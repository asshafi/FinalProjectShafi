package com.example.finalprojectshafi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalprojectshafi.R;
import com.example.finalprojectshafi.database.Task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> implements Filterable {

    public interface OnTaskActionListener {
        void onEditClick(Task task);
        void onDeleteClick(Task task, int position);
    }

    private Context context;
    public List<Task> list;
    public List<Task> listFull;
    private OnTaskActionListener listener;

    public TaskAdapter(Context context, List<Task> list, OnTaskActionListener listener) {
        this.context = context;
        this.list = list;
        this.listFull = new ArrayList<>(list);
        this.listener = listener;
    }

    public void setData(List<Task> newList) {
        this.list.clear();
        this.list.addAll(newList);
        this.listFull.clear();
        this.listFull.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Task task = list.get(position);

        holder.tvTitle.setText(task.title);
        holder.tvDescription.setText(task.description);

        String status = task.status.equals("Completed") ? "Completed" : "Pending";
        holder.tvStatus.setText(status);

        if (status.equals("Completed")) {
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_completed));
        } else {
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_pending));
        }

        long duration = task.getDurationInMinutes();
        holder.tvDuration.setText(duration + " minutes");

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        holder.tvDueDate.setText("Due: " + sdf.format(task.dueDate));

        switch (task.priority) {
            case "High":
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.priority_high));
                break;
            case "Medium":
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.priority_medium));
                break;
            case "Low":
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.priority_low));
                break;
        }

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(task);
            }
        });
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(task, position);
            }
        });
    }

    @Override
    public int getItemCount() { return list.size(); }

    @Override
    public Filter getFilter() {
        return taskFilter;
    }

    private Filter taskFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Task> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(listFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Task item : listFull) {
                    if (item.title.toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list.clear();
            list.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvTitle, tvDescription, tvStatus, tvDuration, tvDueDate;
        ImageButton btnEdit, btnDelete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvDueDate = itemView.findViewById(R.id.tvDueDate);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
