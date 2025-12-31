package com.emsi.recrutement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CVSelectionAdapter extends RecyclerView.Adapter<CVSelectionAdapter.CVViewHolder> {

    private List<CV> cvList;
    private int selectedPosition = -1;
    private OnCVSelectedListener listener;

    public interface OnCVSelectedListener {
        void onCVSelected(CV cv, int position);
    }

    public CVSelectionAdapter(List<CV> cvList, OnCVSelectedListener listener) {
        this.cvList = cvList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CVViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cv_selection, parent, false);
        return new CVViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CVViewHolder holder, int position) {
        CV cv = cvList.get(position);
        holder.bind(cv, position);
    }

    @Override
    public int getItemCount() {
        return cvList != null ? cvList.size() : 0;
    }

    public CV getSelectedCV() {
        if (selectedPosition >= 0 && selectedPosition < cvList.size()) {
            return cvList.get(selectedPosition);
        }
        return null;
    }

    class CVViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCVName, tvCVFileName;
        private RadioButton rbSelectCV;
        private CardView cardCV;

        public CVViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCVName = itemView.findViewById(R.id.tvCVName);
            tvCVFileName = itemView.findViewById(R.id.tvCVFileName);
            rbSelectCV = itemView.findViewById(R.id.rbSelectCV);
            cardCV = itemView.findViewById(R.id.cardCV);
        }

        public void bind(CV cv, int position) {
            tvCVName.setText(cv.getTitre());
            tvCVFileName.setText(cv.getFileName());
            rbSelectCV.setChecked(position == selectedPosition);

            // Change card background color based on selection
            if (position == selectedPosition) {
                cardCV.setCardBackgroundColor(0xFFC8E6C9); // Light green
            } else {
                cardCV.setCardBackgroundColor(0xFFE3F2FD); // Light blue
            }

            itemView.setOnClickListener(v -> {
                int previousPosition = selectedPosition;
                selectedPosition = getAdapterPosition();
                notifyItemChanged(previousPosition);
                notifyItemChanged(selectedPosition);
                if (listener != null) {
                    listener.onCVSelected(cv, position);
                }
            });

            rbSelectCV.setOnClickListener(v -> {
                int previousPosition = selectedPosition;
                selectedPosition = getAdapterPosition();
                notifyItemChanged(previousPosition);
                notifyItemChanged(selectedPosition);
                if (listener != null) {
                    listener.onCVSelected(cv, position);
                }
            });
        }
    }
}
