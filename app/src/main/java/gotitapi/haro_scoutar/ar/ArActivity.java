package gotitapi.haro_scoutar.ar;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.PixelCopy;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.ar.core.AugmentedFace;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.google.ar.sceneform.rendering.Texture;
import com.google.ar.sceneform.ux.AugmentedFaceNode;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import gotitapi.haro_scoutar.R;
import gotitapi.haro_scoutar.api.HttpUtil;
import gotitapi.haro_scoutar.data.ResponseData;
import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class ArActivity extends AppCompatActivity {


    private static final int RC_PERMISSIONS = 0x123;
    private boolean installRequested;

    private GestureDetector gestureDetector;
    private Snackbar loadingMessageSnackbar = null;

    private ArSceneView arSceneView;

    private ModelRenderable iconRenderable;

    private ModelRenderable earthRenderable;
    private ModelRenderable gopherRenderable;
    private ModelRenderable kotlinRenderable;
    private ModelRenderable pythonRenderable;
    private ModelRenderable slacaRenderable;
    private ModelRenderable tsRenderable;
    //    private ViewRenderable solarControlsRenderable;
    //    private ViewRenderable solarControlsRenderable;
    private boolean notLoading = true;

    private Texture faceMeshTexture;

    private final SolarSettings solarSettings = new SolarSettings();

    // True once scene is loaded
    private boolean hasFinishedLoading = false;

    // True once the scene has been placed.
    private boolean hasPlacedSolarSystem = false;

    // Astronomical units to meters ratio. Used for positioning the planets of the solar system.
    private static final float AU_TO_METERS = 0.5f;

    private final Map<String, ModelRenderable> languageMap = new HashMap<>();
    private Node useFaceNode = null;
    private FaceArFragment arFragment;
    private Bitmap icon;

    private ArActivity instance;

    @Override
    @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
    // CompletableFuture requires api level 24
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!DemoUtils.checkIsSupportedDeviceOrFinish(this)) {
            // Not a supported device.
            return;
        }

        setContentView(R.layout.activity_solar);
        arFragment = (FaceArFragment) getSupportFragmentManager().findFragmentById(R.id.face_fragment);

        arSceneView = arFragment.getArSceneView();

        Texture.builder().setSource(this, R.drawable.kotlin).build().thenAccept(
                texture -> MaterialFactory.makeTransparentWithTexture(this, texture).thenAccept(
                        material -> {
                            kotlinRenderable =
//                                  ShapeFactory.makeSphere(0.1f, new Vector3(0.0f, 0.15f, 0.0f), material);
                                    ShapeFactory.makeCube(new Vector3(0.5f, 0.5f, 0.5f).scaled(1.0f), new Vector3(0.0f, 0.15f, 0.0f), material);
                        })
        );
        Texture.builder().setSource(this, R.drawable.python).build().thenAccept(
                texture -> MaterialFactory.makeTransparentWithTexture(this, texture).thenAccept(
                        material -> {
                            pythonRenderable =
//                                  ShapeFactory.makeSphere(0.1f, new Vector3(0.0f, 0.15f, 0.0f), material);
                                    ShapeFactory.makeCube(new Vector3(0.5f, 0.5f, 0.5f).scaled(1.0f), new Vector3(0.0f, 0.15f, 0.0f), material);
                        })
        );
        Texture.builder().setSource(this, R.drawable.scala).build().thenAccept(
                texture -> MaterialFactory.makeTransparentWithTexture(this, texture).thenAccept(
                        material -> {
                            slacaRenderable =
//                                  ShapeFactory.makeSphere(0.1f, new Vector3(0.0f, 0.15f, 0.0f), material);
                                    ShapeFactory.makeCube(new Vector3(0.5f, 0.5f, 0.5f).scaled(1.0f), new Vector3(0.0f, 0.15f, 0.0f), material);
                        })
        );

        // Build all the planet models.

        CompletableFuture<ModelRenderable> tsStage =
                ModelRenderable.builder().setSource(this, Uri.parse("ts.sfb")).build();
        CompletableFuture<ModelRenderable> earthStage =
                ModelRenderable.builder().setSource(this, Uri.parse("Earth.sfb")).build();
        CompletableFuture<ModelRenderable> lunaStage =
                ModelRenderable.builder().setSource(this, Uri.parse("Luna.sfb")).build();
        CompletableFuture<ModelRenderable> gopherStage =
                ModelRenderable.builder().setSource(this, Uri.parse("gopher.sfb")).build();


//        // Build a renderable from a 2D View.
//        CompletableFuture<ViewRenderable> solarControlsStage =
//                ViewRenderable.builder().setView(this, R.layout.solar_controls).build();

        CompletableFuture.allOf(
                earthStage,
                lunaStage,
                gopherStage,
                tsStage

        )
                .handle(
                        (notUsed, throwable) -> {
                            // When you build a Renderable, Sceneform loads its resources in the background while
                            // returning a CompletableFuture. Call handle(), thenAccept(), or check isDone()
                            // before calling get().

                            if (throwable != null) {
                                DemoUtils.displayError(this, "Unable to load renderable", throwable);
                                return null;
                            }

                            try {
                                earthRenderable = earthStage.get();
                                gopherRenderable = gopherStage.get();
                                tsRenderable = tsStage.get();

                                // Everything finished loading successfully.
                                hasFinishedLoading = true;

                                languageMap.put("go", gopherRenderable);
                                languageMap.put("earth", earthRenderable);
                                languageMap.put("ts", tsRenderable);

                            } catch (InterruptedException | ExecutionException ex) {
                                DemoUtils.displayError(this, "Unable to load renderable", ex);
                            }

                            languageMap.put("kotlin", kotlinRenderable);
                            languageMap.put("python", pythonRenderable);
                            languageMap.put("scala", slacaRenderable);
                            return null;
                        });


        Scene scene = arSceneView.getScene();

        // This is important to make sure that the camera stream renders first so that
        // the face mesh occlusion works correctly.
        arSceneView.setCameraStreamRenderPriority(Renderable.RENDER_PRIORITY_FIRST);
        // Set up a tap gesture detector.
        gestureDetector =
                new GestureDetector(
                        this,
                        new GestureDetector.SimpleOnGestureListener() {
                            @Override
                            public boolean onSingleTapUp(MotionEvent e) {
                                onSingleTap();
                                return true;
                            }

//                            @Override
//                            public boolean onDown(MotionEvent e) {
//                                return true;
//                            }
                        });

        // Set a touch listener on the Scene to listen for taps.

        arSceneView
                .getScene()
                .setOnTouchListener(
                        (HitTestResult hitTestResult, MotionEvent event) -> {
                            // If the solar system hasn't been placed yet, detect a tap and then check to see if
                            // the tap occurred on an ARCore plane to place the solar system.
//                            if (!hasPlacedSolarSystem) {
//                                return gestureDetector.onTouchEvent(event);
//                            }
                            onSingleTap();

                            // Otherwise return false so that the touch event can propagate to the scene.
                            return false;
                        });

        arSceneView
                .getScene()
                .addOnUpdateListener(
                        frameTime -> {
//                            if (loadingMessageSnackbar == null) {
//                                return;
//                            }
//                            if (earthRenderable == null || faceMeshTexture == null) {
//                                return;
//                            }
                            if (earthRenderable == null) {
                                return;
                            }


                            // Make new AugmentedFaceNodes for any new faces.
                            Collection<AugmentedFace> faceList =
                                    arSceneView.getSession().getAllTrackables(AugmentedFace.class);
                            Log.d("haro_node", "ready_load");
                            Log.d("haro_node", "num face " + faceList.size());
                            if (faceList.size() != 0 && useFaceNode == null && notLoading) {
                                notLoading = false;
                                final Bitmap bitmap = Bitmap.createBitmap(arSceneView.getWidth(), arSceneView.getHeight(), Bitmap.Config.ARGB_8888);
                                PixelCopy.request(arSceneView, bitmap, copyResult -> {
                                    Single.create((SingleOnSubscribe<String>) emitter -> {
                                        try {
                                            // 一個emitして完了
                                            emitter.onSuccess("Single Hello");
                                        } catch (Exception ex) {
                                            emitter.onError(ex);
                                        }
                                    }).subscribe(new DisposableSingleObserver<String>() {
                                        @Override
                                        public void onSuccess(String value) {
                                            // 一回呼ばれる
                                        }

                                        @Override
                                        public void onError(Throwable e) {

                                        }
                                    });
                                    Single.create(((SingleOnSubscribe<ResponseData>) emitter -> {
                                        try {
                                            ResponseData data = HttpUtil.INSTANCE.getProfile(bitmap);
                                            emitter.onSuccess(data);
                                        } catch (Throwable t) {
                                            emitter.onError(t);
                                        }
                                    }))
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(new DisposableSingleObserver<ResponseData>() {
                                                @Override
                                                public void onSuccess(ResponseData responseData) {
                                                    AugmentedFace face = faceList.iterator().next();

                                                    List languages = responseData.getGithubData().getLanguageList();

                                                    Node faceNode = createFaceSystem(face, languages);
                                                    faceNode.setParent(scene);
                                                    Bitmap icon = responseData.getTwitterData().getImage();

                                                    useFaceNode = faceNode;

                                                    Log.d("haro_node", "createNode");
                                                }

                                                @Override
                                                public void onError(Throwable e) {
//                                                    AugmentedFace face = faceList.iterator().next();
//                                                    List<String> languages = Arrays.asList("go");
//                                                    Node faceNode = createFaceSystem(face, languages);
//                                                    icon = HttpUtil.INSTANCE.getIcon("https://pbs.twimg.com/profile_images/874276197357596672/kUuht00m_400x400.jpg");
//                                                    faceNode.setParent(scene);
//
//                                                    Node iconNode = new Node();
//                                                    iconNode.setRenderable(iconRenderable);
//                                                    iconNode.setLocalPosition(new Vector3(0.0f, 0.1f, 0.1f));
//                                                    useFaceNode = faceNode;
                                                }
                                            });
                                }, new Handler());
//                                AugmentedFace face = faceList.iterator().next();
//                                List<String> languages = Arrays.asList("go", "earth", "kotlin");
//                                Node faceNode = createFaceSystem(face,languages);
//                                faceNode.setParent(scene);
////                                AugmentedFaceNode faceNodeTmp = new AugmentedFaceNode(face);
////                                faceNodeTmp.setParent(scene);
////                                faceNodeTmp.setFaceRegionsRenderable(earthRenderable);
////                                faceNodeTmp.setFaceMeshTexture(faceMeshTexture);
////                                faceNode = faceNodeTmp;
////                                Node solarSystem = createSolarSystem();
//                                useFaceNode = faceNode;
//
////                                if (languageMap.isEmpty()) {
////                                    createFaceSystem(faceNode);
////                                }
//
//                                Log.d("haro_node", "createNode");
                            } else if (faceList.size() == 0 && useFaceNode != null) {
                                useFaceNode.setParent(null);
                                useFaceNode = null;
                            }


//                            for (AugmentedFace face : faceList) {
//                                if (!faceNodeMap.containsKey(face)) {
//                                    tryPlaceFaceSystem(frameTime,);
//
//                                }
//                            }

                            // Remove any AugmentedFaceNodes associated with an AugmentedFace that stopped tracking.
                            // 複数でやるとき
//                            Iterator<Map.Entry<AugmentedFace, AugmentedFaceNode>> iter =
//                                    faceNodeMap.entrySet().iterator();
//                            while (iter.hasNext()) {
//                                Map.Entry<AugmentedFace, AugmentedFaceNode> entry = iter.next();
//                                AugmentedFace face = entry.getKey();
//                                if (face.getTrackingState() == TrackingState.STOPPED) {
//                                    AugmentedFaceNode faceNode = entry.getValue();
//                                    faceNode.setParent(null);
//                                    iter.remove();
//                                }
//                            }

//                            Iterator<Map.Entry<AugmentedFace, Node>> iter = faceNodeMap.entrySet().iterator();
//                            if (iter.hasNext()) {
//                                Map.Entry<AugmentedFace, Node> faceNodeEntry = iter.next();
//                                AugmentedFace face = faceNodeEntry.getKey();
//                                if (face.getTrackingState() == TrackingState.STOPPED) {
//                                    Node faceNode = faceNodeEntry.getValue();
//                                    faceNode.setParent(null);
//                                    iter.remove();
////                                }
//                                }
//                            }

                            // Lastly request CAMERA permission which is required by ARCore.
//                            DemoUtils.requestCameraPermission(this, RC_PERMISSIONS);
                        });
        DemoUtils.requestCameraPermission(this, RC_PERMISSIONS);
        instance = this;
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (arSceneView != null) {
            arSceneView.pause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (arSceneView != null) {
            arSceneView.destroy();
        }
    }

//    private boolean tryPlaceSolarSystem(MotionEvent tap, Frame frame, AugmentedFace face) {
//        if (tap != null && face.getTrackingState() == TrackingState.TRACKING) {
//            for (HitResult hit : frame.hitTest(tap)) {
//                Trackable trackable = hit.getTrackable();
//                if (trackable instanceof Plane && ((Plane) trackable).isPoseInPolygon(hit.getHitPose())) {
//                    // Create the Anchor.
//                    Anchor anchor = hit.createAnchor();
//                    AnchorNode anchorNode = new AnchorNode(anchor);
//                    anchorNode.setParent(arSceneView.getScene());
//                    Node solarSystem = createSolarSystem();
//                    anchorNode.addChild(solarSystem);
//                    return true;
//                }
//            }
//        }
//
//        return false;
//    }

    private Node createFaceSystem(AugmentedFace face, List<String> languages) {
        AugmentedFaceNode faceNode = new AugmentedFaceNode(face);
        Node base = new Node();
        base.setParent((faceNode));
        base.setLocalPosition(new Vector3(0.0f, 0.0f, 0.1f));

        double scale = 0.1f;
        double adder = 0.0f;
        for (String language : languages) {
            createPlanet(language, base, (float) (0.2f + adder * scale), (float) (20f + adder), languageMap.get(language), (float) (0.015f * 5 - 0.001f * adder), 0);
            adder += 1;
        }

        return faceNode;
    }

    private Node createPlanet(
            String name,
            Node parent,
            float auFromParent,
            float orbitDegreesPerSecond,
            ModelRenderable renderable,
            float planetScale,
            float axisTilt) {
        // Orbit is a rotating node with no renderable positioned at the sun.
        // The planet is positioned relative to the orbit so that it appears to rotate around the sun.
        // This is done instead of making the sun rotate so each planet can orbit at its own speed.
        RotatingNode orbit = new RotatingNode(solarSettings, true, false, 0);
        orbit.setDegreesPerSecond(orbitDegreesPerSecond);
        orbit.setParent(parent);

        // Create the planet and position it relative to the sun.
        Planet planet =
                new Planet(
                        this, name, planetScale, orbitDegreesPerSecond, axisTilt, renderable, solarSettings);
        planet.setParent(orbit);
        planet.setLocalPosition(new Vector3(auFromParent * AU_TO_METERS, 0.0f, 0.0f));

        return planet;
    }

    private void showLoadingMessage() {
        if (loadingMessageSnackbar != null && loadingMessageSnackbar.isShownOrQueued()) {
            return;
        }

        loadingMessageSnackbar =
                Snackbar.make(
                        ArActivity.this.findViewById(android.R.id.content),
                        "message dayo",
                        Snackbar.LENGTH_INDEFINITE);
        loadingMessageSnackbar.getView().setBackgroundColor(0xbf323232);
        loadingMessageSnackbar.show();
    }

    private void hideLoadingMessage() {
        if (loadingMessageSnackbar == null) {
            return;
        }

        loadingMessageSnackbar.dismiss();
        loadingMessageSnackbar = null;
    }

    private void onSingleTap() {
        Log.d("onTap","tap!");
        if (icon != null) {
            Texture.builder().setSource(icon).build().thenAccept(
                    texture -> MaterialFactory.makeTransparentWithTexture(this, texture).thenAccept(
                            material -> {
                                iconRenderable =
                                        ShapeFactory.makeCube(new Vector3(0.5f, 0.5f, 0.5f).scaled(1.0f), new Vector3(0.0f, 0.15f, 0.0f), material);
                            })
            );
        }
    }

}
