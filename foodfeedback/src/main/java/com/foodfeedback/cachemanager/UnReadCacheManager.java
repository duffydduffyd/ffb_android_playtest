package com.foodfeedback.cachemanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.content.Context;
import android.util.Log;

import com.foodfeedback.valueobjects.MessageList;

public class UnReadCacheManager {

	public static boolean saveObject(Context ctx, MessageList obj) throws Exception {

		File loginDetailsCacheDir;

		loginDetailsCacheDir = ctx.getCacheDir();
		if (!loginDetailsCacheDir.exists()) {
			loginDetailsCacheDir.mkdirs();
		}

		final File suspend_f = new File(loginDetailsCacheDir,
				"UnReadMessageStore.data");

		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		boolean keep = true;

		try {
			fos = new FileOutputStream(suspend_f);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(obj);
		} catch (Exception e) {
			keep = false;

		} finally {
			try {
				if (oos != null)
					oos.close();
				if (fos != null)
					fos.close();
				if (!keep)
					suspend_f.delete();
			} catch (Exception e) { /* do nothing */
			}
		}
		// Log.d("saveFrinds", obj.getFriendsList().get(0).getFullname());

		return keep;

	}

	public static MessageList getObject(Context ctx) throws Exception {
		MessageList mySendMessage = null;

		File loginDetailsCacheDir;

		loginDetailsCacheDir = ctx.getCacheDir();
		if (!loginDetailsCacheDir.exists())
			loginDetailsCacheDir.mkdirs();

		final File suspend_f = new File(loginDetailsCacheDir,
				"UnReadMessageStore.data");

		FileInputStream fis = null;
		ObjectInputStream is = null;

		try {

			fis = new FileInputStream(suspend_f);
			is = new ObjectInputStream(fis);
			mySendMessage = (MessageList) is.readObject();
		} catch (Exception e) {
//			e.printStackTrace();

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

		return mySendMessage;
	}
}