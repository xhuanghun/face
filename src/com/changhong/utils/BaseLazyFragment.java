package com.changhong.utils;

import android.support.v4.app.Fragment;

public abstract class BaseLazyFragment extends Fragment{
	
	public boolean isVisible;
	
	
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if(getUserVisibleHint()){
			isVisible=true;
			onVisible();
		}else{
		    isVisible=false;
		    onInVisible();
		}
	}

	protected void  onVisible(){
		lazyload();
	}  

	protected abstract void lazyload();

	protected  void  onInVisible(){};

}
