package com.maxlord.wallpapers.simple;

import java.io.IOException;
import java.io.InputStream;

import org.andengine.engine.camera.SmoothCamera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.ui.livewallpaper.BaseLiveWallpaperService;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.util.adt.io.in.IInputStreamOpener;
import org.andengine.util.debug.Debug;

import android.content.SharedPreferences;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

public class LiveWallpaperService extends BaseLiveWallpaperService implements SharedPreferences.OnSharedPreferenceChangeListener {
	private final String TAG = "LiveWallpaperService";

	public static final String SHARED_PREFS_NAME = "LiveWallpaperPreferences";
	private ScreenOrientation mScreenOrientation = ScreenOrientation.LANDSCAPE_FIXED;
	private static final int IMAGE_WIDTH = 1600;
	private static final int IMAGE_HEIGHT = 1200;
	private SmoothCamera camera;
	private ITexture mTexture;
	private ITextureRegion mBackgroundTextureRegion;
	private BuildableBitmapTextureAtlas mBitmapTextureAtlas;
	private TiledTextureRegion mStarTextureRegion;

	private Scene scene;

	private Sprite backgroundSprite;

	@Override
	public EngineOptions onCreateEngineOptions() {
		Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
		int mCameraWidth = display.getWidth();
		int mCameraHeight = display.getHeight();
		RatioResolutionPolicy ratio;
		int rotation = display.getRotation();
		if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
			mScreenOrientation = ScreenOrientation.LANDSCAPE_FIXED;
		} else {
			mScreenOrientation = ScreenOrientation.PORTRAIT_FIXED;
		}
		ratio = new RatioResolutionPolicy(IMAGE_WIDTH, IMAGE_HEIGHT);
		this.camera = new SmoothCamera(0, 0, mCameraWidth, mCameraHeight, 10, 10, 0.5f);
		camera.setCenterDirect(IMAGE_WIDTH / 2, IMAGE_HEIGHT / 2);
		camera.setResizeOnSurfaceSizeChanged(true);
		return new EngineOptions(true, this.mScreenOrientation, ratio, camera);
	}

	@Override
	public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) throws Exception {
		Log.i(TAG, "onCreateResources");
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		try {
			this.mBitmapTextureAtlas = new BuildableBitmapTextureAtlas(this.getTextureManager(), 512, 256, TextureOptions.NEAREST);
			this.mTexture = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
				@Override
				public InputStream open() throws IOException {
					return getAssets().open("gfx/space.jpg");
				}
			});
			this.mStarTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "star.png", 2, 1);
			this.mBitmapTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 1));
			this.mBitmapTextureAtlas.load();
			this.mTexture.load();
			this.mBackgroundTextureRegion = TextureRegionFactory.extractFromTexture(this.mTexture);
			updateScale();
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
		backgroundSprite = new Sprite(0, 0, this.mBackgroundTextureRegion, this.getVertexBufferObjectManager());
		scene.attachChild(backgroundSprite);
		AnimatedSprite star = new AnimatedSprite(100, 50, this.mStarTextureRegion, this.getVertexBufferObjectManager());
		star.animate(100);
		scene.attachChild(star);
		//lines();
		pOnCreateSceneCallback.onCreateSceneFinished(scene);
	}

	private void lines() {
		final Line line = new Line(0, 624, IMAGE_WIDTH, 624, 5, this.getVertexBufferObjectManager());
		scene.attachChild(line);
		final Line line2 = new Line(0, 721, IMAGE_WIDTH, 721, 10, this.getVertexBufferObjectManager());
		scene.attachChild(line2);
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
		updateScale();
		Debug.i(String.format("Camera surface position: %d, %d; size %d, %d", camera.getSurfaceX(), camera.getSurfaceY(), camera.getSurfaceWidth(),
				camera.getSurfaceHeight()));
		Debug.i(String.format("Camera xmin max: %f, %f; ymin, max %f, %f", camera.getXMin(), camera.getXMax(), camera.getYMin(), camera.getYMax()));
	}

	private void updateScale() {
		float scale = 1;
		if (!canFillWidth(camera.getWidth(), camera.getHeight(), IMAGE_WIDTH, IMAGE_HEIGHT)) {
			scale = camera.getWidthRaw() / IMAGE_WIDTH;
			Debug.i("Fill width, scale = " + scale);
		} else {
			scale = camera.getHeightRaw() / IMAGE_HEIGHT;
			Debug.i("Fill height, scale = " + scale);
		}
		camera.setZoomFactorDirect(scale);
	}

	private boolean canFillWidth(float containerW, float containerH, float w, float h) {
		return containerW / w < containerH / h;
	}

}
