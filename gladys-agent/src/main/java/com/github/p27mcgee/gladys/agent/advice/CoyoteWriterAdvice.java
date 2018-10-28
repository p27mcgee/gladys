package com.github.p27mcgee.gladys.agent.advice;

import com.github.p27mcgee.gladys.agent.bootloader.GladysRequestLedger;
import com.github.p27mcgee.gladys.agent.bootloader.GladysRequestLedgerRegistrar;

import net.bytebuddy.asm.Advice;

@Deprecated
// tapping into the CoyoteWriter data stream for 
// inserting metrics is problematic both because of the
// request cadence of the WebGoat application (SPA)
// and because CoyoteWriter's use of new IO
// doesn't provide a clean unbroken output 
// stream to tap into.
public class CoyoteWriterAdvice {

    @Advice.OnMethodEnter
    public static void before(@Advice.Argument(value = 0) String str) {
    	GladysRequestLedger grl;
    	if ((grl = GladysRequestLedgerRegistrar.getGladysRequestLedgerFromThread()) != null) {
//    		if (str != null && (
//    				str.contains("lesson title end")
//    				|| str.contains("main-content-wrapper")
//    				|| str.contains("lesson-content")
//    			)) {
//    			System.out.println("before advice for CoyoteWriter str = " + str);
//    		}
    		System.out.println("GladysRequestLedger.getUrl() = " + grl.getUrl());
    		if (grl.getUrl().endsWith(".mvc")) {
    			System.out.println("before advice for CoyoteWriter *.mvc str = " + str);
    		} else if (grl.getUrl().endsWith(".lesson.lesson")) {
    			System.out.println("before advice for CoyoteWriter *.lesson.lesson str = " + str);
    		}
    	}
	}
}
