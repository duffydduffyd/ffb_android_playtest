package com.foodfeedback.networking;

public interface BackgroundTask<Params, Progress, Results> {

	public Results execute(Params... params);

}
