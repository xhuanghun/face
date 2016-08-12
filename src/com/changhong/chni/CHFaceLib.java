package com.changhong.chni;

import android.graphics.Bitmap;

public class CHFaceLib {
	/**
	 * Initialize function, should be called before other function calls
	 * @param configFile	- specify a file to store user-register information
	 * @return 0 - success 
	 */
	public native static int nativeInitFaceEngine(byte[] configFile);
	
	/**
	 * Uninitialize, release sources of the lib
	 * @param
	 * @return void
	 */
	public native static void nativeReleaseFaceEngine();

	/**
	 * inform `chni` the format of image
	 * @param mode
	 * 0 - for Andorid phone, portrait, that is the image need rotate -90 degrees and flip
	 * 1 - for landscape, the image is normal image
	 */
	public native static void nativeSetImageMode(int mode);
	
	/**
	 * Face detection: fill face position information of @param fd
	 * @param frame -raw YUV420 data
	 * @param width -width of image
	 * @param height -height of image
	 * @param b - removed later
	 * @param fd - face description structure, store the detected information, fd.length as the array-size
	 * @return - number of faces detected
	 */
	public native static int nativeFaceDetect(byte[] frame, int width, int height, int fd[]);
	
	/**
	 * Face recognition: fill face position information && eye locaiton info && recognition info
	 * @param frame - the same as nativeFaceDetect()
	 * @param width - the same as nativeFaceDetect()
	 * @param height - the same as nativeFaceDetect()
	 * @param b - the same as nativeFaceDetect()
	 * @param fd - the same as nativeFaceDetect()
	 * @return - number of faces detected
	 */
	public native static int nativeFaceRecog(byte[] frame, int width, int height, int fd[]);
	
	/**
	 * The steps for adding a new user:
	 * 1: call nativeAddUser()
	 * 2: call nativeAddSampleForUser() for a few frames
	 * 3: call nativeAddUserEnd()
	 * 
	 * 1st step for the add-user operation: specify user name
	 * @param name - specify user name
	 * @return 0 - success
	 * 		  -1 - error
	 */
	public native static int nativeAddUser(byte[] name);
	
	/**
	 * 2nd step for the add-user operation: collect a sample for the newly added user.
	 * @param frame - the same as nativeFaceDetect()
	 * @param width - the same as nativeFaceDetect()
	 * @param height - the same as nativeFaceDetect()
	 * @param b - the same as nativeFaceDetect()
	 * @param fd - the same as nativeFaceDetect()
	 * @return 0 - success
	 *        -1 - No face found
	 *        -2 - more than one face
	 *        -3 - no eyes found
	 */	
	public native static int nativeAddSampleForUser(byte[] frame, int width, int height, int fd[]);
	
	/**
	 * @brief update model
	 * @param frame, width, height @see nativeFaceDetect()
	 * @param id - id of the user
	 * @param mode - 0 replace-mode, 1 append-mode
	 * @return
	 *        0 - success
	 *     else - failure
	 * @author wk
	 */
	public native static int nativeUpdateModelForUser(byte[] frame, int width, int height, int id, int mode);
	/**
	 * 3rd step for the add-user operation: store user info
	 * @return id - the id of the newly add user, valid id should be no less than 0
	 *         -1 - error
	 */
	public native static int nativeAddUserEnd();
	
	/**
	 * Delete user by id
	 * @param id - specify the id of the user who should be removed
	 * @return 0 - success
	 *        -1 - error
	 */
	public native static int nativeDelUser(int id);
	
	/**
	 * Get name by id
	 * @param id - specify the id
	 * @param name - provide buffer for the returned name string
	 * @return (length) - the number of bytes wrote to name, bigger than 0
	 *         -1 - provide wrong id
	 *         -2 - no such user
	 */
	public native static int nativeId2Name(int id, byte[] name);
	
	/**
	 * Get all the IDs of registered users
	 * @param ids - provide buffer for the returned IDs
	 * @return (size) - how many IDs are got, if the return value == ids.length, this may imply the buffer is too small
	 */
	public native static int nativeGetAllUserIDs(int ids[]);
	
	static
	{
		System.loadLibrary("CHFaceLib");
	}
	
	public String TAG = "FaceLib";
	public static final String defaultConfigFile = "/ff.bin";
	public static int statusCode = 0;
	
	public static class FaceDesc {
		public int x, y, w, h; // face position info
		public int lx, ly, rx, ry; // eyes position info
		public int id, conf;       // recognition result info
		public int reserved1, reserved2, reserved3, reserved4, reserved5; // reserved info for later use
	};
	private static final int FD_SIZE = 15; // size of FaceDesc
	private static final int MAX_FD_SIZE = 10;
	
	private static FaceDesc[] fillFaceDesc(int[] array, int arraysize)
	{
		FaceDesc[] fd = new FaceDesc[arraysize];
		for (int i = 0; i < arraysize; ++i) {
			fd[i] = new FaceDesc();
			fd[i].x = array[i*FD_SIZE+0];
			fd[i].y = array[i*FD_SIZE+1];
			fd[i].w = array[i*FD_SIZE+2];
			fd[i].h = array[i*FD_SIZE+3];
			fd[i].lx = array[i*FD_SIZE+4];
			fd[i].ly = array[i*FD_SIZE+5];
			fd[i].rx = array[i*FD_SIZE+6];
			fd[i].ry = array[i*FD_SIZE+7];
			fd[i].id = array[i*FD_SIZE+8];
			fd[i].conf = array[i*FD_SIZE+9];
			fd[i].reserved1 = array[i*FD_SIZE+10];
			fd[i].reserved2 = array[i*FD_SIZE+11];
			fd[i].reserved3 = array[i*FD_SIZE+12];
			fd[i].reserved4 = array[i*FD_SIZE+13];
			fd[i].reserved5 = array[i*FD_SIZE+14];
		}
		return fd;		
	}
	
	public static FaceDesc[] FaceDetect(byte[] frame, int width, int height)
	{
		int[] fdArray = new int[MAX_FD_SIZE * FD_SIZE];
		int x = nativeFaceDetect(frame, width, height, fdArray);
		if (x <= 0 || x > MAX_FD_SIZE)
			return new FaceDesc[0];
		return fillFaceDesc(fdArray, x);
	}
	public static FaceDesc[] FaceRecog(byte[] frame, int width, int height)
	{
		int[] fdArray = new int[MAX_FD_SIZE * FD_SIZE];
		int x = nativeFaceRecog(frame, width, height, fdArray);
		if (x <= 0 || x > MAX_FD_SIZE)
			return new FaceDesc[0];
		return fillFaceDesc(fdArray, x);
	}
	public static FaceDesc[] AddSampleForUser(byte[] frame, int width, int height)
	{
		int[] fdArray = new int[MAX_FD_SIZE * FD_SIZE];
		int ret = nativeAddSampleForUser(frame, width, height, fdArray);
		statusCode = ret;
		if (ret == 0) {
			return fillFaceDesc(fdArray, 1);
		}
		return new FaceDesc[0];
	}
	public static int GetUserCount()
	{
		int[] ids = new int[20];
		return nativeGetAllUserIDs(ids);
	}	
	public static FaceDesc[] EnumUser()
	{
		int[] ids = new int[20];
		int ret = nativeGetAllUserIDs(ids);
		FaceDesc[] fd = new FaceDesc[ret];
		for (int i = 0; i < ret; ++i) {
			fd[i] = new FaceDesc();
			fd[i].x = 150; fd[i].y = (i+1)*20; fd[i].w = fd[i].h = 1;
			fd[i].lx = fd[i].ly = fd[i].rx = fd[i].ry = 0;
			fd[i].id = ids[i]; fd[i].conf = 0;
			fd[i].reserved1 = fd[i].reserved2 = fd[i].reserved3 = fd[i].reserved4 = fd[i].reserved5 = 0;
		}
		return fd;
	}
	public static byte[] GetNameById(int id)
	{
		byte[] name = new byte[100];
		int ret = nativeId2Name(id, name);
		if (ret < 0)
			return new byte[0];	
		byte[] sname = new byte[ret];
		for (int i= 0; i < ret; ++i)
			sname[i] = name[i];
		return sname;
	}	
};
