package com.emsi.recrutement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.ApplicationViewHolder> {

    private List<DatabaseHelper.ApplicationInfo> applications;

    public ApplicationAdapter(List<DatabaseHelper.ApplicationInfo> applications) {
        this.applications = applications;
    }

    @NonNull
    @Override
    public ApplicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_application, parent, false);
        return new ApplicationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApplicationViewHolder holder, int position) {
        DatabaseHelper.ApplicationInfo application = applications.get(position);
        holder.bind(application);
    }

    @Override
    public int getItemCount() {
        return applications != null ? applications.size() : 0;
    }

    public void updateApplications(List<DatabaseHelper.ApplicationInfo> newApplications) {
        this.applications = newApplications;
        notifyDataSetChanged();
    }

    class ApplicationViewHolder extends RecyclerView.ViewHolder {
        private TextView tvJobTitle;
        private TextView tvCompanyName;
        private TextView tvStatus;
        private TextView tvApplicationDate;

        public ApplicationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvJobTitle = itemView.findViewById(R.id.tvJobTitle);
            tvCompanyName = itemView.findViewById(R.id.tvCompanyName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvApplicationDate = itemView.findViewById(R.id.tvApplicationDate);
        }

        public void bind(DatabaseHelper.ApplicationInfo application) {
            if (tvJobTitle != null) {
                tvJobTitle.setText(application.getJobTitle());
            }
            if (tvCompanyName != null) {
                tvCompanyName.setText(application.getCompany());
            }
            if (tvStatus != null) {
                String statusText = application.getStatus();
                tvStatus.setText(statusText);
                // Changer la couleur selon le statut
                int colorRes = android.R.color.darker_gray;
                if ("Acceptée".equals(statusText)) {
                    colorRes = android.R.color.holo_green_dark;
                } else if ("Refusée".equals(statusText)) {
                    colorRes = android.R.color.holo_red_dark;
                }
                tvStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), colorRes));
            }
            if (tvApplicationDate != null && application.getDate() != null) {
                tvApplicationDate.setText("Postulé: " + application.getDate());
            }
        }
    }
}

