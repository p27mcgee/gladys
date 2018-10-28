package com.github.p27mcgee.gladys.agent;

public class InstrumentMe {

	// Just for test
	public static void main(String[] args) {
		System.out.println("InstrumentMe is executing...");
		new Object();
		System.out.println("InstrumentMe has executed!");
	}
}
