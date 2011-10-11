package com.android.shuomi.network;

import java.util.Observable;

public class NetworkObservable extends Observable {
	public void notifyObservers( Object data ) {
		super.setChanged();
		super.notifyObservers( data );
		super.clearChanged();
	}
}
