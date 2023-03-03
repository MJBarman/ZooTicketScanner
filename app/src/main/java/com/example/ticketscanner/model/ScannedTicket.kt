package com.example.ticketscanner.model

data class ScannedTicket(
    val visitor_name: String,
    val booking_no: String,
    val mobile_no: String,
    val total_person: Int,
    val total_camera: Int,
    val net_amt: Double,
    val service_amt: Double,
    val total_amt: Double
)