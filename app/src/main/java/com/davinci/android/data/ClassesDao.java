package com.davinci.android.data;

import com.davinci.android.model.Classes;
import com.davinci.android.model.Element;
import com.davinci.android.model.Links;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import io.reactivex.Completable;
import io.reactivex.Observable;

@Dao
public abstract class ClassesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract void insert(Classes classes);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract void insert(List<Links> links);

    public Observable<Element> insert(Element item) {
        return Completable.create(task -> {
            insert(item.getItem());
            insert(item.getLinks());
            task.onComplete();
        }).toObservable();
    }

    @Query("DELETE FROM classes")
    public abstract void deleteClasses();

    @Query("DELETE FROM links")
    public abstract void deleteLinks();

    @Query("SELECT * FROM classes")
    public abstract Observable<List<Element>> getAll();

    @Query("SELECT * FROM classes WHERE id = (:id)")
    public abstract Observable<Element> getElement(int id);
}