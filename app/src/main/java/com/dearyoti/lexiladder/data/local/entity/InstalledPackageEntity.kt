package com.dearyoti.lexiladder.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "installed_packages")
data class InstalledPackageEntity(
    @PrimaryKey
    val packageId: String,
    val type: String,
    val locale: String,
    val contentVersion: Int,
    val schemaVersion: Int,
    val installedAt: Long,
    val sha256: String
)
