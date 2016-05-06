// Copyright 2016 The Domain Registry Authors. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package google.registry.model.common;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableSet;

import google.registry.testing.ExceptionRule;

import org.joda.time.DateTime;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Unit tests for {@link TimeOfYear}. */
@RunWith(JUnit4.class)
public class TimeOfYearTest {

  private static final DateTime february28 = DateTime.parse("2012-02-28T01:02:03.0Z");
  private static final DateTime february29 = DateTime.parse("2012-02-29T01:02:03.0Z");
  private static final DateTime march1 = DateTime.parse("2012-03-01T01:02:03.0Z");

  @Rule
  public final ExceptionRule thrown = new ExceptionRule();

  @Test
  public void testSuccess_fromDateTime() throws Exception {
    // We intentionally don't allow leap years in TimeOfYear, so February 29 should be February 28.
    assertThat(TimeOfYear.fromDateTime(february28)).isEqualTo(TimeOfYear.fromDateTime(february29));
    assertThat(TimeOfYear.fromDateTime(february29)).isNotEqualTo(TimeOfYear.fromDateTime(march1));
  }

  @Test
  public void testSuccess_nextAfter() throws Exception {
    // This should be lossless because atOrAfter includes an exact match.
    assertThat(TimeOfYear.fromDateTime(march1).getNextInstanceAtOrAfter(march1)).isEqualTo(march1);
    // This should be a year later because we stepped forward a millisecond
    assertThat(TimeOfYear.fromDateTime(march1).getNextInstanceAtOrAfter(march1.plusMillis(1)))
        .isEqualTo(march1.plusYears(1));
  }

  @Test
  public void testSuccess_nextBefore() throws Exception {
    // This should be lossless because beforeOrAt includes an exact match.
    assertThat(TimeOfYear.fromDateTime(march1).getLastInstanceBeforeOrAt(march1)).isEqualTo(march1);
    // This should be a year earlier because we stepped backward a millisecond
    assertThat(TimeOfYear.fromDateTime(march1).getLastInstanceBeforeOrAt(march1.minusMillis(1)))
        .isEqualTo(march1.minusYears(1));
  }

  @Test
  public void testSuccess_getInstancesOfTimeOfYearInRange() {
    DateTime startDate = DateTime.parse("2012-05-01T00:00:00Z");
    DateTime endDate = DateTime.parse("2016-02-01T00:00:00Z");
    TimeOfYear timeOfYear = TimeOfYear.fromDateTime(DateTime.parse("2012-10-01T00:00:00Z"));
    ImmutableSet<DateTime> actual = timeOfYear.getInstancesInRange(startDate, endDate);
    ImmutableSet<DateTime> expected = ImmutableSet.<DateTime>of(
        DateTime.parse("2012-10-01T00:00:00Z"),
        DateTime.parse("2013-10-01T00:00:00Z"),
        DateTime.parse("2014-10-01T00:00:00Z"),
        DateTime.parse("2015-10-01T00:00:00Z"));
    assertThat(actual).containsExactlyElementsIn(expected);
  }

  @Test
  public void testSuccess_getInstancesOfTimeOfYearInRange_empty() {
    DateTime startDate = DateTime.parse("2012-05-01T00:00:00Z");
    DateTime endDate = DateTime.parse("2013-02-01T00:00:00Z");
    TimeOfYear timeOfYear = TimeOfYear.fromDateTime(DateTime.parse("2012-03-01T00:00:00Z"));
    ImmutableSet<DateTime> actual = timeOfYear.getInstancesInRange(startDate, endDate);
    assertThat(actual).isEmpty();
  }

  @Test
  public void testSuccess_getInstancesOfTimeOfYearInRange_inclusive() {
    DateTime startDate = DateTime.parse("2012-05-01T00:00:00Z");
    DateTime endDate = DateTime.parse("2015-05-01T00:00:00Z");
    TimeOfYear timeOfYear = TimeOfYear.fromDateTime(DateTime.parse("2012-05-01T00:00:00Z"));
    ImmutableSet<DateTime> actual = timeOfYear.getInstancesInRange(startDate, endDate);
    ImmutableSet<DateTime> expected = ImmutableSet.<DateTime>of(
        DateTime.parse("2012-05-01T00:00:00Z"),
        DateTime.parse("2013-05-01T00:00:00Z"),
        DateTime.parse("2014-05-01T00:00:00Z"),
        DateTime.parse("2015-05-01T00:00:00Z"));
    assertThat(actual).containsExactlyElementsIn(expected);
  }

  @Test
  public void testFailure_getInstancesOfTimeOfYearInRange_inverted() {
    thrown.expect(IllegalArgumentException.class, "Lower bound is not before or at upper bound.");
    TimeOfYear.fromDateTime(DateTime.parse("2013-10-01T00:00:00Z")).getInstancesInRange(
        DateTime.parse("2015-10-01T00:00:00Z"),
        DateTime.parse("2012-10-01T00:00:00Z"));
  }
}