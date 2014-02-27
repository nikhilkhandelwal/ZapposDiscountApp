package com.zappos.discount.main;


import android.view.View;
import android.widget.RatingBar;
import android.widget.ToggleButton;


public class ViewHolder {
	ToggleButton switchButton=null;
	  
	  ViewHolder(View base) {
	    this.switchButton=(ToggleButton)base.findViewById(R.id.favorite_button);
	  }

}
