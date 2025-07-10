package com.arshalif.cashi.infrastructure.external

import kotlinx.serialization.Serializable

// Firestore-specific serializable data classes
@Serializable
data class FirestoreStringValue(
    val stringValue: String
)

@Serializable
data class FirestoreDoubleValue(
    val doubleValue: Double
)

@Serializable
data class FirestoreDocumentFields(
    val id: FirestoreStringValue,
    val recipientEmail: FirestoreStringValue,
    val amount: FirestoreDoubleValue,
    val currency: FirestoreStringValue,
    val timestamp: FirestoreStringValue
)

@Serializable
data class FirestoreDocument(
    val fields: FirestoreDocumentFields
)

@Serializable
data class FirestoreCreateRequest(
    val fields: FirestoreDocumentFields
)

@Serializable
data class FirestoreListResponse(
    val documents: List<FirestoreDocument> = emptyList()
)