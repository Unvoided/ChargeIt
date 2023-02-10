package com.unvoided.chargeit.data

import com.google.gson.annotations.SerializedName

data class Station(
    @SerializedName("ID") var id: Int? = null,
    @SerializedName("OperatorInfo") var operatorInfo: OperatorInfo? = null,
    @SerializedName("StatusType") var statusType: StatusType? = null,
    @SerializedName("UsageCost") var usageCost: String? = null,
    @SerializedName("AddressInfo") var addressInfo: AddressInfo? = null,
    @SerializedName("Connections") val connections: ArrayList<Connections>? = null,
    @SerializedName("NumberOfPoints") var numberOfPoints: Int? = null,
)

data class OperatorInfo(
    @SerializedName("PhonePrimaryContact") val phonePrimaryContact: String? = null,
    @SerializedName("ContactEmail") var contactEmail: String? = null,
    @SerializedName("Title") var title: String? = null
)

data class StatusType(
    @SerializedName("IsOperational") val isOperational: Boolean? = null,
)

data class AddressInfo(
    @SerializedName("Title") var title: String? = null,
    @SerializedName("Town") var town: String? = null,
    @SerializedName("Latitude") var latitude: Double? = null,
    @SerializedName("Longitude") var longitude: Double? = null,
    @SerializedName("Distance") var distance: Double? = null,
    @SerializedName("DistanceUnit") var distanceUnit: Int? = null
)

data class ConnectionType(
    @SerializedName("FormalName") var formalName: String? = null,
    @SerializedName("Title") var title: String? = null
)

data class Connections(
    @SerializedName("ID") var id: Int? = null,
    @SerializedName("ConnectionType") var connectionType: ConnectionType? = null,
    @SerializedName("StatusType") var statusType: StatusType? = null,
    @SerializedName("Amps") var amps: Int? = null,
    @SerializedName("Voltage") var voltage: Int? = null,
    @SerializedName("PowerKW") var powerKw: Double? = null,
    @SerializedName("Quantity") var quantity: Int? = null
)