package com.alderferstudios.percentcalculatorv2.activity

data class CalcResults(var subtotal: Double = 0.00, var percent: String = "",
                       var taxAmount: String = "", var total: String = "",
                       var eachTip: String = "", var eachTotal: String = "")
