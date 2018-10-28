package com.github.p27mcgee.gladys.agent.advice;

import com.github.p27mcgee.gladys.agent.bootloader.GladysRequestLedger;
import com.github.p27mcgee.gladys.agent.bootloader.GladysRequestLedgerRegistrar;

import net.bytebuddy.asm.Advice;

public class StringConstructorAdvice {

	// After creating a new String bump the count in GladysRequestLedger
	// if one is registered to the thread.
    @Advice.OnMethodExit
    public static void after(@Advice.This Object createdObject) {
    	GladysRequestLedger gladysRequestLedger = 
    		GladysRequestLedgerRegistrar.getGladysRequestLedgerFromThread();
    	if (gladysRequestLedger != null) {
    		gladysRequestLedger.logStringCreate();
    	}
    }
}
