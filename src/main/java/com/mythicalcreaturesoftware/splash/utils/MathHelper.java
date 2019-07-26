package com.mythicalcreaturesoftware.splash.utils;

public class MathHelper {

    /**
     * Given two quantities, returns the corresponding percentage. Eg: If the total is 100 and the part is 20, it returns 20%
     * @param total
     * @param part
     * @return
     */
    public static double percentageOf (double total, double part) {
        return (part * 100) / total;
    }

    /**
     * Given an amount and a percentage, returns the value of that percentage according to the amount. Eg: If the percentage is 20% and the amount is 100, it returns 20.
     * @param percentage
     * @param amount
     * @return
     */
    public static double percentageValue (double percentage, double amount) {
        return (percentage/100) * amount;
    }
}
