package com.seiferware.java.utils.turing;

import com.seiferware.java.utils.turing.TuringMachine.TuringCommand;
import com.seiferware.java.utils.turing.TuringMachine.TuringState;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

@SuppressWarnings("javadoc")
public abstract class TuringMachineDisplay {
	public abstract <E extends Enum<E>, M extends Enum<M>> void display(@NotNull MachineState<E, M> state, boolean halted);
	public final <E extends Enum<E>, M extends Enum<M>> void run(@NotNull TuringMachine<E, M> machine) {
		MachineState<E, M> state = new MachineState<>(machine);
		while(!state.getFinalStates().contains(state.getCurrentState())) {
			display(state, false);
			TuringState<E, M> current = new TuringState<E, M>(state.getCurrentSymbol(), state.getCurrentState());
			TuringCommand<E, M> result = machine.step(current);
			if(result == null) {
				break;
			}
			if(result.getNewSymbol() != null) {
				state.setCurrentSymbol(result.getNewSymbol());
			}
			if(result.getNewState() != null) {
				state.setCurrentState(result.getNewState());
			}
			if(result.getMove() != null) {
				switch(result.getMove()) {
					case LEFT:
						state.setCurrentIndex(state.getCurrentIndex() - 1);
						break;
					case RIGHT:
						state.setCurrentIndex(state.getCurrentIndex() + 1);
						break;
					default:
						break;
				}
			}
		}
		display(state, true);
	}
	public static class MachineState<E extends Enum<E>, M extends Enum<M>> {
		private Class<E> symbolClass;
		private Class<M> stateClass;
		private E blankSymbol;
		private M currentState;
		private SortedMap<Long, E> tape;
		private Set<M> finalStates;
		private long currentIndex;
		public MachineState(@NotNull TuringMachine<E, M> machine) {
			symbolClass = machine.getSymbols();
			stateClass = machine.getStates();
			blankSymbol = machine.getBlankSymbol();
			currentState = machine.getInitalState();
			tape = new TreeMap<>();
			finalStates = Collections.unmodifiableSet(machine.getFinalStates());
		}
		@NotNull
		public E getBlankSymbol() {
			return blankSymbol;
		}
		public long getCurrentIndex() {
			return currentIndex;
		}
		public void setCurrentIndex(long currentIndex) {
			this.currentIndex = currentIndex;
		}
		@NotNull
		public M getCurrentState() {
			return currentState;
		}
		public void setCurrentState(@NotNull M currentState) {
			this.currentState = currentState;
		}
		@NotNull
		public E getCurrentSymbol() {
			return getSymbolAt(currentIndex);
		}
		public void setCurrentSymbol(@NotNull E value) {
			tape.put(currentIndex, value);
		}
		@NotNull
		public Set<M> getFinalStates() {
			return finalStates;
		}
		@NotNull
		public Class<M> getStateClass() {
			return stateClass;
		}
		@NotNull
		public E getSymbolAt(long index) {
			if(tape.containsKey(index)) {
				return tape.get(index);
			} else {
				return getBlankSymbol();
			}
		}
		@NotNull
		public Class<E> getSymbolClass() {
			return symbolClass;
		}
		@NotNull
		public SortedMap<Long, E> getTape() {
			return tape;
		}
	}
}
