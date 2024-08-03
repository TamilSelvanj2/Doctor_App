package com.bics.expense.doctormodule.fragment.patientDetail




data class PatientModel(

    var patientID: String,
    var userID: String,
    var quickbloxId: String,
    var firstName:String,
    var lastName: String,
    var name: String,
    var email: String,
    var phone: String,
    var dateOfBirth: String,
    var age: String,
    var gender: String,
    var createdBy: String,
    var dateCreated: String
)