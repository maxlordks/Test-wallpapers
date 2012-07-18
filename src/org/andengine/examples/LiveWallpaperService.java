package org.andengine.examples;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.IBackground;
import org.andengine.extension.ui.livewallpaper.BaseLiveWallpaperService;

import android.content.SharedPreferences;
import android.util.Log;

public class LiveWallpaperService extends BaseLiveWallpaperService implements SharedPreferences.OnSharedPreferenceChangeListener {

	
	public static final String SHARED_PREFS_NAME = "livewallpapertemplatesettings"; 
	private ScreenOrientation mScreenOrientation;
	private static final int CAMERA_WIDTH = 512;
	private static final int CAMERA_HEIGHT = 768;

	@Override
	public EngineOptions onCreateEngineOptions() {
		return new EngineOptions(true, this.mScreenOrientation, new FillResolutionPolicy(), new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT));
	}

	@Override
	public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) throws Exception {
		Log.i("AAAA", "onCreateResources");
		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws Exception {
		Log.i("AAAA", "onCreateScene");
		final Scene scene = new Scene(1);
		IBackground b = new Background(1, 0, 0);
		scene.setBackground(b);
		pOnCreateSceneCallback.onCreateSceneFinished(scene);
	}

	@Override
	public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {
		Log.i("AAAA", "onPopulateScene");
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

}
