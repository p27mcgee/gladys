package com.github.p27mcgee.gladys.agent.advice;

import com.github.p27mcgee.gladys.agent.bootloader.GladysRequestLedger;
import com.github.p27mcgee.gladys.agent.bootloader.GladysRequestLedgerRegistrar;

import net.bytebuddy.asm.Advice;

@Deprecated 
// Activating this crashes the app.  Maybe some circular dependency?
public class ObjectConstructorAdvice {

	// After creating a new object include the estimated
	// heap usage in the GladysRequestLedger.
    @Advice.OnMethodExit
    public static void after(@Advice.This Object createdObject) {
		if (createdObject != null) {
			System.out.println("Executed StringConstructorAdvice.after() and found Advice.This = " + createdObject);
	    	GladysRequestLedger gladysRequestLedger = 
	        		GladysRequestLedgerRegistrar.getGladysRequestLedgerFromThread();
	    	if (gladysRequestLedger != null) {
	    		gladysRequestLedger.logHeapAllocation(createdObject);
	    	}
		} else {
			System.out.println("Executed StringConstructorAdvice.after() and found null Advice.This");
		}
    }
}
