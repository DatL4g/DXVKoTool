package dev.datlag.dxvkotool.other

import java.nio.ByteOrder

object DXVK {
    const val MAGIC = "DXVK"
    const val HASH_SIZE = 20
    const val LEGACY_VERSION = 7

    private const val EMPTY_HEADER_BYTE_VALUE_1 = 218
    private const val EMPTY_HEADER_BYTE_POS_1 = 0
    private const val EMPTY_HEADER_BYTE_VALUE_2 = 57
    private const val EMPTY_HEADER_BYTE_POS_2 = 1
    private const val EMPTY_HEADER_BYTE_VALUE_3 = 163
    private const val EMPTY_HEADER_BYTE_POS_3 = 2
    private const val EMPTY_HEADER_BYTE_VALUE_4 = 238
    private const val EMPTY_HEADER_BYTE_POS_4 = 3
    private const val EMPTY_HEADER_BYTE_VALUE_5 = 94
    private const val EMPTY_HEADER_BYTE_POS_5 = 4

    private const val EMPTY_HEADER_BYTE_VALUE_6 = 107
    private const val EMPTY_HEADER_BYTE_POS_6 = 5
    private const val EMPTY_HEADER_BYTE_VALUE_7 = 75
    private const val EMPTY_HEADER_BYTE_POS_7 = 6
    private const val EMPTY_HEADER_BYTE_VALUE_8 = 13
    private const val EMPTY_HEADER_BYTE_POS_8 = 7
    private const val EMPTY_HEADER_BYTE_VALUE_9 = 50
    private const val EMPTY_HEADER_BYTE_POS_9 = 8
    private const val EMPTY_HEADER_BYTE_VALUE_10 = 85
    private const val EMPTY_HEADER_BYTE_POS_10 = 9

    private const val EMPTY_HEADER_BYTE_VALUE_11 = 191
    private const val EMPTY_HEADER_BYTE_POS_11 = 10
    private const val EMPTY_HEADER_BYTE_VALUE_12 = 239
    private const val EMPTY_HEADER_BYTE_POS_12 = 11
    private const val EMPTY_HEADER_BYTE_VALUE_13 = 149
    private const val EMPTY_HEADER_BYTE_POS_13 = 12
    private const val EMPTY_HEADER_BYTE_VALUE_14 = 96
    private const val EMPTY_HEADER_BYTE_POS_14 = 13
    private const val EMPTY_HEADER_BYTE_VALUE_15 = 24
    private const val EMPTY_HEADER_BYTE_POS_15 = 14

    private const val EMPTY_HEADER_BYTE_VALUE_16 = 144
    private const val EMPTY_HEADER_BYTE_POS_16 = 15
    private const val EMPTY_HEADER_BYTE_VALUE_17 = 175
    private const val EMPTY_HEADER_BYTE_POS_17 = 16
    private const val EMPTY_HEADER_BYTE_VALUE_18 = 216
    private const val EMPTY_HEADER_BYTE_POS_18 = 17
    private const val EMPTY_HEADER_BYTE_VALUE_19 = 7
    private const val EMPTY_HEADER_BYTE_POS_19 = 18
    private const val EMPTY_HEADER_BYTE_VALUE_20 = 9
    private const val EMPTY_HEADER_BYTE_POS_20 = 19

    val ENDIAN = ByteOrder.LITTLE_ENDIAN
    val SHA1_EMPTY = run {
        val bytes = ByteArray(20)
        bytes[EMPTY_HEADER_BYTE_POS_1] = (EMPTY_HEADER_BYTE_VALUE_1).toByte()
        bytes[EMPTY_HEADER_BYTE_POS_2] = (EMPTY_HEADER_BYTE_VALUE_2).toByte()
        bytes[EMPTY_HEADER_BYTE_POS_3] = (EMPTY_HEADER_BYTE_VALUE_3).toByte()
        bytes[EMPTY_HEADER_BYTE_POS_4] = (EMPTY_HEADER_BYTE_VALUE_4).toByte()
        bytes[EMPTY_HEADER_BYTE_POS_5] = (EMPTY_HEADER_BYTE_VALUE_5).toByte()

        bytes[EMPTY_HEADER_BYTE_POS_6] = (EMPTY_HEADER_BYTE_VALUE_6).toByte()
        bytes[EMPTY_HEADER_BYTE_POS_7] = (EMPTY_HEADER_BYTE_VALUE_7).toByte()
        bytes[EMPTY_HEADER_BYTE_POS_8] = (EMPTY_HEADER_BYTE_VALUE_8).toByte()
        bytes[EMPTY_HEADER_BYTE_POS_9] = (EMPTY_HEADER_BYTE_VALUE_9).toByte()
        bytes[EMPTY_HEADER_BYTE_POS_10] = (EMPTY_HEADER_BYTE_VALUE_10).toByte()

        bytes[EMPTY_HEADER_BYTE_POS_11] = (EMPTY_HEADER_BYTE_VALUE_11).toByte()
        bytes[EMPTY_HEADER_BYTE_POS_12] = (EMPTY_HEADER_BYTE_VALUE_12).toByte()
        bytes[EMPTY_HEADER_BYTE_POS_13] = (EMPTY_HEADER_BYTE_VALUE_13).toByte()
        bytes[EMPTY_HEADER_BYTE_POS_14] = (EMPTY_HEADER_BYTE_VALUE_14).toByte()
        bytes[EMPTY_HEADER_BYTE_POS_15] = (EMPTY_HEADER_BYTE_VALUE_15).toByte()

        bytes[EMPTY_HEADER_BYTE_POS_16] = (EMPTY_HEADER_BYTE_VALUE_16).toByte()
        bytes[EMPTY_HEADER_BYTE_POS_17] = (EMPTY_HEADER_BYTE_VALUE_17).toByte()
        bytes[EMPTY_HEADER_BYTE_POS_18] = (EMPTY_HEADER_BYTE_VALUE_18).toByte()
        bytes[EMPTY_HEADER_BYTE_POS_19] = (EMPTY_HEADER_BYTE_VALUE_19).toByte()
        bytes[EMPTY_HEADER_BYTE_POS_20] = (EMPTY_HEADER_BYTE_VALUE_20).toByte()

        bytes
    }
}
