package com.bics.expense.doctormodule.fragment.prescription



data class AddPrecriptionRequests(
    val medicineId: String,
    val medicineName: String,
    val dosage: String,
    val duration: Int,
    val repeat: String,
    val morning: Boolean,
    val afterNoon: Boolean,
    val evening: Boolean,
    val beforeFood: Boolean,
    val afterFood: Boolean,
    val appointmentID: String
)