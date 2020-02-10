/**********************************************************************************************************************
 * garbagecat                                                                                                         *
 *                                                                                                                    *
 * Copyright (c) 2008-2020 Red Hat, Inc.                                                                              *
 *                                                                                                                    * 
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse *
 * Public License v1.0 which accompanies this distribution, and is available at                                       *
 * http://www.eclipse.org/legal/epl-v10.html.                                                                         *
 *                                                                                                                    *
 * Contributors:                                                                                                      *
 *    Red Hat, Inc. - initial API and implementation                                                                  *
 *********************************************************************************************************************/
package org.eclipselabs.garbagecat.domain.jdk.unified;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipselabs.garbagecat.domain.JvmRun;
import org.eclipselabs.garbagecat.service.GcManager;
import org.eclipselabs.garbagecat.util.Constants;
import org.eclipselabs.garbagecat.util.jdk.JdkUtil;
import org.eclipselabs.garbagecat.util.jdk.JdkUtil.LogEventType;
import org.eclipselabs.garbagecat.util.jdk.Jvm;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * @author <a href="mailto:mmillson@redhat.com">Mike Millson</a>
 */
public class TestShenandoahConcurrentEvent extends TestCase {

    public void testLogLine() {
        String logLine = "[0.437s][info][gc] GC(0) Concurrent reset 15M->16M(64M) 4.701ms";
        Assert.assertTrue("Log line not recognized as " + JdkUtil.LogEventType.SHENANDOAH_CONCURRENT.toString() + ".",
                ShenandoahConcurrentEvent.match(logLine));
        ShenandoahConcurrentEvent event = new ShenandoahConcurrentEvent(logLine);
        Assert.assertEquals("Event name incorrect.", JdkUtil.LogEventType.SHENANDOAH_CONCURRENT.toString(),
                event.getName());
        Assert.assertEquals("Time stamp not parsed correctly.", 437 - 4, event.getTimestamp());
        Assert.assertEquals("Combined begin size not parsed correctly.", 15 * 1024, event.getCombinedOccupancyInit());
        Assert.assertEquals("Combined end size not parsed correctly.", 16 * 1024, event.getCombinedOccupancyEnd());
        Assert.assertEquals("Combined allocation size not parsed correctly.", 64 * 1024, event.getCombinedSpace());
    }

    public void testIdentityEventType() {
        String logLine = "[0.437s][info][gc] GC(0) Concurrent reset 15M->16M(64M) 4.701ms";
        Assert.assertEquals(JdkUtil.LogEventType.SHENANDOAH_CONCURRENT + "not identified.",
                JdkUtil.LogEventType.SHENANDOAH_CONCURRENT, JdkUtil.identifyEventType(logLine));
    }

    public void testParseLogLine() {
        String logLine = "[0.437s][info][gc] GC(0) Concurrent reset 15M->16M(64M) 4.701ms";
        Assert.assertTrue(JdkUtil.LogEventType.SHENANDOAH_CONCURRENT.toString() + " not parsed.",
                JdkUtil.parseLogLine(logLine) instanceof ShenandoahConcurrentEvent);
    }

    public void testNotBlocking() {
        String logLine = "[0.437s][info][gc] GC(0) Concurrent reset 15M->16M(64M) 4.701ms";
        Assert.assertFalse(
                JdkUtil.LogEventType.SHENANDOAH_CONCURRENT.toString() + " incorrectly indentified as blocking.",
                JdkUtil.isBlocking(JdkUtil.identifyEventType(logLine)));
    }

    public void testReportable() {
        Assert.assertTrue(JdkUtil.LogEventType.SHENANDOAH_CONCURRENT.toString() + " not indentified as reportable.",
                JdkUtil.isReportable(JdkUtil.LogEventType.SHENANDOAH_CONCURRENT));
    }

    public void testUnified() {
        List<LogEventType> eventTypes = new ArrayList<LogEventType>();
        eventTypes.add(LogEventType.SHENANDOAH_CONCURRENT);
        Assert.assertTrue(JdkUtil.LogEventType.SHENANDOAH_CONCURRENT.toString() + " not indentified as unified.",
                JdkUtil.isUnifiedLogging(eventTypes));
    }

    public void testMarking() {
        String logLine = "[0.528s][info][gc] GC(1) Concurrent marking 16M->17M(64M) 7.045ms";
        Assert.assertTrue("Log line not recognized as " + JdkUtil.LogEventType.SHENANDOAH_CONCURRENT.toString() + ".",
                ShenandoahConcurrentEvent.match(logLine));
    }

    public void testMarkingProcessWeakrefs() {
        String logLine = "[0.454s][info][gc] GC(0) Concurrent marking (process weakrefs) 17M->19M(64M) 15.264ms";
        Assert.assertTrue("Log line not recognized as " + JdkUtil.LogEventType.SHENANDOAH_CONCURRENT.toString() + ".",
                ShenandoahConcurrentEvent.match(logLine));
    }

    public void testMarkingUpdateRefs() {
        String logLine = "[10.458s][info][gc] GC(279) Concurrent marking (update refs) 47M->48M(64M) 5.559ms";
        Assert.assertTrue("Log line not recognized as " + JdkUtil.LogEventType.SHENANDOAH_CONCURRENT.toString() + ".",
                ShenandoahConcurrentEvent.match(logLine));
    }

    public void testMarkingUpdateRefsProcessWeakrefs() {
        String logLine = "[11.012s][info][gc] GC(300) Concurrent marking (update refs) (process weakrefs) "
                + "49M->49M(64M) 5.416ms";
        Assert.assertTrue("Log line not recognized as " + JdkUtil.LogEventType.SHENANDOAH_CONCURRENT.toString() + ".",
                ShenandoahConcurrentEvent.match(logLine));
    }

    public void testPrecleaning() {
        String logLine = "[0.455s][info][gc] GC(0) Concurrent precleaning 19M->19M(64M) 0.202ms";
        Assert.assertTrue("Log line not recognized as " + JdkUtil.LogEventType.SHENANDOAH_CONCURRENT.toString() + ".",
                ShenandoahConcurrentEvent.match(logLine));
    }

    public void testEvacuation() {
        String logLine = "[0.465s][info][gc] GC(0) Concurrent evacuation 17M->19M(64M) 6.528ms";
        Assert.assertTrue("Log line not recognized as " + JdkUtil.LogEventType.SHENANDOAH_CONCURRENT.toString() + ".",
                ShenandoahConcurrentEvent.match(logLine));
    }

    public void testUpdateReferences() {
        String logLine = "[0.470s][info][gc] GC(0) Concurrent update references 19M->19M(64M) 4.708ms";
        Assert.assertTrue("Log line not recognized as " + JdkUtil.LogEventType.SHENANDOAH_CONCURRENT.toString() + ".",
                ShenandoahConcurrentEvent.match(logLine));
    }

    public void testCleanup() {
        String logLine = "[0.472s][info][gc] GC(0) Concurrent cleanup 18M->15M(64M) 0.036ms";
        Assert.assertTrue("Log line not recognized as " + JdkUtil.LogEventType.SHENANDOAH_CONCURRENT.toString() + ".",
                ShenandoahConcurrentEvent.match(logLine));
    }

    public void testLoggingDetailsResetNoSizes() {
        String logLine = "[41.892s][info][gc,start     ] GC(1500) Concurrent reset";
        Assert.assertTrue("Log line not recognized as " + JdkUtil.LogEventType.SHENANDOAH_CONCURRENT.toString() + ".",
                ShenandoahConcurrentEvent.match(logLine));
    }

    public void testLoggingDetailsReset() {
        String logLine = "[41.893s][info][gc           ] GC(1500) Concurrent reset 50M->50M(64M) 0.126ms";
        Assert.assertTrue("Log line not recognized as " + JdkUtil.LogEventType.SHENANDOAH_CONCURRENT.toString() + ".",
                ShenandoahConcurrentEvent.match(logLine));
    }

    public void testLoggingUptimeMillis() {
        String logLine = "[2019-02-05T14:47:34.156-0200][3068ms] GC(0) Concurrent reset";
        Assert.assertTrue("Log line not recognized as " + JdkUtil.LogEventType.SHENANDOAH_CONCURRENT.toString() + ".",
                ShenandoahConcurrentEvent.match(logLine));
    }

    public void testLoggingUncommitUptimeMillis() {
        String logLine = "[2019-02-05T14:52:31.138-0200][300050ms] Concurrent uncommit 874M->874M(1303M) 5.654ms";
        Assert.assertTrue("Log line not recognized as " + JdkUtil.LogEventType.SHENANDOAH_CONCURRENT.toString() + ".",
                ShenandoahConcurrentEvent.match(logLine));
        ShenandoahConcurrentEvent event = new ShenandoahConcurrentEvent(logLine);
        Assert.assertEquals("Event name incorrect.", JdkUtil.LogEventType.SHENANDOAH_CONCURRENT.toString(),
                event.getName());
        Assert.assertEquals("Time stamp not parsed correctly.", 300050 - 5, event.getTimestamp());
        Assert.assertEquals("Combined begin size not parsed correctly.", 874 * 1024, event.getCombinedOccupancyInit());
        Assert.assertEquals("Combined end size not parsed correctly.", 874 * 1024, event.getCombinedOccupancyEnd());
        Assert.assertEquals("Combined allocation size not parsed correctly.", 1303 * 1024, event.getCombinedSpace());
    }

    public void testLoggingUncommitUptimeMillisNoGcEventNumber() {
        String logLine = "[2019-02-05T14:52:31.132-0200][300044ms] Concurrent uncommit";
        Assert.assertTrue("Log line not recognized as " + JdkUtil.LogEventType.SHENANDOAH_CONCURRENT.toString() + ".",
                ShenandoahConcurrentEvent.match(logLine));
    }

    /**
     * Test max heap space and occupancy data.
     */
    public void testMaxHeapData() {
        // TODO: Create File in platform independent way.
        File testFile = new File("src/test/data/dataset167.txt");
        GcManager gcManager = new GcManager();
        File preprocessedFile = gcManager.preprocess(testFile, null);
        gcManager.store(preprocessedFile, false);
        JvmRun jvmRun = gcManager.getJvmRun(new Jvm(null, null), Constants.DEFAULT_BOTTLENECK_THROUGHPUT_THRESHOLD);
        Assert.assertFalse(JdkUtil.LogEventType.UNKNOWN.toString() + " collector identified.",
                jvmRun.getEventTypes().contains(LogEventType.UNKNOWN));
        Assert.assertEquals("Max heap occupancy for a non blocking event not parsed correctly.", 19 * 1024,
                jvmRun.getMaxHeapOccupancyNonBlocking());
        Assert.assertEquals("Max heap space for a non blocking event not parsed correctly.", 33 * 1024,
                jvmRun.getMaxHeapSpaceNonBlocking());
    }
}
