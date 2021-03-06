package com.corrot.room.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.corrot.room.db.entity.Routine;
import com.corrot.room.interfaces.RoutineCallback;
import com.corrot.room.repository.RoutinesRepository;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class RoutineViewModel extends AndroidViewModel {

    private RoutinesRepository mRoutinesRepository;
    private LiveData<List<Routine>> mAllRoutines;

    public RoutineViewModel(Application application) {
        super(application);
        mRoutinesRepository = new RoutinesRepository(application);
        mAllRoutines = mRoutinesRepository.getAllRoutines();
    }

    public LiveData<List<Routine>> getAllRoutines() {
        if (mRoutinesRepository == null) {
            mAllRoutines = mRoutinesRepository.getAllRoutines();
        }
        return mAllRoutines;
    }

    public Routine getRoutineByName(String name)
            throws ExecutionException, InterruptedException {
        return mRoutinesRepository.getRoutineByName(name);
    }

    public void insertSingleRoutine(Routine routine) {
        mRoutinesRepository.insertSingleRoutine(routine);
    }

    public void updateRoutine(Routine routine) {
        mRoutinesRepository.updateRoutine(routine);
    }

    public void deleteRoutine(Routine routine) {
        mRoutinesRepository.deleteRoutine(routine);
    }

    /*public void deleteAll() {
        mRoutinesRepository.deleteAll();
    }*/
}
