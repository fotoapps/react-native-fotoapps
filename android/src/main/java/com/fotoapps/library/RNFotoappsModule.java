
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

import org.xdump.android.zinnia.Zinnia;

public class RNFotoappsModule extends ReactContextBaseJavaModule {

	private final ReactApplicationContext ctx;
	private final FirebaseVisionTextRecognizer detector;

	private static String DATA_PATH = Environment.getExternalStorageDirectory().toString() + File.separator;
	private static final String OCRMODELS = "ocrmodels";
	private static final String JAMODEL = "handwriting-ja.model";
	private final Zinnia zinnia = new Zinnia();

	@VisibleForTesting
	private static final String REACT_CLASS = "RNFotoapps";

	public RNFotoappsModule(ReactApplicationContext reactContext) {
		super(reactContext);
		ctx = reactContext;
		detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
		this.DATA_PATH = reactContext.getFilesDir().getAbsolutePath() + File.separator;

		prepareZinnia();
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
		// try {
		    // FirebaseVisionImage image = FirebaseVisionImage.fromFilePath(ctx, Uri.fromFile(new File(path)));
		    // detector.processImage(image)
		    //     .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
		    //         @Override
		    //         public void onSuccess(FirebaseVisionText result) {
		    //             String resultText = result.getText();
		    //             promise.resolve(resultText);
		    //         }
		    //     })
		    //     .addOnFailureListener(new OnFailureListener() {
		    //         @Override
		    //         public void onFailure(@NonNull Exception e) {
		    //             promise.reject("An error occurred", e.getMessage());
		    //         }
		    //     });
		    long recognizer = zinnia.zinnia_recognizer_new();
		    // if (recognizer <= 0) {
		    // 	promise.resolve("e1");
		    // 	return;
		    // }
		    if (zinnia.zinnia_recognizer_open(recognizer, DATA_PATH + OCRMODELS + File.separator + JAMODEL) > 0) {
		    	long character = zinnia.zinnia_character_new();
		    	// test
		    	zinnia.zinnia_character_clear(character);
		    	zinnia.zinnia_character_set_width(character, 300);
		    	zinnia.zinnia_character_set_height(character, 300);
		    	zinnia.zinnia_character_add(character, 0, 51, 29);
				zinnia.zinnia_character_add(character, 0, 117, 41);
				zinnia.zinnia_character_add(character, 1, 99, 65);
				zinnia.zinnia_character_add(character, 1, 219, 77);
				zinnia.zinnia_character_add(character, 2, 27, 131);
				zinnia.zinnia_character_add(character, 2, 261, 131);
				zinnia.zinnia_character_add(character, 3, 129, 17);
				zinnia.zinnia_character_add(character, 3, 57, 203);
				zinnia.zinnia_character_add(character, 4, 111, 71);
				zinnia.zinnia_character_add(character, 4, 219, 173);
				zinnia.zinnia_character_add(character, 5, 81, 161);
				zinnia.zinnia_character_add(character, 5, 93, 281);
				zinnia.zinnia_character_add(character, 6, 99, 167);
				zinnia.zinnia_character_add(character, 6, 207, 167);
				zinnia.zinnia_character_add(character, 6, 189, 245);
				zinnia.zinnia_character_add(character, 7, 99, 227);
				zinnia.zinnia_character_add(character, 7, 189, 227);
				zinnia.zinnia_character_add(character, 8, 111, 257);
				zinnia.zinnia_character_add(character, 8, 189, 245);

				long result = zinnia.zinnia_recognizer_classify(recognizer, character, 10);

				Log.d(REACT_CLASS, "---- Zinnia error: " + zinnia.zinnia_recognizer_strerror(recognizer));
				long resultSize = zinnia.zinnia_result_size(result);
				String recognizedResult = "";
				for (int i = 0; i < resultSize; ++i) {
					recognizedResult += zinnia.zinnia_result_value(result, i);
					if (i < resultSize - 1) {
						recognizedResult += ",";
					}
				}

				zinnia.zinnia_result_destroy(result);
				zinnia.zinnia_character_destroy(character);

				promise.resolve(recognizedResult);
		    } else {
		    	promise.reject("An error occurred", "Cannot open recognizer model");
		    }
		    zinnia.zinnia_recognizer_destroy(recognizer);
		// } catch (IOException e) {
		//     e.printStackTrace();
		//     promise.reject("An error occurred", e.getMessage());
		// }
	}

	private String extractText(String path) {		 
		return "A";
	}

	private void prepareDirectory(String path) {
		File dir = new File(path);
		if (!dir.exists()) {
			if (!dir.mkdirs()) {
				Log.e(REACT_CLASS, "ERROR: Creation of directory " + path
						+ " failed, check permission to write to external storage.");
			}
		} else {
			Log.i(REACT_CLASS, "Created directory " + path);
		}
	}

	private void prepareZinnia() {
		Log.d(REACT_CLASS, "Preparing zinnia enviroment");

		try {
			prepareDirectory(DATA_PATH + OCRMODELS);
		} catch (Exception e) {
			e.printStackTrace();
		}

		copyZinniaModelFiles(OCRMODELS);
	}

	private void copyZinniaModelFiles(String path) {
		try {
			String fileList[] = ctx.getAssets().list(path);

			for (String fileName : fileList) {

				String pathToDataFile = DATA_PATH + path + File.separator + fileName;
				if (!(new File(pathToDataFile)).exists()) {

					InputStream in = ctx.getAssets().open(path + File.separator + fileName);

					OutputStream out = new FileOutputStream(pathToDataFile);

					byte[] buf = new byte[1024];
					int len;

					while ((len = in.read(buf)) > 0) {
						out.write(buf, 0, len);
					}
					in.close();
					out.close();

					Log.d(REACT_CLASS, "Copied " + fileName + "to ocrmodels");
				}
			}
		} catch (IOException e) {
			Log.e(REACT_CLASS, "Unable to copy files to ocrmodels " + e.toString());
		}
	}
}
