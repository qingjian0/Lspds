package org.qingjian.lspds.core;
import org.junit.Test;
import static org.junit.Assert.*;
public class HostProfileTest {
 @Test public void onlyVerifiedStaticBaselineIsAccepted() {
  assertTrue(HostProfile.inspect("/unused",288L,"2.7.1").isKnownStaticBaseline());
  assertFalse(HostProfile.inspect("/unused",287L,"2.7.0").isKnownStaticBaseline());
  assertFalse(HostProfile.inspect("/unused",999L,"future").isKnownStaticBaseline());
 }
}
