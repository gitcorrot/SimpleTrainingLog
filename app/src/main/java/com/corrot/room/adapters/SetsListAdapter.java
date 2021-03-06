package com.corrot.room.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.corrot.room.ExerciseSetItem;
import com.corrot.room.R;
import com.corrot.room.utils.PreferencesManager;
import com.corrot.room.viewmodel.NewWorkoutViewModel;

import java.util.List;

public class SetsListAdapter extends RecyclerView.Adapter<SetsListAdapter.ExerciseSetViewHolder> {

    private static int REPS_EDIT_TEXT = 0;
    private static int WEIGHT_EDIT_TEXT = 1;

    class ExerciseSetViewHolder extends RecyclerView.ViewHolder {
        private final TextView setTextView;
        private final EditText repsEditText;
        private final EditText weightEditText;
        private final TextView unitTextView;

        private MyEditTextOnFocusChangeListener repsFocusListener;
        private MyEditTextOnFocusChangeListener weightsFocusListener;

        private ExerciseSetViewHolder(View itemView) {
            super(itemView);

            setTextView = itemView.findViewById(R.id.new_set_text_view);
            repsEditText = itemView.findViewById(R.id.new_set_reps_edit_text);
            weightEditText = itemView.findViewById(R.id.new_set_weight_edit_text);
            unitTextView = itemView.findViewById(R.id.new_set_unit);

            repsFocusListener = new MyEditTextOnFocusChangeListener(REPS_EDIT_TEXT);
            weightsFocusListener = new MyEditTextOnFocusChangeListener(WEIGHT_EDIT_TEXT);

            repsEditText.setOnFocusChangeListener(repsFocusListener);
            weightEditText.setOnFocusChangeListener(weightsFocusListener);

            unitTextView.setText(PreferencesManager.getInstance().getUnitSystem());
        }
    }

    private final LayoutInflater mInflater;
    private List<ExerciseSetItem> mSets;
    private NewWorkoutViewModel mNewWorkoutViewModel;

    SetsListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);

        mNewWorkoutViewModel =
                ViewModelProviders.of((AppCompatActivity) context).get(NewWorkoutViewModel.class);
        mNewWorkoutViewModel.init();
    }

    @NonNull
    @Override
    public ExerciseSetViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, final int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_set_item, viewGroup, false);
        return new ExerciseSetViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ExerciseSetViewHolder viewHolder, int position) {

        viewHolder.repsFocusListener.updatePosition(viewHolder.getAdapterPosition());
        viewHolder.weightsFocusListener.updatePosition(viewHolder.getAdapterPosition());

        if (mSets != null) {
            ExerciseSetItem item = mSets.get(viewHolder.getAdapterPosition());
            viewHolder.setTextView.setText(String.valueOf(viewHolder.getAdapterPosition() + 1));
            if (item.weight != 0) viewHolder.weightEditText.setText(String.valueOf(item.weight));
            if (item.reps != 0) viewHolder.repsEditText.setText(String.valueOf(item.reps));
        }
    }

    @Override
    public int getItemCount() {
        return mSets != null ? mSets.size() : 0;
    }

    public void setSets(List<ExerciseSetItem> sets) {
        mSets = sets;
    }

    private class MyEditTextOnFocusChangeListener implements View.OnFocusChangeListener {

        private final int type;
        private int position;
        private int reps;
        private float weight;

        private MyEditTextOnFocusChangeListener(int type) {
            this.type = type;
        }

        private void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                ExerciseSetItem item = mSets.get(position);
                EditText editText = (EditText) v;

                if ((type == SetsListAdapter.REPS_EDIT_TEXT)) {
                    String repsString = editText.getText().toString();
                    if (TextUtils.isEmpty(repsString))
                        this.reps = 0;
                    else {
                        try {
                            this.reps = Integer.parseInt(repsString);
                        } catch (NumberFormatException e) {
                            Log.e("SetsAdapter", e.getLocalizedMessage());
                        }
                    }

                    if (item.reps != reps) {
                        item.reps = reps;
                        mNewWorkoutViewModel.updateSet(item, position);
                    }
                } else if (type == SetsListAdapter.WEIGHT_EDIT_TEXT) {
                    String weightString = editText.getText().toString();
                    if (TextUtils.isEmpty(weightString))
                        this.weight = 0;
                    else {
                        try {
                            this.weight = Float.parseFloat(weightString);
                        } catch (NumberFormatException e) {
                            Log.e("SetsAdapter", e.getLocalizedMessage());
                        }
                    }
                    if (item.weight != weight) {
                        item.weight = weight;
                        mNewWorkoutViewModel.updateSet(item, position);
                    }
                }
            }
        }
    }
}


