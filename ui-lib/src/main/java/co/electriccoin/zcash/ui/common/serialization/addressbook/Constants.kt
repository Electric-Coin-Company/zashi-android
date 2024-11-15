package co.electriccoin.zcash.ui.common.serialization.addressbook

import java.nio.ByteOrder

internal const val ADDRESS_BOOK_SERIALIZATION_V1 = 1
internal const val ADDRESS_BOOK_ENCRYPTION_V1 = 1
internal const val ADDRESS_BOOK_ENCRYPTION_KEY_SIZE = 32
internal const val ADDRESS_BOOK_FILE_IDENTIFIER_SIZE = 32
internal const val ADDRESS_BOOK_SALT_SIZE = 32
internal val ADDRESS_BOOK_BYTE_ORDER = ByteOrder.BIG_ENDIAN
