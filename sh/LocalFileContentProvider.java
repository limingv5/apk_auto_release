package com.nmi.__BContapk__;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.net.URI;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.res.AssetManager;
import android.content.res.AssetFileDescriptor;
import android.os.ParcelFileDescriptor;
import android.database.Cursor;
import android.net.Uri;


public class LocalFileContentProvider extends ContentProvider {

	private static final String URI_PREFIX = "content://ui.localfile";

	public static String constructUri(String url) {
		Uri uri = Uri.parse(url);
		return uri.isAbsolute() ? url : URI_PREFIX + url;
	}

	@Override
	public AssetFileDescriptor openAssetFile(Uri uri, String mode) throws FileNotFoundException {
		AssetManager am = getContext().getAssets();
		String path = uri.getPath().substring(1);
		try {
			AssetFileDescriptor afd = am.openFd(path);
			return afd;
		}
		catch (IOException e) {}
		return super.openAssetFile(uri, mode);
	}
	
	@Override
	public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
		URI fileURI = URI.create("file://" + uri.getPath());
		File file = new File(fileURI);

		ParcelFileDescriptor parcel = null;
		try {
			parcel = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
		}
		catch (FileNotFoundException e) {}

		return parcel;
	}

	@Override
	public boolean onCreate() {
		return true;
	}

	@Override
	public int delete(Uri uri, String s, String[] as) {
		throw new UnsupportedOperationException("Not supported by this provider");
	}

	@Override
	public String getType(Uri uri) {
		throw new UnsupportedOperationException("Not supported by this provider");
	}

	@Override
	public Uri insert(Uri uri, ContentValues contentvalues) {
		throw new UnsupportedOperationException("Not supported by this provider");
	}

	@Override
	public Cursor query(Uri uri, String[] as, String s, String[] as1, String s1) {
		throw new UnsupportedOperationException("Not supported by this provider");
	}

	@Override
	public int update(Uri uri, ContentValues contentvalues, String s,
			String[] as) {
		throw new UnsupportedOperationException("Not supported by this provider");
	}
}
