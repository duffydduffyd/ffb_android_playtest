package com.foodfeedback.cachemanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

import com.foodfeedback.valueobjects.Coach;
import com.foodfeedback.valueobjects.MemberInfo;

public class CoachOrderingCacheManager {

	public static void saveObject(Context ctx, ArrayList<MemberInfo> obj)
			throws Exception {

		Thread thread = new Thread(new MySaveObjectTask(ctx, obj));
	    thread.start(); //in background thread
		

	}
	static class MySaveObjectTask implements Runnable {
		private Context mContext;
		private ArrayList<MemberInfo> myObjectToSave;

		public MySaveObjectTask(Context ctx, ArrayList<MemberInfo> obj) {
			this.myObjectToSave = obj;
			this.mContext = ctx;
		}

		public void run() {
			File loginDetailsCacheDir;

			loginDetailsCacheDir = mContext.getCacheDir();
			if (!loginDetailsCacheDir.exists()) {
				loginDetailsCacheDir.mkdirs();
			}

			final File suspend_f = new File(loginDetailsCacheDir,
					"CoachOrderListStore.data");

			FileOutputStream fos = null;
			ObjectOutputStream oos = null;

			try {
				fos = new FileOutputStream(suspend_f);
				oos = new ObjectOutputStream(fos);
				oos.writeObject(myObjectToSave);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (oos != null)
						oos.close();
					if (fos != null)
						fos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	public static ArrayList<MemberInfo> rearrangeMembers(Context ctx,
			ArrayList<MemberInfo> olderCoachList) {

		ArrayList<MemberInfo> myCoachList = new ArrayList<MemberInfo>();
		try {
			myCoachList = getObject(ctx);
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		// set the ordering here

		// If there was nothing saved earlier. Save it now and return the
		// original list
		if (myCoachList == null || myCoachList.size() == 0) {
			try {
				saveObject(ctx, olderCoachList);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return olderCoachList;
		}

		
		// // If both have the same set of values then the send the ordered one.
		// DOES NOT WORK - containsall function
		// if (olderCoachList.containsAll(myCoachList) &&
		// myCoachList.containsAll(olderCoachList)){
		// Log.i("SORT","Exactly same items.Return the one that previously saved");
		// return myCoachList;
		// }

		ArrayList<MemberInfo> tempCoachList = new ArrayList<MemberInfo>();
		// First get all the ordered items from myCoachList that are in the
		// provided list
		for (int j = 0; j < myCoachList.size(); j++) {
			for (int i = 0; i < olderCoachList.size(); i++) {
				if (olderCoachList.get(i) !=null &&  myCoachList!=null && myCoachList.get(j)!=null){
					if (olderCoachList.get(i).getId() == myCoachList.get(j).getId()) {
						// Directly add to the list. Found in saved coaches and in
						// list provided
						tempCoachList.add(myCoachList.get(j));
					}
				}
			}
		}

		// Now check if any items are in the provided list that is not in the
		// coachlist
		for (int i = 0; i < olderCoachList.size(); i++) {
			boolean found = false;
			for (int j = 0; j < myCoachList.size(); j++) {
				if (olderCoachList.get(i) !=null &&  myCoachList!=null && myCoachList.get(j)!=null){
					if (olderCoachList.get(i).getId() == myCoachList.get(j).getId()) {
						found = true;
					}
				}
			}
			if (!found){
				//It was not in the saved list. So add
				tempCoachList.add(olderCoachList.get(i));
			}
		}
		
		myCoachList = tempCoachList;
		try {
			saveObject(ctx, myCoachList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return myCoachList;
		

	}

	
	public static ArrayList<Coach> rearrangeCoaches(Context ctx,
			ArrayList<Coach> olderCoachList) {

		ArrayList<MemberInfo> myCoachList = new ArrayList<MemberInfo>();
		try {
			myCoachList = getObject(ctx);
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		// set the ordering here

		// If there was nothing saved earlier. Save it now and return the
		// original list
		if (myCoachList == null || myCoachList.size() == 0) {
			try {
				myCoachList = new ArrayList<MemberInfo>();
				for (int j = 0; j < olderCoachList.size(); j++) {
					myCoachList.add(olderCoachList.get(j).getMemberInfo());
				}
				saveObject(ctx, myCoachList);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return olderCoachList;
		}

		
		// // If both have the same set of values then the send the ordered one.
		// DOES NOT WORK - containsall function
		// if (olderCoachList.containsAll(myCoachList) &&
		// myCoachList.containsAll(olderCoachList)){
		// Log.i("SORT","Exactly same items.Return the one that previously saved");
		// return myCoachList;
		// }

		ArrayList<Coach> tempCoachList = new ArrayList<Coach>();
		// First get all the ordered items from myCoachList that are in the
		// provided list
		for (int j = 0; j < myCoachList.size(); j++) {
			for (int i = 0; i < olderCoachList.size(); i++) {
				if (olderCoachList.get(i) !=null && olderCoachList.get(i).getMemberInfo()!= null &&  myCoachList!=null && myCoachList.get(j)!=null){
					if (olderCoachList.get(i).getMemberInfo().getId() == myCoachList.get(j).getId()) {
						// Directly add to the list. Found in saved coaches and in
						// list provided
						tempCoachList.add(olderCoachList.get(i));
					}	
				}
				
			}
		}

		// Now check if any items are in the provided list that is not in the
		// coachlist
		for (int i = 0; i < olderCoachList.size(); i++) {
			boolean found = false;
			for (int j = 0; j < myCoachList.size(); j++) {
				if (olderCoachList.get(i) !=null && olderCoachList.get(i).getMemberInfo()!= null &&  myCoachList!=null && myCoachList.get(j)!=null){
					
					if (olderCoachList.get(i).getMemberInfo().getId() == myCoachList.get(j).getId()) {
						found = true;
					}
				}
			}
			if (!found){
				//It was not in the saved list. So add
				tempCoachList.add(olderCoachList.get(i));
			}
		}
		
		olderCoachList = tempCoachList;
		try {
			
			try {
				myCoachList = new ArrayList<MemberInfo>();
				for (int j = 0; j < olderCoachList.size(); j++) {
					myCoachList.add(olderCoachList.get(j).getMemberInfo());
				}
				saveObject(ctx, myCoachList);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return olderCoachList;
		

	}
	public static ArrayList<MemberInfo> getObject(Context ctx) throws Exception {
		ArrayList<MemberInfo> myCoachOrderList = null;

		File loginDetailsCacheDir;

		loginDetailsCacheDir = ctx.getCacheDir();
		if (!loginDetailsCacheDir.exists())
			loginDetailsCacheDir.mkdirs();

		final File suspend_f = new File(loginDetailsCacheDir,
				"CoachOrderListStore.data");

		FileInputStream fis = null;
		ObjectInputStream is = null;

		try {
			fis = new FileInputStream(suspend_f);
			is = new ObjectInputStream(fis);
			myCoachOrderList = (ArrayList<MemberInfo>) is.readObject();
		} catch (Exception e) {
			// e.printStackTrace();

		} finally {
			try {
				if (fis != null)
					fis.close();
				if (is != null)
					is.close();

			} catch (Exception e) {
			}
		}

		try {
		} catch (Exception e) {
			Log.d("exception", e.toString());
		}

		return myCoachOrderList;
	}
}