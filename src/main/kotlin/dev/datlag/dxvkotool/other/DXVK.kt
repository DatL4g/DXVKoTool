package dev.datlag.dxvkotool.other

import java.nio.ByteOrder

object DXVK {
    const val MAGIC = "DXVK"
    const val HASH_SIZE = 20
    const val LEGACY_VERSION = 7

    val ENDIAN = ByteOrder.LITTLE_ENDIAN
    val SHA1_EMPTY = run {
        val bytes = ByteArray(20)
        bytes[0] = (218).toByte()
        bytes[1] = (57).toByte()
        bytes[2] = (163).toByte()
        bytes[3] = (238).toByte()
        bytes[4] = (94).toByte()
        bytes[5] = (107).toByte()

        bytes[6] = (75).toByte()
        bytes[7] = (13).toByte()
        bytes[8] = (50).toByte()
        bytes[9] = (85).toByte()
        bytes[10] = (191).toByte()
        bytes[11] = (239).toByte()

        bytes[12] = (149).toByte()
        bytes[13] = (96).toByte()
        bytes[14] = (24).toByte()
        bytes[15] = (144).toByte()
        bytes[16] = (175).toByte()
        bytes[17] = (216).toByte()

        bytes[18] = (7).toByte()
        bytes[19] = (9).toByte()

        bytes
    }
}
