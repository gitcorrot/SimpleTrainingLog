package com.corrot.room.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.corrot.room.NewRoutineDialog;
import com.corrot.room.RoutineExerciseItem;
import com.corrot.room.R;
import com.corrot.room.db.entity.Routine;
import com.corrot.room.utils.RoutinesDiffUtilCallback;
import com.corrot.room.viewmodel.RoutineViewModel;
import com.corrot.room.viewmodel.NewRoutineViewModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

public class RoutinesAdapter extends RecyclerView.Adapter<RoutinesAdapter.RoutineViewHolder> {

    class RoutineViewHolder extends RecyclerView.ViewHolder {
        private final TextView labelTextView;
        private final TextView exercisesTextView;
        private final LinearLayout subItem;
        private final ImageButton deleteButton;
        private final ImageButton editButton;

        private RoutineViewHolder(View itemView) {
            super(itemView);
            labelTextView = itemView.findViewById(R.id.routine_label);
            exercisesTextView = itemView.findViewById(R.id.routine_exercises);
            subItem = itemView.findViewById(R.id.routine_sub_item);
            deleteButton = itemView.findViewById(R.id.routine_delete_button);
            editButton = itemView.findViewById(R.id.routine_edit_button);
        }
    }

    private final LayoutInflater mInflater;
    private List<Routine> mRoutines;
    private RoutineViewModel mRoutineViewModel;
    private FragmentActivity mActivity;

    public RoutinesAdapter(Context context, FragmentActivity activity) {
        mInflater = LayoutInflater.from(context);
        mActivity = activity;
        mRoutineViewModel = ViewModelProviders.of(mActivity).get(RoutineViewModel.class);
    }

    @NonNull
    @Override
    public RoutineViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = mInflater.inflate(R.layout.recyclerview_routine_item, viewGroup, false);
        final RoutineViewHolder viewHolder = new RoutineViewHolder(itemView);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Routine workout = mRoutines.get(viewHolder.getAdapterPosition());
                workout.expanded = !workout.expanded;
                notifyItemChanged(viewHolder.getAdapterPosition());
            }
        });

        viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Routine workout = mRoutines.get(viewHolder.getAdapterPosition());
                showDeleteDialog(mActivity, workout).show();
            }
        });

        viewHolder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Routine workout = mRoutines.get(viewHolder.getAdapterPosition());
                List<RoutineExerciseItem> exerciseItems = getRoutineExercises(workout);

                NewRoutineViewModel mNewRoutineViewModel =
                        ViewModelProviders.of(mActivity).get(NewRoutineViewModel.class);
                mNewRoutineViewModel.init();
                mNewRoutineViewModel.setExercises(exerciseItems);

                NewRoutineDialog dialog = new NewRoutineDialog();
                Bundle args = new Bundle();
                args.putInt("id", workout.id);
                args.putString("label", workout.label);
                dialog.setArguments(args);
                dialog.show(mActivity.getSupportFragmentManager(), "Edit");
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RoutineViewHolder viewHolder, int i) {
        if (mRoutines != null) {
            final Routine workout = mRoutines.get(i);
            viewHolder.labelTextView.setText(workout.label);

            StringBuilder exercises = new StringBuilder();
            for (String s : workout.exercises) {
                exercises.append(s);
                exercises.append("\n");
            }
            viewHolder.exercisesTextView.setText(exercises.toString());

            viewHolder.subItem.setVisibility(workout.expanded ? View.VISIBLE : View.GONE);
        }
    }

    public void setRoutines(List<Routine> newRoutines) {
        if (this.mRoutines == null) {
            this.mRoutines = new ArrayList<>();
        }
        if (newRoutines != null) {
            RoutinesDiffUtilCallback callback =
                    new RoutinesDiffUtilCallback(this.mRoutines, newRoutines);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(callback);
            this.mRoutines.clear();
            this.mRoutines.addAll(newRoutines);
            diffResult.dispatchUpdatesTo(this);
        }
    }

    @Override
    public int getItemCount() {
        if (mRoutines != null)
            return mRoutines.size();
        else return 0;
    }

    private AlertDialog showDeleteDialog(FragmentActivity fragmentActivity, final Routine workout) {
        return new AlertDialog.Builder(fragmentActivity.getThemedContext())
                .setTitle("Are you sure you want delete this routine?")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mRoutineViewModel.deleteRoutine(workout);
                    }
                })
                .create();
    }

    private List<RoutineExerciseItem> getRoutineExercises(Routine routine) {
        List<RoutineExerciseItem> exerciseItems = new ArrayList<>();

        for (int i = 0; i < routine.exercises.size(); i++) {
            // Parse for example "Squats = 2 sets." into object
            RoutineExerciseItem item = new RoutineExerciseItem();
            try {
                String[] name = routine.exercises.get(i).split(" - ");
                item.name = name[0];
                try {
                    String[] sets = name[1].split(" ");
                    item.sets = Integer.parseInt(sets[0]);
                } catch (ArrayIndexOutOfBoundsException e) {
                    Log.e("RoutinesAdapter", "Can't find ' ' in String!");
                    item.sets = 0;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                Log.e("RoutinesAdapter", "Can't find ' - ' in String!");
                item.name = "Name";
                item.sets = 0;
            }
            item.position = i;
            exerciseItems.add(item);
        }
        return exerciseItems;
    }
}