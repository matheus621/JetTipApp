package com.example.jettipapp.util


fun calculateTotalTip(totalBill: Double, tipPorcentage: Int): Double {
    return if (totalBill > 1 && totalBill.toString().isNotEmpty())
        (totalBill * tipPorcentage) / 100 else 0.0

}

fun calculateTotalPerson(totalBill: Double, numberSplit: Int, tipPorcentage: Int): Double {
    val bill = calculateTotalTip(totalBill = totalBill, tipPorcentage = tipPorcentage) + totalBill
    return (bill / numberSplit)
}