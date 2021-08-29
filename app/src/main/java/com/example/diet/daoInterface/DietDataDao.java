package com.example.diet.daoInterface;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.diet.entity.DietDataList;

import java.util.List;

@Dao
public interface DietDataDao {
    @Insert
    public void addData(DietDataList dietDataList);

    @Query("select * from DietDataList")
    public List<DietDataList> getALLDietData();

    @Delete
    public void delete(DietDataList dietDataList);

    @Query("DELETE FROM dietdatalist")
    void delete();

    @Query("SELECT * FROM DietDataList WHERE dietID= :dietID ")
    public DietDataList getDietById(int dietID);

    @Query("UPDATE DietDataList SET receiveCount=:receiveCount WHERE dietID = :id")
    void updateCount(String receiveCount, int id);

  /*  @Query("UPDATE VideoDataList SET historyDate=:historyDate WHERE trackId = :trackId")
    void update(String historyDate, Long trackId);


    @Query("SELECT count(*)!=0 FROM videodatalist WHERE trackId = :trackId ")
    boolean containsPrimaryKey(Long trackId);*/


    /*check for empty database or not*/
    @Query("SELECT * FROM DietDataList LIMIT 1")
    DietDataList getDietDto();

}
