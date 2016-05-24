package com.seiferware.java.utils.turing;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

/**
 * An interface used to represent a Turing machine implementation.
 *
 * @param <E>
 * 		The enumeration representing the set of possible symbols.
 * @param <M>
 * 		The enumeration representing the set of possible machine states.
 */
public abstract class TuringMachine<E extends Enum<E>, M extends Enum<M>> {
	protected final Class<E> symbolClass;
	protected final Class<M> stateClass;
	/**
	 * Creates the machine and initializes the classes for the possible symbols and states.
	 *
	 * @param symbols
	 * 		The possible symbols to be written to the tape.
	 * @param states
	 * 		The possible machine states.
	 */
	public TuringMachine(@NotNull Class<E> symbols, @NotNull Class<M> states) {
		symbolClass = symbols;
		stateClass = states;
	}
	/**
	 * Indicate which symbol should be used as the "blank" symbol. That is, the one that is assumed to be on all
	 * uninitialized segments of tape.
	 *
	 * @return The blank symbol.
	 */
	@NotNull
	public abstract E getBlankSymbol();
	/**
	 * Get the set of states that can be considered final. That is, setting the machine to any of these states causes it
	 * to halt.
	 *
	 * @return The set of final states, if applicable. An empty set if the machine is to run indefinitely.
	 */
	@NotNull
	public abstract EnumSet<M> getFinalStates();
	/**
	 * The state in which the machine should begin.
	 *
	 * @return The initial state of the machine.
	 */
	@NotNull
	public abstract M getInitalState();
	/**
	 * Get the class of the enumeration representing the machine states.
	 *
	 * @return The state class.
	 */
	@NotNull
	public final Class<M> getStates() {
		return stateClass;
	}
	/**
	 * Get the class of the enumeration representing the symbols.
	 *
	 * @return The symbol class.
	 */
	@NotNull
	public final Class<E> getSymbols() {
		return symbolClass;
	}
	/**
	 * Executes one step of the machine.
	 *
	 * @param state
	 * 		The current state information of the machine.
	 *
	 * @return The instructions to the machine.
	 */
	@Nullable
	public abstract TuringCommand<E, M> step(@NotNull TuringState<E, M> state);
	/**
	 * An enumeration to indicate the desired movement of the tape through the machine.
	 */
	public enum TuringMove {
		/**
		 * Indicates the tape should remain in place.
		 */
		NONE,
		/**
		 * Indicates the tape should be moved one segment to the left.
		 */
		LEFT,
		/**
		 * Indicates the tape should be moved one segment to the right.
		 */
		RIGHT
	}
	
	/**
	 * A command issued by the machine at each step.
	 *
	 * @param <E>
	 * 		The enumeration representing the set of possible symbols.
	 * @param <M>
	 * 		The enumeration representing the set of possible machine states.
	 */
	public static final class TuringCommand<E extends Enum<E>, M extends Enum<M>> {
		private TuringMove move = null;
		private E newSymbol = null;
		private M newState = null;
		/**
		 * Creates and initializes the command.
		 *
		 * @param move
		 * 		The direction to move the tape.
		 * @param newSymbol
		 * 		The symbol to write to the tape, or {@code null} for no change.
		 * @param newState
		 * 		The state to assign to the machine or {@code null} for no change.
		 */
		public TuringCommand(@Nullable TuringMove move, @Nullable E newSymbol, @Nullable M newState) {
			this.move = move;
			this.newSymbol = newSymbol;
			this.newState = newState;
		}
		/**
		 * How to move the tape. A value of {@code null} is the same as {@link TuringMove#NONE}.
		 *
		 * @return {@link TuringMove#LEFT LEFT}, {@link TuringMove#RIGHT RIGHT}, or {@link TuringMove#NONE NONE}.
		 */
		@Nullable
		public TuringMove getMove() {
			return move;
		}
		/**
		 * The new state to apply to the machine. A value of {@code null} indicates no change.
		 *
		 * @return The new state or {@code null}.
		 */
		@Nullable
		public M getNewState() {
			return newState;
		}
		/**
		 * The new symbol to write on the tape. A value of {@code null} indicates no change.
		 *
		 * @return The new symbol or {@code null}.
		 */
		@Nullable
		public E getNewSymbol() {
			return newSymbol;
		}
	}
	
	/**
	 * The current state of the machine.
	 *
	 * @param <E>
	 * 		The enumeration representing the set of possible symbols.
	 * @param <M>
	 * 		The enumeration representing the set of possible machine states.
	 */
	public static final class TuringState<E extends Enum<E>, M extends Enum<M>> {
		private E currentSymbol;
		private M currentState;
		/**
		 * Creates the state representation.
		 *
		 * @param currentSymbol
		 * 		The symbol on the current segment of tape.
		 * @param currentState
		 * 		The current machine state.
		 */
		public TuringState(@NotNull E currentSymbol, @NotNull M currentState) {
			this.currentSymbol = currentSymbol;
			this.currentState = currentState;
		}
		/**
		 * @return The current machine state.
		 */
		@NotNull
		public M getCurrentState() {
			return currentState;
		}
		/**
		 * @return The symbol on the current position of the tape.
		 */
		@NotNull
		public E getCurrentSymbol() {
			return currentSymbol;
		}
	}
}
