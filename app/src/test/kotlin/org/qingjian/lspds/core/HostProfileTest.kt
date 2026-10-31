package org.qingjian.lspds.core

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HostProfileTest {
    @Test
    fun onlyKnownStaticBaselineIsAccepted() {
        assertTrue(HostProfile.inspect("/unused", 288L, "2.7.1").isKnownStaticBaseline())
        assertFalse(HostProfile.inspect("/unused", 287L, "2.7.0").isKnownStaticBaseline())
        assertFalse(HostProfile.inspect("/unused", 999L, "future").isKnownStaticBaseline())
    }

    @Test
    fun missingApkPathIsRepresentedWithoutFailure() {
        assertTrue(HostProfile.inspect(null, 288L, null).apkLength == -1L)
    }
}
