package com.bics.expense.doctormodule.fragment.prescription


class PrescriptionRequest {

    var success: Boolean? = null
    var error: String? = null
    var data: List<PrescriptionResponse>? = null
    var statusCode: Int? = null
}