package com.halo.redpacket.util

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat

/**
 * @author Zhouxin
 * @date :2021/3/26
 * @description: BigDecimal 工具类
 */
object BigDecimalUtil {
    /**
     * 价格类型 保留小数点 2
     */
    const val PRICE_DECIMAL_POINT = 2

    /**
     * 价格类型 保留小数点 6
     */
    const val SIX_PRICE_DECIMAL_POINT = 6

    /**
     * 重量类型保留小数点 3
     */
    const val WEIGHT_DECIMAL_POINT = 3

    /**
     * BigDecimal 加法 num1 + num2
     * 未做非空校验
     *
     * @param num1
     * @param num2
     * @param point 请使用BigDecimalUtils.PRICE_DECIMAL_POINT | BigDecimalUtils.WEIGHT_DECIMAL_POINT
     * @return BigDecimal
     */
    fun add(num1: BigDecimal, num2: BigDecimal?, point: Int): BigDecimal {
        return setScale(num1.add(num2), point)
    }

    /**
     * BigDecimal 乘法 num1 x num2
     * 未做非空校验
     *
     * @param num1
     * @param num2
     * @param point 请使用BigDecimalUtils.PRICE_DECIMAL_POINT | BigDecimalUtils.WEIGHT_DECIMAL_POINT
     * @return BigDecimal
     */
    fun multiply(num1: BigDecimal, num2: BigDecimal?, point: Int): BigDecimal {
        return setScale(num1.multiply(num2), point)
    }

    /**
     * BigDecimal 减法 num1 - num2
     * 未做非空校验
     *
     * @param num1
     * @param num2
     * @param point 请使用BigDecimalUtils.PRICE_DECIMAL_POINT | BigDecimalUtils.WEIGHT_DECIMAL_POINT
     * @return BigDecimal
     */
    fun subtract(num1: BigDecimal, num2: BigDecimal?, point: Int): BigDecimal {
        return setScale(num1.subtract(num2), point)
    }

    /**
     * BigDecimal 除法 num1/num2
     * 未做非空校验
     *
     * @param num1
     * @param num2
     * @param point 请使用BigDecimalUtils.PRICE_DECIMAL_POINT | BigDecimalUtils.WEIGHT_DECIMAL_POINT
     * @return BigDecimal
     */
    fun divide(num1: BigDecimal, num2: BigDecimal?, point: Int): BigDecimal {
        return num1.divide(num2, point, RoundingMode.HALF_UP)
    }

    /**
     * 设置小数点类型为 四舍五入
     *
     * @param num
     * @param point
     * @return BigDecimal
     */
    fun setScale(num: BigDecimal, point: Int): BigDecimal {
        return num.setScale(point, RoundingMode.HALF_UP)
    }

    /**
     * 比较 num1 是否大于 num2
     *
     * @param num1
     * @param num2
     * @return boolean
     */
    fun isGreaterThan(num1: BigDecimal, num2: BigDecimal?): Boolean {
        return num1.compareTo(num2) == 1
    }

    /**
     * 比较 num1 是否大于等于 num2
     *
     * @param num1
     * @param num2
     * @return boolean
     */
    fun isGreaterOrEqual(num1: BigDecimal, num2: BigDecimal?): Boolean {
        return isGreaterThan(num1, num2) || equals(num1, num2)
    }

    /**
     * 比较 num1 是否小于 num2
     *
     * @param num1
     * @param num2
     * @return boolean
     */
    fun isLessThan(num1: BigDecimal, num2: BigDecimal?): Boolean {
        return num1.compareTo(num2) == -1
    }

    /**
     * 比较 num1 是否小于等于 num2
     *
     * @param num1
     * @param num2
     * @return boolean
     */
    fun isLessOrEqual(num1: BigDecimal, num2: BigDecimal?): Boolean {
        return isLessThan(num1, num2) || equals(num1, num2)
    }

    /**
     * 比较 num1 是否等于 num2
     *
     * @param num1
     * @param num2
     * @return
     */
    fun equals(num1: BigDecimal, num2: BigDecimal?): Boolean {
        return num1.compareTo(num2) == 0
    }

    /**
     * 计算 num1 / num2 的百分比
     *
     * @param num1
     * @param num2
     * @return String
     */
    fun getPercentage(num1: BigDecimal, num2: BigDecimal?): String {
        val result: BigDecimal = num1.divide(num2, 4, RoundingMode.HALF_UP)
        val percent: NumberFormat = NumberFormat.getPercentInstance()
        percent.setMaximumFractionDigits(2)
        return percent.format(result.toDouble())
    }

    /**
     * 计算 num1 / num2 的百分比
     *
     * @param num1
     * @param num2
     * @param point 保留几位小数
     * @return String
     */
    fun bigDecimalPercent(num1: Int?, num2: Int?, point: Int): BigDecimal {
        if (num1 == null || num2 == null) {
            return BigDecimal.ZERO
        }
        if (num2 == Integer.valueOf(0)) {
            return BigDecimal.ZERO
        }
        val bigDecimalNum1 = BigDecimal(num1)
        val bigDecimalNum2 = BigDecimal(num2)
        return bigDecimalPercent(bigDecimalNum1, bigDecimalNum2, point)
    }

    /**
     * 计算 num1 / num2 的百分比
     *
     * @param num1
     * @param num2
     * @param point 保留几位小数
     * @return String
     */
    fun bigDecimalPercent(num1: BigDecimal?, num2: BigDecimal?, point: Int): BigDecimal {
        if (num1 == null || num2 == null) {
            return BigDecimal.ZERO
        }
        if (equals(BigDecimal.ZERO, num2)) {
            return BigDecimal.ZERO
        }
        val percent: BigDecimal = num1.divide(num2, point + 2, RoundingMode.HALF_UP)
        return percent.multiply(BigDecimal(100)).setScale(point)
    }

    /**
     * 判断num是否为空 或者 零
     *
     * @param num
     * @return String
     */
    fun isEmpty(num: BigDecimal?): Boolean {
        return null == num || equals(BigDecimal.ZERO, num)
    }

    /**
     * 判断num是否 不等于null 并且不等于零
     *
     * @param num
     * @return String
     */
    fun isNotEmpty(num: BigDecimal?): Boolean {
        return !isEmpty(num)
    }

    /**
     * 转换为万
     *
     * @param num
     * @param point
     * @return
     */
    fun convertTenThousand(num: BigDecimal, point: Int): BigDecimal {
        return num.divide(BigDecimal(10000), point, RoundingMode.HALF_UP)
    }

    /**
     * 转换为负数
     *
     * @param num
     * @return
     */
    fun convertToMinusNumber(num: BigDecimal): BigDecimal {
        return if (isLessOrEqual(num, BigDecimal.ZERO)) {
            num
        } else BigDecimal.ZERO.subtract(num)
    }

    /**
     * 金额相关计算方法：四舍五入 保留2位小数点
     */
    object Amount {
        fun add(num1: BigDecimal, num2: BigDecimal?): BigDecimal {
            return setScale(num1.add(num2), PRICE_DECIMAL_POINT)
        }

        fun multiply(num1: BigDecimal, num2: BigDecimal?): BigDecimal {
            return setScale(num1.multiply(num2), PRICE_DECIMAL_POINT)
        }

        fun subtract(num1: BigDecimal, num2: BigDecimal?): BigDecimal {
            return setScale(num1.subtract(num2), PRICE_DECIMAL_POINT)
        }

        fun divide(num1: BigDecimal, num2: BigDecimal?): BigDecimal {
            return setScale(num1.divide(num2, RoundingMode.HALF_UP), PRICE_DECIMAL_POINT)
        }
    }

    /**
     * 金额相关计算方法：四舍五入 保留2位小数点
     */
    object AmountSix {
        fun add(num1: BigDecimal, num2: BigDecimal?): BigDecimal {
            return setScale(num1.add(num2), SIX_PRICE_DECIMAL_POINT)
        }

        fun multiply(num1: BigDecimal, num2: BigDecimal?): BigDecimal {
            return setScale(num1.multiply(num2), SIX_PRICE_DECIMAL_POINT)
        }

        fun subtract(num1: BigDecimal, num2: BigDecimal?): BigDecimal {
            return setScale(num1.subtract(num2), SIX_PRICE_DECIMAL_POINT)
        }

        fun divide(num1: BigDecimal, num2: BigDecimal?): BigDecimal {
            return num1.divide(num2, PRICE_DECIMAL_POINT, RoundingMode.HALF_UP)
        }
    }

    /**
     * 重量相关计算方法：四舍五入 保留3位小数点
     */
    object Weight {
        fun add(num1: BigDecimal, num2: BigDecimal?): BigDecimal {
            return setScale(num1.add(num2), WEIGHT_DECIMAL_POINT)
        }

        fun multiply(num1: BigDecimal, num2: BigDecimal?): BigDecimal {
            return setScale(num1.multiply(num2), WEIGHT_DECIMAL_POINT)
        }

        fun subtract(num1: BigDecimal, num2: BigDecimal?): BigDecimal {
            return setScale(num1.subtract(num2), WEIGHT_DECIMAL_POINT)
        }

        fun divide(num1: BigDecimal, num2: BigDecimal?): BigDecimal {
            return num1.divide(num2, WEIGHT_DECIMAL_POINT, RoundingMode.HALF_UP)
        }
    }
}