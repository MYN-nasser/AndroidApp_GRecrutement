package com.emsi.recrutement;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class JobHorizontalAdapter extends RecyclerView.Adapter<JobHorizontalAdapter.JobHorizontalViewHolder> {

    private List<JobOffer> jobOffers;
    private String userEmail;
    private android.content.Context context;

    public JobHorizontalAdapter(List<JobOffer> jobOffers, String userEmail, android.content.Context context) {
        this.jobOffers = jobOffers;
        this.userEmail = userEmail;
        this.context = context;
    }

    @NonNull
    @Override
    public JobHorizontalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_job_horizontal, parent, false);
        return new JobHorizontalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobHorizontalViewHolder holder, int position) {
        JobOffer job = jobOffers.get(position);
        holder.bind(job);
    }

    @Override
    public int getItemCount() {
        return jobOffers != null ? jobOffers.size() : 0;
    }

    public void updateJobs(List<JobOffer> newJobs) {
        this.jobOffers = newJobs;
        notifyDataSetChanged();
    }

    class JobHorizontalViewHolder extends RecyclerView.ViewHolder {
        private TextView tvJobTitle;
        private TextView tvCompany;
        private TextView tvLocation;
        private TextView tvSalary;
        private TextView tvType;

        public JobHorizontalViewHolder(@NonNull View itemView) {
            super(itemView);
            tvJobTitle = itemView.findViewById(R.id.tvJobTitle);
            tvCompany = itemView.findViewById(R.id.tvCompany);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvSalary = itemView.findViewById(R.id.tvSalary);
            tvType = itemView.findViewById(R.id.tvType);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && jobOffers != null) {
                    JobOffer job = jobOffers.get(position);
                    Intent intent = new Intent(context, JobDetailsActivity.class);
                    intent.putExtra("JOB_ID", job.getId());
                    intent.putExtra("USER_EMAIL", userEmail);
                    context.startActivity(intent);
                }
            });
        }

        public void bind(JobOffer job) {
            if (tvJobTitle != null && job.getTitle() != null) {
                tvJobTitle.setText(job.getTitle());
            }
            if (tvCompany != null && job.getCompany() != null) {
                tvCompany.setText(job.getCompany());
            }
            if (tvLocation != null && job.getLocation() != null) {
                tvLocation.setText(job.getLocation());
            }
            if (tvSalary != null && job.getSalary() != null) {
                tvSalary.setText(job.getSalary());
            }
            if (tvType != null && job.getType() != null) {
                tvType.setText(job.getType());
            }
        }
    }
}

