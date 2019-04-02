
package com.fotoapps.library;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.net.Uri;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.common.annotations.VisibleForTesting;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class RNFotoappsModule extends ReactContextBaseJavaModule {

	private final ReactApplicationContext ctx;
	private final FirebaseVisionTextRecognizer detector;

	@VisibleForTesting
	private static final String REACT_CLASS = "RNFotoapps";

	public RNFotoappsModule(ReactApplicationContext reactContext) {
		super(reactContext);
		ctx = reactContext;
		detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
	}

	@Override
	public String getName() {
		return REACT_CLASS;
	}

	@Override
	public Map<String, Object> getConstants() {
		final Map<String, Object> constants = new HashMap<>();

		return constants;
	}

	@ReactMethod
	public void recognize(String path, final Promise promise) {
		Log.d(REACT_CLASS, "Start ocr images");
		try {
		    FirebaseVisionImage image = FirebaseVisionImage.fromFilePath(ctx, Uri.fromFile(new File(path)));
		    detector.processImage(image)
		        .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
		            @Override
		            public void onSuccess(FirebaseVisionText result) {
		                String resultText = result.getText();
		                promise.resolve(resultText);
		            }
		        })
		        .addOnFailureListener(new OnFailureListener() {
		            @Override
		            public void onFailure(@NonNull Exception e) {
		                promise.reject("An error occurred", e.getMessage());
		            }
		        });
		} catch (IOException e) {
		    e.printStackTrace();
		    promise.reject("An error occurred", e.getMessage());
		}
	}

	private String extractText(String path) {		 
		return "A";
	}
}
