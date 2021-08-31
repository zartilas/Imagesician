/*
package unipi.p17168.imagesician.utils

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage


class QrRecognizing : ImageAnalysis.Analyzer {


    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_QR_CODE,
            Barcode.FORMAT_AZTEC)
        .build()

    var listWithResult = arrayListOf<String>()

//    var typeOfQR: Boolean? = null

    private val scanner = BarcodeScanning.getClient(options)

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {

        val mediaImage = imageProxy.image
        if (mediaImage != null) {

        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        scanner.process(image).addOnSuccessListener { barcodes ->
                // Task completed successfully
                for (barcode in barcodes) {
                    // barcodeListener( barcode.rawValue)
                    val bounds = barcode.boundingBox
                    val corners = barcode.cornerPoints
                    val rawValue = barcode.rawValue
                    Log.wtf("MyApp 69:","I AM HERE BOSS !!")
                    Log.e("bounds:","$bounds")
                    Log.e("corners:","$corners")
                    Log.wtf("rawValue:",rawValue)
                    // See API reference for complete list of supported types
                    when (barcode.valueType) {
                        Barcode.TYPE_WIFI -> {
                            val ssid = barcode.wifi!!.ssid
                            val password = barcode.wifi!!.password
                            val type = barcode.wifi!!.encryptionType
                            Log.wtf("test:","I AM WIFI BOSS")
//                            typeOfQR = true
                            listWithResult.add(ssid!!)
                            listWithResult.add(password!!)
                            listWithResult.add(type.toString())
                        }
                        Barcode.TYPE_URL -> {
                            Log.wtf("test:","I AM URL BOSS")
                            val title = barcode.url!!.title
                            val url = barcode.url!!.url
//                            typeOfQR = false
                            listWithResult.add(title!!)
                            listWithResult.add(url!!)

                            Log.e("My URL IS HERE BOSS:",url)
                        }
                        Barcode.FORMAT_ALL_FORMATS->{
//                            typeOfQR = null
                            val value = barcode.displayValue
                            listWithResult.add(value!!)
                            Log.wtf("test:",value)

                        }
                    }
                }
            }.addOnFailureListener {
                // Task failed with an exception
            }.addOnCompleteListener {
                // It's important to close the imageProxy
                imageProxy.close()
            }
        }
    }
}
*/
