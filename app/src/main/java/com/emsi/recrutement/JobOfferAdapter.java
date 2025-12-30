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
    private int currentUserId;

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
        holder.setCurrentUserId(currentUserId);
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

    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
    }

    class JobViewHolder extends RecyclerView.ViewHolder {
        private TextView tvJobTitle;
        private TextView tvCompanyName;
        private TextView tvLocationType;
        private TextView tvPublicationDate;
        private ImageButton btnSaveJob;
        private MaterialCardView cardView;
        private View layoutCardContent;
        private DatabaseHelper dbHelper;
        private int currentUserId;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardJobOffer);
            layoutCardContent = itemView.findViewById(R.id.layoutCardContent);
            tvJobTitle = itemView.findViewById(R.id.tv_job_title);
            tvCompanyName = itemView.findViewById(R.id.tv_company_name);
            tvLocationType = itemView.findViewById(R.id.tv_location_type);
            tvPublicationDate = itemView.findViewById(R.id.tv_publication_date);
            btnSaveJob = itemView.findViewById(R.id.btn_save_job);

            dbHelper = new DatabaseHelper(itemView.getContext());

            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onJobClick(jobOffers.get(getAdapterPosition()));
                }
            });

            if (btnSaveJob != null) {
                btnSaveJob.setOnClickListener(v -> {
                    if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION && currentUserId > 0) {
                        JobOffer job = jobOffers.get(getAdapterPosition());

                        // Check current saved state
                        boolean isSaved = dbHelper.isJobSaved(currentUserId, job.getId());

                        if (isSaved) {
                            // Unsave the job
                            dbHelper.unsaveJob(currentUserId, job.getId());
                            btnSaveJob.setImageResource(R.drawable.ic_bookmark_border);
                            btnSaveJob.setColorFilter(0xFF9E9E9E); // Gray
                        } else {
                            // Save the job
                            dbHelper.saveJob(currentUserId, job.getId());
                            btnSaveJob.setImageResource(R.drawable.ic_bookmark_filled);
                            btnSaveJob.setColorFilter(0xFFFFC107); // Yellow
                        }

                        // Notify listener for statistics update
                        listener.onSaveJobClick(job);
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

            // Display publication date
            if (tvPublicationDate != null) {
                tvPublicationDate.setText("Il y a 2 jours"); // Placeholder - will be dynamic later
            }

            // Update bookmark icon based on saved status
            if (btnSaveJob != null && currentUserId > 0) {
                boolean isSaved = dbHelper.isJobSaved(currentUserId, job.getId());
                if (isSaved) {
                    btnSaveJob.setImageResource(R.drawable.ic_bookmark_filled);
                    btnSaveJob.setColorFilter(0xFFFFC107); // Yellow
                } else {
                    btnSaveJob.setImageResource(R.drawable.ic_bookmark_border);
                    btnSaveJob.setColorFilter(0xFF9E9E9E); // Gray
                }
            }
        }

        public void setCurrentUserId(int userId) {
            this.currentUserId = userId;
        }
    }
}
