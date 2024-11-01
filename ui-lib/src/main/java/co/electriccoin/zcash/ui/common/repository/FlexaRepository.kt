package co.electriccoin.zcash.ui.common.repository

import android.app.Application
import cash.z.ecc.android.sdk.ext.convertZatoshiToZec
import cash.z.ecc.android.sdk.internal.Twig
import co.electriccoin.zcash.ui.BuildConfig
import com.flexa.core.Flexa
import com.flexa.core.shared.AssetAccount
import com.flexa.core.shared.AvailableAsset
import com.flexa.core.shared.CustodyModel
import com.flexa.core.shared.FlexaClientConfiguration
import com.flexa.core.theme.FlexaTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.UUID

interface FlexaRepository {
    fun init()
}

class FlexaRepositoryImpl(
    private val balanceRepository: BalanceRepository,
    private val application: Application,
) : FlexaRepository {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val publishableKey: String?
        get() = BuildConfig.ZCASH_FLEXA_KEY.takeIf { it.isNotEmpty() }

    override fun init() {
        scope.launch {
            val configuration = getFlexaClientConfiguration()
            if (configuration != null) {
                Flexa.init(configuration)
                Twig.info { "Flexa initialized" }

                balanceRepository.state
                    .map { it.spendableBalance }
                    .collect {
                        Flexa.updateAssetAccounts(
                            arrayListOf(
                                createFlexaAccount(
                                    zecBalance = it.convertZatoshiToZec().toDouble()
                                )
                            )
                        )

                        Twig.info { "Flexa updated by ${it.convertZatoshiToZec().toDouble()}" }
                    }
            }
        }
    }

    /**
     * @return an instance of [FlexaClientConfiguration] or null if no publishable key set up
     */
    private fun getFlexaClientConfiguration() =
        publishableKey?.let { publishableKey ->
            FlexaClientConfiguration(
                context = application,
                publishableKey = publishableKey,
                theme =
                    FlexaTheme(
                        useDynamicColorScheme = true,
                    ),
                assetAccounts = arrayListOf(createFlexaAccount(DEFAULT_ZEC_BALANCE)),
                webViewThemeConfig =
                    "{\n" +
                        "    \"android\": {\n" +
                        "        \"light\": {\n" +
                        "            \"backgroundColor\": \"#100e29\",\n" +
                        "            \"sortTextColor\": \"#ed7f60\",\n" +
                        "            \"titleColor\": \"#ffffff\",\n" +
                        "            \"cardColor\": \"#2a254e\",\n" +
                        "            \"borderRadius\": \"15px\",\n" +
                        "            \"textColor\": \"#ffffff\"\n" +
                        "        },\n" +
                        "        \"dark\": {\n" +
                        "            \"backgroundColor\": \"#100e29\",\n" +
                        "            \"sortTextColor\": \"#ed7f60\",\n" +
                        "            \"titleColor\": \"#ffffff\",\n" +
                        "            \"cardColor\": \"#2a254e\",\n" +
                        "            \"borderRadius\": \"15px\",\n" +
                        "            \"textColor\": \"#ffffff\"\n" +
                        "        }\n" +
                        "    }\n" +
                        "}"
            )
        }

    private fun createFlexaAccount(zecBalance: Double) =
        AssetAccount(
            displayName = "",
            icon = "https://flexa.network/static/4bbb1733b3ef41240ca0f0675502c4f7/d8419/flexa-logo%403x.png",
            availableAssets =
                listOf(
                    AvailableAsset(
                        assetId = "bip122:00040fe8ec8471911baa1db1266ea15d/slip44:133",
                        balance = zecBalance,
                        symbol = "ZEC",
                    )
                ),
            custodyModel = CustodyModel.LOCAL,
            assetAccountHash = UUID.randomUUID().toString().toSha256()
        )

    private fun String.toSha256() =
        MessageDigest.getInstance("SHA-256")
            .digest(toByteArray())
            .fold("") { str, value -> str + "%02x".format(value) }
}

private const val DEFAULT_ZEC_BALANCE = .0
