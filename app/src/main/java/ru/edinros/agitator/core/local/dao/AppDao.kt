package ru.edinros.agitator.core.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertTask(entity: TaskEntity)
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertTasks(list:List<TaskEntity>)
//
//    @Update
//    suspend fun updateTask(task: TaskEntity)
//
//    @Query("select * from TaskEntity order by assigned_at desc limit 100")
//    fun getTaskListFlow(): Flow<List<TaskEntity>>
//
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertReport(report: ReportEntity)
//
//    @Update
//    suspend fun updateReport(report: ReportEntity)
//
//    @Query("select * from ReportEntity where task_id=:taskId and sent = 0")
//    fun getReportFlowById(taskId:Int): Flow<ReportEntity>
//
//    @Query("select count(*) from ReportEntity where task_id=:taskId and sent = 0")
//    suspend fun getReportCountByTaskId(taskId:Int):Int
//
//    @Query("select * from TaskEntity where id=:taskId")
//    suspend fun getTaskById(taskId: Int):TaskEntity
//
//    @Query("select * from TaskEntity where id=:taskId")
//    fun getFlowTaskById(taskId: Int): Flow<TaskEntity>
//
//    @Query("DELETE FROM TaskEntity")
//    suspend fun deleteAllTasks()
//
//    @Query("select * from TaskEntity order by assigned_at desc limit 100")
//    suspend fun getTasks(): List<TaskEntity>
//
//    @Query("select * from ReportEntity where task_id=:taskId and sent = 0")
//    suspend fun getReportByTaskId(taskId:Int):ReportEntity
//
//    @Query("select * from ReportEntity where task_id in (:taskIds) and sent = 0")
//    suspend fun getReportsByTaskIds(taskIds: List<Int>): List<ReportEntity>


}