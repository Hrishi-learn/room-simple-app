package com.hrishi.roomdemo

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EmployeeDao{
    @Insert
    suspend fun insert(employeeEntity: EmployeeEntity)
    @Delete
    suspend fun delete(employeeEntity: EmployeeEntity)
    @Update
    suspend fun update(employeeEntity: EmployeeEntity)
    @Query("SELECT*FROM `employee-table`")
    fun fetchAllEmployees():Flow<List<EmployeeEntity>>
    @Query("SELECT*FROM `employee-table` where id=:id")
    fun fetchEmployeeById(id:Int):Flow<EmployeeEntity>
}