package com.android.shuomi.persistence;

import java.util.Observable;

public class DbObservable extends Observable {
	public void notifyObservers( Object data ) {
		super.setChanged();
		super.notifyObservers( data );
		super.clearChanged();
	}
}
