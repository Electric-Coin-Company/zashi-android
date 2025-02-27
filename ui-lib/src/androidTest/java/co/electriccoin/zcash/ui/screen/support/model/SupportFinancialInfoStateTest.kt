package co.electriccoin.zcash.ui.screen.support.model

import co.electriccoin.zcash.configuration.AndroidConfigurationFactory
import co.electriccoin.zcash.ui.test.getAppContext
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SupportFinancialInfoStateTest {
    @Test
    fun filter_time() =
        runTest {
            val supportInfo = SupportInfo.new(getAppContext(), AndroidConfigurationFactory.new())

            val individualExpected = supportInfo.timeInfo.toSupportString()

            val actualIncluded = supportInfo.toSupportString(setOf(SupportInfoType.Time))
            assertTrue(actualIncluded.contains(individualExpected))

            val actualExcluded = supportInfo.toSupportString(emptySet())
            assertFalse(actualExcluded.contains(individualExpected))
        }

    @Test
    fun filter_app() =
        runTest {
            val supportInfo = SupportInfo.new(getAppContext(), AndroidConfigurationFactory.new())

            val individualExpected = supportInfo.appInfo.toSupportString()

            val actualIncluded = supportInfo.toSupportString(setOf(SupportInfoType.App))
            assertTrue(actualIncluded.contains(individualExpected))

            val actualExcluded = supportInfo.toSupportString(emptySet())
            assertFalse(actualExcluded.contains(individualExpected))
        }

    @Test
    fun filter_os() =
        runTest {
            val supportInfo = SupportInfo.new(getAppContext(), AndroidConfigurationFactory.new())

            val individualExpected = supportInfo.operatingSystemInfo.toSupportString()

            val actualIncluded = supportInfo.toSupportString(setOf(SupportInfoType.Os))
            assertTrue(actualIncluded.contains(individualExpected))

            val actualExcluded = supportInfo.toSupportString(emptySet())
            assertFalse(actualExcluded.contains(individualExpected))
        }

    @Test
    fun filter_device() =
        runTest {
            val supportInfo = SupportInfo.new(getAppContext(), AndroidConfigurationFactory.new())

            val individualExpected = supportInfo.deviceInfo.toSupportString()

            val actualIncluded = supportInfo.toSupportString(setOf(SupportInfoType.Device))
            assertTrue(actualIncluded.contains(individualExpected))

            val actualExcluded = supportInfo.toSupportString(emptySet())
            assertFalse(actualExcluded.contains(individualExpected))
        }

    @Test
    fun filter_crash() =
        runTest {
            val supportInfo = SupportInfo.new(getAppContext(), AndroidConfigurationFactory.new())

            val individualExpected = supportInfo.crashInfo.toCrashSupportString()

            val actualIncluded = supportInfo.toSupportString(setOf(SupportInfoType.Crash))
            assertTrue(actualIncluded.contains(individualExpected))
        }

    @Test
    fun filter_environment() =
        runTest {
            val supportInfo = SupportInfo.new(getAppContext(), AndroidConfigurationFactory.new())

            val individualExpected = supportInfo.environmentInfo.toSupportString()

            val actualIncluded = supportInfo.toSupportString(setOf(SupportInfoType.Environment))
            assertTrue(actualIncluded.contains(individualExpected))

            val actualExcluded = supportInfo.toSupportString(emptySet())
            assertFalse(actualExcluded.contains(individualExpected))
        }

    @Test
    fun filter_permission() =
        runTest {
            val supportInfo = SupportInfo.new(getAppContext(), AndroidConfigurationFactory.new())

            val individualExpected = supportInfo.permissionInfo.toPermissionSupportString()

            val actualIncluded = supportInfo.toSupportString(setOf(SupportInfoType.Permission))
            assertTrue(actualIncluded.contains(individualExpected))

            val actualExcluded = supportInfo.toSupportString(emptySet())
            assertFalse(actualExcluded.contains(individualExpected))
        }
}
