package gotitapi.haro_scoutar.ar

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.PixelCopy
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.AugmentedFace
import com.google.ar.sceneform.ArSceneView
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.*
import com.google.ar.sceneform.ux.AugmentedFaceNode
import gotitapi.haro_scoutar.R
import gotitapi.haro_scoutar.api.HttpUtil
import kotlinx.android.synthetic.main.activity_solar.*
import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException

class ArActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    companion object {
        private const val RC_PERMISSIONS = 0x123

        // Astronomical units to meters ratio. Used for positioning the planets of the solar system.
        private const val AU_TO_METERS = 0.5f
    }

    private var arSceneView: ArSceneView? = null

    private var iconRenderable: ModelRenderable? = null
    private var loadingRenderable: ModelRenderable? = null

    private var earthRenderable: ModelRenderable? = null
    private var gopherRenderable: ModelRenderable? = null
    private var kotlinRenderable: ModelRenderable? = null
    private var pythonRenderable: ModelRenderable? = null
    private var slacaRenderable: ModelRenderable? = null
    private var tsRenderable: ModelRenderable? = null

    private var cppRenderable: ModelRenderable? = null
    private var csRenderable: ModelRenderable? = null
    private var dRenderable: ModelRenderable? = null
    private var haskellRenderable: ModelRenderable? = null
    private var htmlRenderable: ModelRenderable? = null
    private var javascriptRenderable: ModelRenderable? = null
    private var perlRenerable: ModelRenderable? = null
    private var phpRenderable: ModelRenderable? = null
    private var rubyRenderable: ModelRenderable? = null
    private var rustRenderable: ModelRenderable? = null
    private var swiftRenderable: ModelRenderable? = null
    private var javaRenderable: ModelRenderable? = null
    private var notLoading = true

    private val solarSettings = SolarSettings()

    // True once scene is loaded
    private var hasFinishedLoading = false

    private val languageMap = HashMap<String, ModelRenderable>()
    private var useFaceNode: Node? = null
    private var arFragment: FaceArFragment? = null

    private var instance: ArActivity? = null

    override
    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!DemoUtils.checkIsSupportedDeviceOrFinish(this)) {
            return
        }

        setContentView(R.layout.activity_solar)
        arFragment = supportFragmentManager.findFragmentById(R.id.face_fragment) as FaceArFragment?

        arSceneView = arFragment!!.arSceneView

        Texture.builder().setSource(this, R.drawable.kotlin).build().thenAccept { texture ->
            MaterialFactory.makeTransparentWithTexture(this, texture).thenAccept { material ->
                kotlinRenderable =
                    ShapeFactory.makeCube(
                        Vector3(0.5f, 0.5f, 0.5f).scaled(1.0f),
                        Vector3(0.0f, 0.15f, 0.0f),
                        material
                    )
            }
        }
        Texture.builder().setSource(this, R.drawable.python).build().thenAccept { texture ->
            MaterialFactory.makeTransparentWithTexture(this, texture).thenAccept { material ->
                pythonRenderable =
                    ShapeFactory.makeCube(
                        Vector3(0.5f, 0.5f, 0.5f).scaled(1.0f),
                        Vector3(0.0f, 0.15f, 0.0f),
                        material
                    )
            }
        }
        Texture.builder().setSource(this, R.drawable.scala).build().thenAccept { texture ->
            MaterialFactory.makeTransparentWithTexture(this, texture).thenAccept { material ->
                slacaRenderable = ShapeFactory.makeCube(
                    Vector3(0.5f, 0.5f, 0.5f).scaled(1.0f),
                    Vector3(0.0f, 0.15f, 0.0f),
                    material
                )
            }
        }
        Texture.builder().setSource(this, R.drawable.loading).build().thenAccept { texture ->
            MaterialFactory.makeTransparentWithTexture(this, texture).thenAccept { material ->
                slacaRenderable = ShapeFactory.makeCube(
                    Vector3(0.5f, 0.5f, 0.5f).scaled(1.0f),
                    Vector3(0.0f, 0.15f, 0.0f),
                    material
                )
            }
        }

        Texture.builder().setSource(this, R.drawable.cpp).build().thenAccept { texture ->
            MaterialFactory.makeTransparentWithTexture(this, texture).thenAccept { material ->
                cppRenderable = ShapeFactory.makeCube(
                    Vector3(0.5f, 0.5f, 0.5f).scaled(1.0f),
                    Vector3(0.0f, 0.15f, 0.0f),
                    material
                )
            }
        }
        Texture.builder().setSource(this, R.drawable.cs).build().thenAccept { texture ->
            MaterialFactory.makeTransparentWithTexture(this, texture).thenAccept { material ->
                csRenderable = ShapeFactory.makeCube(
                    Vector3(0.5f, 0.5f, 0.5f).scaled(1.0f),
                    Vector3(0.0f, 0.15f, 0.0f),
                    material
                )
            }
        }
        Texture.builder().setSource(this, R.drawable.dman).build().thenAccept { texture ->
            MaterialFactory.makeTransparentWithTexture(this, texture).thenAccept { material ->
                dRenderable = ShapeFactory.makeCube(
                    Vector3(0.5f, 0.5f, 0.5f).scaled(1.0f),
                    Vector3(0.0f, 0.15f, 0.0f),
                    material
                )
            }
        }
        Texture.builder().setSource(this, R.drawable.haskell).build().thenAccept { texture ->
            MaterialFactory.makeTransparentWithTexture(this, texture).thenAccept { material ->
                haskellRenderable = ShapeFactory.makeCube(
                    Vector3(0.5f, 0.5f, 0.5f).scaled(1.0f),
                    Vector3(0.0f, 0.15f, 0.0f),
                    material
                )
            }
        }
        Texture.builder().setSource(this, R.drawable.html).build().thenAccept { texture ->
            MaterialFactory.makeTransparentWithTexture(this, texture).thenAccept { material ->
                htmlRenderable = ShapeFactory.makeCube(
                    Vector3(0.5f, 0.5f, 0.5f).scaled(1.0f),
                    Vector3(0.0f, 0.15f, 0.0f),
                    material
                )
            }
        }
        Texture.builder().setSource(this, R.drawable.javascript).build().thenAccept { texture ->
            MaterialFactory.makeTransparentWithTexture(this, texture).thenAccept { material ->
                javascriptRenderable = ShapeFactory.makeCube(
                    Vector3(0.5f, 0.5f, 0.5f).scaled(1.0f),
                    Vector3(0.0f, 0.15f, 0.0f),
                    material
                )
            }
        }
        Texture.builder().setSource(this, R.drawable.perl).build().thenAccept { texture ->
            MaterialFactory.makeTransparentWithTexture(this, texture).thenAccept { material ->
                perlRenerable = ShapeFactory.makeCube(
                    Vector3(0.5f, 0.5f, 0.5f).scaled(1.0f),
                    Vector3(0.0f, 0.15f, 0.0f),
                    material
                )
            }
        }
        Texture.builder().setSource(this, R.drawable.php).build().thenAccept { texture ->
            MaterialFactory.makeTransparentWithTexture(this, texture).thenAccept { material ->
                phpRenderable = ShapeFactory.makeCube(
                    Vector3(0.5f, 0.5f, 0.5f).scaled(1.0f),
                    Vector3(0.0f, 0.15f, 0.0f),
                    material
                )
            }
        }
        Texture.builder().setSource(this, R.drawable.ruby).build().thenAccept { texture ->
            MaterialFactory.makeTransparentWithTexture(this, texture).thenAccept { material ->
                rubyRenderable = ShapeFactory.makeCube(
                    Vector3(0.5f, 0.5f, 0.5f).scaled(1.0f),
                    Vector3(0.0f, 0.15f, 0.0f),
                    material
                )
            }
        }

        Texture.builder().setSource(this, R.drawable.rust).build().thenAccept { texture ->
            MaterialFactory.makeTransparentWithTexture(this, texture).thenAccept { material ->
                rustRenderable = ShapeFactory.makeCube(
                    Vector3(0.5f, 0.5f, 0.5f).scaled(1.0f),
                    Vector3(0.0f, 0.15f, 0.0f),
                    material
                )
            }
        }
        Texture.builder().setSource(this, R.drawable.swift).build().thenAccept { texture ->
            MaterialFactory.makeTransparentWithTexture(this, texture).thenAccept { material ->
                swiftRenderable = ShapeFactory.makeCube(
                    Vector3(0.5f, 0.5f, 0.5f).scaled(1.0f),
                    Vector3(0.0f, 0.15f, 0.0f),
                    material
                )
            }
        }
        Texture.builder().setSource(this, R.drawable.java).build().thenAccept { texture ->
            MaterialFactory.makeTransparentWithTexture(this, texture).thenAccept { material ->
                javaRenderable = ShapeFactory.makeCube(
                    Vector3(0.5f, 0.5f, 0.5f).scaled(1.0f),
                    Vector3(0.0f, 0.15f, 0.0f),
                    material
                )
            }
        }

        // Build all the planet models.
        val tsStage = ModelRenderable.builder().setSource(this, Uri.parse("ts.sfb")).build()
        val earthStage = ModelRenderable.builder().setSource(this, Uri.parse("Earth.sfb")).build()
        val lunaStage = ModelRenderable.builder().setSource(this, Uri.parse("Luna.sfb")).build()
        val gopherStage = ModelRenderable.builder().setSource(this, Uri.parse("gopher.sfb")).build()
        val loadingStage =
            ModelRenderable.builder().setSource(this, Uri.parse("loading.sfb")).build()

        CompletableFuture.allOf(
            earthStage,
            lunaStage,
            gopherStage,
            tsStage,
            loadingStage

        )
            .handle<Any> { _, throwable ->
                if (throwable != null) {
                    DemoUtils.displayError(this, "Unable to load renderable", throwable)
                    return@handle null
                }

                try {
                    earthRenderable = earthStage.get()
                    gopherRenderable = gopherStage.get()
                    tsRenderable = tsStage.get()
                    loadingRenderable = loadingStage.get()

                    // Everything finished loading successfully.
                    hasFinishedLoading = true

                    languageMap["Go"] = gopherRenderable!!
                    languageMap["earth"] = earthRenderable!!
                    languageMap["TypeScript"] = tsRenderable!!

                } catch (ex: InterruptedException) {
                    DemoUtils.displayError(this, "Unable to load renderable", ex)
                } catch (ex: ExecutionException) {
                    DemoUtils.displayError(this, "Unable to load renderable", ex)
                }

                languageMap["Kotlin"] = kotlinRenderable!!
                languageMap["Python"] = pythonRenderable!!
                languageMap["Scala"] = slacaRenderable!!
                languageMap["C"] = cppRenderable!!
                languageMap["C++"] = cppRenderable!!
                languageMap["C#"] = csRenderable!!
                languageMap["D"] = dRenderable!!
                languageMap["Haskel"] = haskellRenderable!!
                languageMap["HTML"] = htmlRenderable!!
                languageMap["Java"] = javaRenderable!!
                languageMap["JavaScript"] = javascriptRenderable!!
                languageMap["Perl"] = perlRenerable!!
                languageMap["PHP"] = phpRenderable!!
                languageMap["Ruby"] = rubyRenderable!!
                languageMap["Rust"] = rustRenderable!!
                languageMap["Swift"] = swiftRenderable!!
                languageMap["Jupyter Notebook"] = pythonRenderable!!
                languageMap["CMake"] = cppRenderable!!
                null
            }
        val scene = arSceneView!!.scene

        // This is important to make sure that the camera stream renders first so that
        // the face mesh occlusion works correctly.
        arSceneView!!.cameraStreamRenderPriority = Renderable.RENDER_PRIORITY_FIRST

        arSceneView!!
            .scene
            .addOnUpdateListener { _ ->
                if (earthRenderable == null) {
                    return@addOnUpdateListener
                }
                // Make new AugmentedFaceNodes for any new faces.
                val faceList = arSceneView!!.session!!.getAllTrackables(AugmentedFace::class.java)
                Log.d("haro_node", "ready_load")
                Log.d("haro_node", "num face " + faceList.size)
                if (faceList.isNotEmpty() && useFaceNode == null && notLoading) {
                    notLoading = false

                    val face = faceList.iterator().next()
                    val faceNode = AugmentedFaceNode(face)
                    faceNode.setParent(scene)
                    useFaceNode = faceNode

                    val bitmapOrigin = Bitmap.createBitmap(
                        arSceneView!!.width,
                        arSceneView!!.height,
                        Bitmap.Config.ARGB_8888
                    )
                    val bitmap = Bitmap.createScaledBitmap(
                        bitmapOrigin,
                        (bitmapOrigin.width * 0.3).toInt()
                    , (bitmapOrigin.height *0.3).toInt(), true)
                    PixelCopy.request(arSceneView!!, bitmap, { _ ->
                        val loading = Node()
                        loading.setParent(faceNode)
                        loading.renderable = loadingRenderable
                        loading.localScale = Vector3(3.0f, 3.0f, 3.0f)
                        loading.localPosition = Vector3(0.0f, 0.0f, 0.1f)
                        loading.localRotation = Quaternion.axisAngle(Vector3(1.0f, 0.0f, 0.0f), 90f)

                        launch(Dispatchers.Main) {
                            runCatching {
                                withContext(Dispatchers.IO) {
                                    HttpUtil.getMock(bitmap)
                                }
                            }.onSuccess {
                                Toast.makeText(instance, "認証に成功しました", Toast.LENGTH_LONG).show()
                                loading.setParent(null)

                                val languages = it.githubData.languageList
                                createFaceSystem(faceNode, languages)
                                faceNode.setParent(scene)

                                useFaceNode = faceNode
                                runCatching {
                                    withContext(Dispatchers.IO) {
                                        HttpUtil.getIcon(it.twitterData.imageUrl)
                                    }
                                }.onSuccess { bitmap ->
                                    Log.d("icon", "deteruyo")
                                    tramp_view.setImageBitmap(bitmap)
                                    Texture.builder().setSource(bitmap).build()
                                        .thenAccept { texture ->
                                            MaterialFactory.makeTransparentWithTexture(
                                                this@ArActivity,
                                                texture
                                            ).thenAccept { material ->
                                                iconRenderable = ShapeFactory.makeCube(
                                                    Vector3(0.5f, 0.5f, 0.5f).scaled(1.0f),
                                                    Vector3(0.0f, 0.15f, 0.0f),
                                                    material
                                                )
                                            }
                                        }
                                    val iconNode = Node()
                                    iconNode.setParent(faceNode)
                                    iconNode.renderable = iconRenderable
                                    iconNode.localScale = Vector3(0.1f, 0.1f, 0.1f)
                                    iconNode.localPosition = Vector3(0.0f, 0.1f, 0.1f)
                                }.onFailure {
                                    Log.d("icon", "detenaiyo")
                                }
                            }.onFailure {
                                Toast.makeText(instance, "認証に失敗しました", Toast.LENGTH_LONG).show()
                                loading.setParent(null)
                                val languages = listOf("Java", "Kotlin", "Java", "Kotlin", "Python")
                                createFaceSystem(faceNode, languages)
                                faceNode.setParent(scene)
                                useFaceNode = faceNode

                                Texture.builder().setSource(bitmap).build()
                                    .thenAccept { texture ->
                                        MaterialFactory.makeTransparentWithTexture(
                                            this@ArActivity,
                                            texture
                                        ).thenAccept { material ->
                                            iconRenderable = ShapeFactory.makeCube(
                                                Vector3(0.5f, 0.5f, 0.5f).scaled(1.0f),
                                                Vector3(0.0f, 0.15f, 0.0f),
                                                material
                                            )
                                            val iconNode = Node()
                                            iconNode.setParent(faceNode)
                                            iconNode.renderable = iconRenderable
                                            iconNode.localScale = Vector3(0.1f, 0.1f, 0.1f)
                                            iconNode.localPosition = Vector3(0.0f, 0.1f, 0.1f)
                                        }
                                    }
                            }
                        }

                    }, Handler())
                } else if (faceList.isEmpty() && useFaceNode != null) {
                    useFaceNode!!.setParent(null)
                    useFaceNode = null
                    notLoading = true
                }
            }
        DemoUtils.requestCameraPermission(this, RC_PERMISSIONS)
        instance = this
    }

    public override fun onPause() {
        super.onPause()
        if (arSceneView != null) {
            arSceneView!!.pause()
        }
    }

    public override fun onDestroy() {
        super.onDestroy()
        if (arSceneView != null) {
            arSceneView!!.destroy()
        }
    }

    private fun createFaceSystem(faceNode: AugmentedFaceNode, languages: List<String>): Node {
        val base = Node()
        base.setParent(faceNode)
        base.localPosition = Vector3(0.0f, 0.0f, 0.1f)

        val scale = 0.1
        var adder = 0.0
        for (language in languages) {
            createPlanet(
                language,
                base,
                (0.2f + adder * scale).toFloat(),
                (20f + adder * 5).toFloat(),
                languageMap[language],
                (0.015f * 5 - 0.001f * adder).toFloat(),
                0f
            )
            adder += 1.0
        }

        return faceNode
    }

    private fun createPlanet(
        name: String,
        parent: Node,
        auFromParent: Float,
        orbitDegreesPerSecond: Float,
        renderable: ModelRenderable?,
        planetScale: Float,
        axisTilt: Float
    ): Node {
        // Orbit is a rotating node with no renderable positioned at the sun.
        // The planet is positioned relative to the orbit so that it appears to rotate around the sun.
        // This is done instead of making the sun rotate so each planet can orbit at its own speed.
        val orbit = RotatingNode(solarSettings, true, false, 0f)
        orbit.setDegreesPerSecond(orbitDegreesPerSecond)
        orbit.setParent(parent)

        // Create the planet and position it relative to the sun.
        val planet = Planet(
            this, name, planetScale, orbitDegreesPerSecond, axisTilt, renderable, solarSettings
        )
        planet.setParent(orbit)
        planet.localPosition = Vector3(auFromParent * AU_TO_METERS, 0.0f, 0.0f)

        return planet
    }

}
