package co.electriccoin.zcash.ui.common.serialization.addressbook

import java.io.InputStream
import java.nio.ByteBuffer

internal abstract class BaseAddressBookSerializer {
    protected fun Int.createByteArray(): ByteArray {
        return this.toLong().createByteArray()
    }

    protected fun Long.createByteArray(): ByteArray {
        return ByteBuffer.allocate(Long.SIZE_BYTES).order(ADDRESS_BOOK_BYTE_ORDER).putLong(this).array()
    }

    protected fun String.createByteArray(): ByteArray {
        val byteArray = this.toByteArray()
        return byteArray.size.createByteArray() + byteArray
    }

    protected fun InputStream.readInt(): Int {
        return readLong().toInt()
    }

    protected fun InputStream.readLong(): Long {
        val buffer = ByteArray(Long.SIZE_BYTES)
        require(this.read(buffer) == buffer.size)
        return ByteBuffer.wrap(buffer).order(ADDRESS_BOOK_BYTE_ORDER).getLong()
    }

    protected fun InputStream.readString(): String {
        val size = this.readInt()
        val buffer = ByteArray(size)
        require(this.read(buffer) == buffer.size)
        return String(buffer)
    }
}
