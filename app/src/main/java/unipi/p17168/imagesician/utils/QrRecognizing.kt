package unipi.p17168.imagesician.utils


import android.annotation.SuppressLint
import android.media.Image
import android.os.Bundle
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage


class QrRecognizing : ImageAnalysis.Analyzer {

    private var options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .build()
    var barcodeScanner = BarcodeScanning.getClient(options)

    var listWithResult = arrayListOf<String>()

//    var typeOfQR: Boolean? = null

    override fun analyze(imageProxy: ImageProxy) {
        @SuppressLint("UnsafeOptInUsageError")
        val mediaImage: Image? = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            Log.wtf("MyApp 636", "I am here boss")
            barcodeScanner.process(image).addOnSuccessListener {  barcodes ->
                Log.wtf("MyApp 1312", "I am here boss")

                if (barcodes != null && barcodes.isNotEmpty()) {

                    Log.wtf("MyApp 1000", "I am here boss")
                    for (barcode in barcodes) {
                        Log.wtf("MyApp 655", "I am here boss")
                        Log.wtf("My code", barcode.rawValue)

                        when (barcode.valueType) {
                            Barcode.TYPE_WIFI -> {
                                val ssid = barcode.wifi!!.ssid
                                val password = barcode.wifi!!.password
                                val type = barcode.wifi!!.encryptionType
                                Log.wtf("test:", "I AM WIFI BOSS")
                            }
                            Barcode.TYPE_URL -> {
                                Log.wtf("test:", "I AM URL BOSS")
                                val title = barcode.url!!.title
                                val url = barcode.url!!.url
                                Log.e("My URL IS HERE BOSS:", url)
                            }
                        }
                    }
                }
            }
                .addOnFailureListener {}
                .addOnCompleteListener {
                    // It's important to close the imageProxy
                    imageProxy.close()
                }
        }
    }

}
