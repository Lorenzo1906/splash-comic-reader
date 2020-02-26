package reader.utils

/**
 * Given two quantities, returns the corresponding percentage. Eg: If the total is 100 and the part is 20, it returns 20%
 * @param total
 * @param part
 * @return
 */
fun percentageOf (total: Double, part: Double ): Double {
    return (part * 100) / total
}

/**
 * Given an amount and a percentage, returns the value of that percentage according to the amount. Eg: If the percentage is 20% and the amount is 100, it returns 20.
 * @param percentage
 * @param amount
 * @return
 */
fun percentageValue (percentage: Double, amount: Double): Double {
    return (percentage/100) * amount
}