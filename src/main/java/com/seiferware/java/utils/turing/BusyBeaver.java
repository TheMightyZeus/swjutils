package com.seiferware.java.utils.turing;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

/**
 * The common busy beaver turing machine.
 */
public class BusyBeaver extends TuringMachine<BusyBeaver.BusyBeaverSymbols, BusyBeaver.BusyBeaverStates> {
	/**
	 * Create the busy beaver.
	 */
	public BusyBeaver() {
		super(BusyBeaverSymbols.class, BusyBeaverStates.class);
	}
	@Override
	@NotNull
	public BusyBeaverSymbols getBlankSymbol() {
		return BusyBeaverSymbols.S0;
	}
	@NotNull
	@Override
	public EnumSet<BusyBeaverStates> getFinalStates() {
		return EnumSet.of(BusyBeaverStates.HALT);
	}
	@NotNull
	@Override
	public BusyBeaverStates getInitalState() {
		return BusyBeaverStates.A;
	}
	@Override
	@Nullable
	public TuringCommand<BusyBeaverSymbols, BusyBeaverStates> step(@NotNull TuringState<BusyBeaverSymbols, BusyBeaverStates> state) {
		switch(state.getCurrentState()) {
			case A:
				switch(state.getCurrentSymbol()) {
					case S0:
						return new TuringCommand<>(TuringMove.RIGHT, BusyBeaverSymbols.S1, BusyBeaverStates.B);
					case S1:
						return new TuringCommand<>(TuringMove.LEFT, BusyBeaverSymbols.S1, BusyBeaverStates.C);
				}
			case B:
				switch(state.getCurrentSymbol()) {
					case S0:
						return new TuringCommand<>(TuringMove.LEFT, BusyBeaverSymbols.S1, BusyBeaverStates.A);
					case S1:
						return new TuringCommand<>(TuringMove.RIGHT, BusyBeaverSymbols.S1, BusyBeaverStates.B);
				}
			case C:
				switch(state.getCurrentSymbol()) {
					case S0:
						return new TuringCommand<>(TuringMove.LEFT, BusyBeaverSymbols.S1, BusyBeaverStates.B);
					case S1:
						return new TuringCommand<>(TuringMove.RIGHT, BusyBeaverSymbols.S1, BusyBeaverStates.HALT);
				}
			default:
				return null;
		}
	}
	/**
	 * The states available to the busy beaver machine.
	 */
	public enum BusyBeaverStates {
		/**
		 * The state A.
		 */
		A,
		/**
		 * The state B.
		 */
		B,
		/**
		 * The state C.
		 */
		C,
		/**
		 * The state indicating the machine should halt.
		 */
		HALT
	}
	
	/**
	 * The symbols available to the busy beaver machine.
	 */
	public enum BusyBeaverSymbols {
		/**
		 * The number zero.
		 */
		S0,
		/**
		 * The number one.
		 */
		S1
	}
}
