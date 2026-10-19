package com.dearyoti.lexiladder.data.content.model

data class UiCatalogPackage(
    val schemaVersion: Int = 1,
    val packageId: String,
    val locale: String,
    val contentVersion: Int,
    val strings: Map<String, String> = emptyMap()
)
