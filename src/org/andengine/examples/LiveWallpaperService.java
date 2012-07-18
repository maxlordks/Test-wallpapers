package org.andengine.examples;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.extension.ui.livewallpaper.BaseLiveWallpaperService;


public class LiveWallpaperService extends BaseLiveWallpaperService {

	private ScreenOrientation mScreenOrientation;
	private static final int CAMERA_WIDTH = 512;
	private static final int CAMERA_HEIGHT = 768;
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		return new EngineOptions(true, this.mScreenOrientation, new FillResolutionPolicy(), new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT));
	}

	@Override
	public void onCreateResources(
			OnCreateResourcesCallback pOnCreateResourcesCallback)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPopulateScene(Scene pScene,
			OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
