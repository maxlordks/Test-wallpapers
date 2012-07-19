package com.maxlord.wallpapers.simple;

import java.io.IOException;
import java.io.InputStream;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.ui.livewallpaper.BaseLiveWallpaperService;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.util.adt.io.in.IInputStreamOpener;
import org.andengine.util.debug.Debug;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

public class LiveWallpaperService extends BaseLiveWallpaperService implements SharedPreferences.OnSharedPreferenceChangeListener {
	private final String TAG = "LiveWallpaperService";

	public static final String SHARED_PREFS_NAME = "LiveWallpaperPreferences";
	private ScreenOrientation mScreenOrientation = ScreenOrientation.LANDSCAPE_SENSOR;
	private Camera camera;
	private final static int IMAGE_WIDTH = 1600;
	private final static int IMAGE_HEIGHT = 1200;
	private ITexture mTexture;
	private ITextureRegion mFaceTextureRegion;

	private WindowManager window;

	private Scene scene;

	private Sprite backgroundSprite;

	@Override
	public EngineOptions onCreateEngineOptions() {
		window = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		camera = new Camera(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
		camera.setResizeOnSurfaceSizeChanged(true);
		return new EngineOptions(true, this.mScreenOrientation, new FillResolutionPolicy(), camera);
	}

	@Override
	public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) throws Exception {
		Log.i(TAG, "onCreateResources");
		try {
			this.mTexture = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
				@Override
				public InputStream open() throws IOException {
					return getAssets().open("gfx/wallpaper_1.jpg");
				}
			});

			this.mTexture.load();
			this.mFaceTextureRegion = TextureRegionFactory.extractFromTexture(this.mTexture);
		} catch (IOException e) {
			Debug.e(e);
		}

		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws Exception {
		Log.i(TAG, "onCreateScene");
		this.mEngine.registerUpdateHandler(new FPSLogger());

		scene = new Scene();
		scene.setBackground(new Background(0.09804f, 0.6274f, 0.8784f));

		/*
		 * Calculate the coordinates for the face, so its centered on the
		 * camera.
		 */
		backgroundSprite = new Sprite(0, 0, this.mFaceTextureRegion, this.getVertexBufferObjectManager());
		updateSpritePosition();
		scene.attachChild(backgroundSprite);
		final Line line = new Line(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT, 5, this.getVertexBufferObjectManager());
		scene.attachChild(line);
		final Line line2 = new Line(IMAGE_WIDTH, 0, 0, IMAGE_HEIGHT, 10, this.getVertexBufferObjectManager());
		scene.attachChild(line2);
		pOnCreateSceneCallback.onCreateSceneFinished(scene);
	}

	@Override
	public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {
		Log.i(TAG, "onPopulateScene");
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSurfaceChanged(org.andengine.opengl.util.GLState pGLState, int pWidth, int pHeight) {
		super.onSurfaceChanged(pGLState, pWidth, pHeight);
		updateSpritePosition();
		Debug.i(String.format("Camera surface position: %d, %d; size %d, %d", camera.getSurfaceX(), camera.getSurfaceY(), camera.getSurfaceWidth(),
				camera.getSurfaceHeight()));
		Debug.i(String.format("Camera xmin max: %f, %f; ymin, max %f, %f", camera.getXMin(), camera.getXMax(), camera.getYMin(), camera.getYMax()));
	}

	private void updateSpritePosition() {
		float width;
		float height;
		if (!canFillWidth(camera.getWidth(), camera.getHeight(), this.mFaceTextureRegion.getWidth(), this.mFaceTextureRegion.getHeight())) {
			width = camera.getWidth();
			height = (int) (this.mFaceTextureRegion.getHeight() * width / this.mFaceTextureRegion.getWidth());
			Debug.i("Fill width");
		} else {
			height = camera.getHeight();
			width = (int) (this.mFaceTextureRegion.getWidth() * height / this.mFaceTextureRegion.getHeight());
			Debug.i("Fill height");
		}
		Debug.i(String.format("Sprite size %f, %f", width, height));
		backgroundSprite.setSize(width, height);
		final float centerX = camera.getCenterX() - backgroundSprite.getWidth() / 2;
		final float centerY = camera.getCenterY() - backgroundSprite.getHeight() / 2;
		Debug.i(String.format("CenterX, centerY %f, %f, size: %f, %f", centerX, centerY, camera.getWidth(), camera.getHeight()));
		backgroundSprite.setPosition(centerX, centerY);

	}

	private boolean canFillWidth(float containerW, float containerH, float w, float h) {
		return containerW / w < containerH / h;
	}

}
