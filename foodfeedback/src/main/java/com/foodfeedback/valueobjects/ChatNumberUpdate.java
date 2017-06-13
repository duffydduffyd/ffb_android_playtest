package com.foodfeedback.valueobjects;

import java.io.Serializable;

public class ChatNumberUpdate implements Serializable{
    
    /**
     * 
     */
    private static final long serialVersionUID = 2627722647004095575L;
    private int numberUpdate;

    public int getNumberUpdate() {
        return numberUpdate;
    }

    public void setNumberUpdate(int numberUpdate) {
        this.numberUpdate = numberUpdate;
    }
    
}
