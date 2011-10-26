package com.android.shuomi;

import com.android.shuomi.util.Util;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

public class EnhancedEditText extends EditText {

	public interface OnEditorActionDoneListener {
		public void done();
	}
	
	public interface OnRightDrawableClickListener {
		public void onClick();
	}
	
	public interface OnAfterTextChangedListener {
		public void onChanged();
	}
	
	private OnRightDrawableClickListener mRightClickListener = null;
	
	public EnhancedEditText( Context context, AttributeSet attrs, int defStyle ) {
        super( context, attrs, defStyle );
        setDefaultListener();
    }
	
    public EnhancedEditText( Context context, AttributeSet attrs ) {
        super(context, attrs);
        setDefaultListener();
    }
    
    public EnhancedEditText( Context context ) {
        super( context );
        setDefaultListener();
    }

    public void setOnEditorDoneListener( final OnEditorActionDoneListener listener ) {
    	setOnEditorActionListener( new OnEditorActionListener() {
			@Override
			public boolean onEditorAction( TextView view, int actionId, KeyEvent event ) {
				if ( actionId == EditorInfo.IME_ACTION_DONE ) {
					listener.done();
				}
				return false;
			}			
		} );
    }
    
    public void setOnRightDrawableClickListener( OnRightDrawableClickListener listener ) {
    	mRightClickListener = listener;
    }
    
    @Override
    public boolean onTouchEvent( MotionEvent event )
    {
        if ( event.getAction() == MotionEvent.ACTION_UP && getCompoundDrawables()[2] != null ) {
            if ( event.getX() > getWidth() - getPaddingRight() - getCompoundDrawables()[2].getIntrinsicWidth()) {
            	if ( mRightClickListener != null ) {
                	mRightClickListener.onClick();
                }
            	Log.d( "edittext", "right drawable" );
            	setCompoundDrawablesWithIntrinsicBounds( getCompoundDrawables()[0], getCompoundDrawables()[1],
            			null, getCompoundDrawables()[3] );
            	event.setAction( MotionEvent.ACTION_CANCEL );//use this to prevent the keyboard from coming up
            }
        }
        
        return super.onTouchEvent(event);
    }
    
    public void setOnAfterTextChangedListener( final OnAfterTextChangedListener listener ) {
    	addTextChangedListener( new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				listener.onChanged();
			}
		});
    }
    
    private void setDefaultListener()
    {
    	setOnAfterTextChangedListener( new OnAfterTextChangedListener() {

			@Override
			public void onChanged() {
				if ( Util.isValid( getText().toString() ) ) {
					setCompoundDrawablesWithIntrinsicBounds( android.R.drawable.sym_action_email, 
							0, R.drawable.ic_cancel, 0 );
				}
				else {
					setCompoundDrawablesWithIntrinsicBounds( android.R.drawable.sym_action_email, 
							0, 0, 0 );
				}
			}
			
		} );
		
		setOnRightDrawableClickListener( new OnRightDrawableClickListener() {

			@Override
			public void onClick() {
				setText( "" );
			}
			
		});
    }
}
