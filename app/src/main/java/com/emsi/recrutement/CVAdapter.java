package com.emsi.recrutement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CVAdapter extends RecyclerView.Adapter<CVAdapter.CVViewHolder> {

    private List<CV> cvList;
    private OnCVActionListener listener;

    public interface OnCVActionListener {
        void onCVClick(CV cv);

        void onDeleteClick(CV cv);
    }

    public CVAdapter(List<CV> cvList, OnCVActionListener listener) {
        this.cvList = cvList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CVViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cv_card, parent, false);
        return new CVViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CVViewHolder holder, int position) {
        CV cv = cvList.get(position);
        holder.bind(cv, listener);
    }

    @Override
    public int getItemCount() {
        return cvList != null ? cvList.size() : 0;
    }

    public void updateCVs(List<CV> newCVList) {
        this.cvList = newCVList;
        notifyDataSetChanged();
    }

    static class CVViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitre, tvDomaine, tvFileName, tvDate, tvPreview;
        ImageButton btnDelete;

        public CVViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitre = itemView.findViewById(R.id.tvCVTitre);
            tvDomaine = itemView.findViewById(R.id.tvCVDomaine);
            tvFileName = itemView.findViewById(R.id.tvCVFileName);
            tvDate = itemView.findViewById(R.id.tvCVDate);
            tvPreview = itemView.findViewById(R.id.tvCVPreview);
            btnDelete = itemView.findViewById(R.id.btnDeleteCV);
        }

        public void bind(CV cv, OnCVActionListener listener) {
            tvTitre.setText(cv.getTitre());

            if (cv.getDomaine() != null && !cv.getDomaine().isEmpty()) {
                tvDomaine.setText(cv.getDomaine());
                tvDomaine.setVisibility(View.VISIBLE);
            } else {
                tvDomaine.setVisibility(View.GONE);
            }

            tvFileName.setText(cv.getFileName());
            tvDate.setText(formatDate(cv.getUploadDate()));

            // Set preview text (you can customize this based on CV content)
            String preview = generatePreview(cv);
            tvPreview.setText(preview);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCVClick(cv);
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(cv);
                }
            });
        }

        private String generatePreview(CV cv) {
            // Generate a realistic preview based on CV info
            // In a real app, you would extract text from the PDF file
            // For now, we'll create a sample preview based on available data

            if (cv.getPreview() != null && !cv.getPreview().isEmpty()) {
                return cv.getPreview();
            }

            // Generate sample preview text
            StringBuilder preview = new StringBuilder();

            if (cv.getDomaine() != null && !cv.getDomaine().isEmpty()) {
                preview.append("Profil professionnel en ").append(cv.getDomaine()).append(".\n");
            } else {
                preview.append("Profil professionnel.\n");
            }

            preview.append("Expérience et compétences détaillées.\n");
            preview.append("Formation académique et certifications...");

            return preview.toString();
        }

        private String formatDate(String dateString) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date date = inputFormat.parse(dateString);
                return outputFormat.format(date);
            } catch (ParseException e) {
                return dateString;
            }
        }
    }
}
