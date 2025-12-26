package com.emsi.recrutement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class JobOfferAdapter extends RecyclerView.Adapter<JobOfferAdapter.JobViewHolder> {

    private List<JobOffer> jobOffers;
    private OnJobClickListener listener;

    public interface OnJobClickListener {
        void onJobClick(JobOffer job);
        void onSaveJobClick(JobOffer job);
    }

    public JobOfferAdapter(List<JobOffer> jobOffers, OnJobClickListener listener) {
        this.jobOffers = jobOffers;
        this.listener = listener;
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_job_offer_card, parent, false);
        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
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

    class JobViewHolder extends RecyclerView.ViewHolder {
        private TextView tvJobTitle;
        private TextView tvCompanyName;
        private TextView tvLocationType;
        private ImageButton btnSaveJob;
        private MaterialCardView cardView;
        private View layoutCardContent;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardJobOffer);
            layoutCardContent = itemView.findViewById(R.id.layoutCardContent);
            tvJobTitle = itemView.findViewById(R.id.tv_job_title);
            tvCompanyName = itemView.findViewById(R.id.tv_company_name);
            tvLocationType = itemView.findViewById(R.id.tv_location_type);
            btnSaveJob = itemView.findViewById(R.id.btn_save_job);

            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onJobClick(jobOffers.get(getAdapterPosition()));
                }
            });

            if (btnSaveJob != null) {
                btnSaveJob.setOnClickListener(v -> {
                    if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                        listener.onSaveJobClick(jobOffers.get(getAdapterPosition()));
                    }
                });
            }
        }

        public void bind(JobOffer job) {
            // Appliquer une bordure bleue à toutes les cartes
            if (cardView != null) {
                cardView.setStrokeColor(ContextCompat.getColor(itemView.getContext(), R.color.primary_color));
            }

            if (tvJobTitle != null && job.getTitle() != null) {
                tvJobTitle.setText(job.getTitle());
            }
            if (tvCompanyName != null && job.getCompany() != null) {
                tvCompanyName.setText(job.getCompany());
            }
            
            if (tvLocationType != null) {
                String locationType = job.getLocation() != null ? job.getLocation() : "";
                if (job.getType() != null && !job.getType().isEmpty()) {
                    if (!locationType.isEmpty()) {
                        locationType += " • " + job.getType();
                    } else {
                        locationType = job.getType();
                    }
                }
                tvLocationType.setText(locationType);
            }
        }
    }
}

