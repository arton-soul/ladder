package com.dearyoti.lexiladder.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dearyoti.lexiladder.data.local.entity.InstalledPackageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InstalledPackageDao {

    @Query("SELECT * FROM installed_packages ORDER BY installedAt DESC")
    fun getAllInstalledPackages(): Flow<List<InstalledPackageEntity>>

    @Query("SELECT * FROM installed_packages WHERE packageId = :packageId LIMIT 1")
    suspend fun getInstalledPackage(packageId: String): InstalledPackageEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePackage(pkg: InstalledPackageEntity)

    @Query("DELETE FROM installed_packages WHERE packageId = :packageId")
    suspend fun deletePackage(packageId: String)
}
