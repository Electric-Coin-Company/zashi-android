package cash.z.ecc.sdk.fixture

import cash.z.ecc.android.sdk.type.WalletBirthday

object WalletBirthdayFixture {

    const val HEIGHT = 1500000
    const val HASH = "00047a34c61409682f44640af9352023ad92f69b827d0f2b288f152ebea50f46"
    const val EPOCH_SECONDS = 1627076501L

    @Suppress("MaxLineLength")
    const val TREE = "01172b95f271c6af8f68388f08c8ef970db8ec8d8d61204ecb7b2bb2c38262b92d0010016284585a6c85dadfef27ff33f1403926b4bb391de92e8be797e4280cc4ca2971000001a1ff388639379c0120782b3929bd8871af797be4b651f694aa961bad65a9c12400000001d806c98bda9653d5ae22757eed750871e16e0fb657f52c3d771a4411668e84330001260f6e9fac0922f98d58afbcc3f391ac19d5d944081466929a33b99df19c0e6a0000013d2fd009bf8a22d68f720eac19c411c99014ed9c5f85d5942e15d1fc039e28680001f08f39275112dd8905b854170b7f247cf2df18454d4fa94e6e4f9320cca05f24011f8322ef806eb2430dc4a7a41c1b344bea5be946efc7b4349c1c9edb14ff9d39"

    fun new(
        height: Int = HEIGHT,
        hash: String = HASH,
        time: Long = EPOCH_SECONDS,
        tree: String = TREE
    ) = WalletBirthday(height = height, hash = hash, time = time, tree = tree)
}
