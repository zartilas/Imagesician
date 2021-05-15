package unipi.p17168.imagesician

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import com.google.mlkit.vision.objects.defaults.PredefinedCategory
import kotlinx.coroutines.selects.select
import unipi.p17168.imagesician.databinding.FragmentImageBinding
import java.io.IOException


class ImageFragment : Fragment() {

    //~~~~~~~VARIABLES~~~~~~~

    //var
    private var _binding : FragmentImageBinding ? = null
    private var imageIsText = false

    //val
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentImageBinding.inflate(inflater, container, false)
        val view = binding.root

        userTriggerButtons()

        return view
    }

    fun init(){

    }


    private fun userTriggerButtons() {
        binding.switchContextText.setOnClickListener{
            imageIsText = binding.switchContextText.isChecked
        }
        binding.floatingButton.setOnClickListener{
            pickImage() //custom fun
        }

    }
    private fun pickImage(){
        val openGalleryIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(openGalleryIntent, 666)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == 666) {
            if(resultCode == Activity.RESULT_OK)
            {
                val bitmap = getImage(data)
                if (bitmap != null) {
                  //  binding.imageView.setImageURI(Uri.parse("imageUri"))
                    binding.imageView.setImageBitmap(bitmap)
                    processImageTagging(bitmap)
                }

//                val bitmap = getImage(data)
//                    bitmap.apply {
//                        binding.imageView.setImageBitmap(this)
//                        if(!imageIsText){
//                            if (bitmap != null) {
//
//                            }
//                        }
//                        if(imageIsText){
//                            if (bitmap != null) {
//                               // startTextRecognizing(bitmap)
//                            }
//                        }
//                    }
                }
            }

//
//
//
//           }
//        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun processImageTagging(bitmap: Bitmap){

        // Multiple object detection in static images
        val options = ObjectDetectorOptions.Builder()
            .setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE)
            .enableMultipleObjects()
            .enableClassification()  // Optional
            .build()
        val objectDetector = ObjectDetection.getClient(options)

        try {
            val image = InputImage.fromBitmap(bitmap,0)
            objectDetector.process(image)
                .addOnSuccessListener {
                    // Task completed successfully
                    // ...
                    for (detectedObject in it) {
                        val boundingBox = detectedObject.boundingBox //giro apo to kouti
                        val trackingId = detectedObject.trackingId
                        val fslakdjfasldkf = detectedObject.labels
                        for (label in detectedObject.labels) {
                            val text = label.text
                            println("00000000000000000000000000000000000000000000")
                            println(boundingBox)
                            println(fslakdjfasldkf)

                            if (PredefinedCategory.FOOD == text) {
                               // ...
                                println("1111111111111111111111111111111111111111111111")
                                println(boundingBox)
                                println(fslakdjfasldkf)

  

                            }
                            val index = label.index
                            if (PredefinedCategory.FOOD_INDEX == index) {
                               // ...
                                println("222222222222222222222222222222222")
                                println(boundingBox)
                                println(fslakdjfasldkf)

                            }
                            val confidence = label.confidence //poso sigouros einai gia to antikimeno
                            println("4444444444444444444444444444444444444444444444444")
                            println(confidence)
                        }
                    }
                }
                .addOnFailureListener {
                    // Task failed with an exception
                    // ...
                }
        } catch (e: IOException) {
            e.printStackTrace()
        }



       // val visionImg = FirebaseVisionImage.fromBitmap(bitmap)
//        FirebaseVision.getInstance().cloudImageLabeler.processImage(visionImg).addOnSuccessListener{ tags->
//            binding.chipGroup.removeAllViews()
//            tags.sortedByDescending { it.confidence }
//                .map{
//                    Chip(context,null,R.style.Widget_MaterialComponents_Chip_Choice)
//                        .apply { text=it.text }
//                }
//                .forEach{
//                    binding.chipGroup.addView(it)
//                }
//        }.addOnFailureListener{
//            ex->
//            Log.wtf("Log",ex)
//        }
    }

    //get image from data
    private fun getImage(data: Intent?): Bitmap?{
        val selectedImage = data?.data
        return MediaStore.Images.Media.getBitmap(context?.contentResolver,selectedImage)
    }

//    private fun startTextRecognizing(bitmap: Bitmap){
//        if (binding.imageView.drawable != null){
//            val image = FirebaseVisionImage.fromBitmap(bitmap)
//            val detector = FirebaseVision.getInstance().cloudTextRecognizer
//            detector.processImage(image).addOnSuccessListener { firebaseVisionText->
//                processTextBlock(firebaseVisionText)
//                //firebase code for save text
//            }
//                .addOnFailureListener{
//                    Toast.makeText(context,"Sorry, Failed",Toast.LENGTH_LONG).show()
//                }
//        }else {
//            Toast.makeText(context,"Sorry, Failed",Toast.LENGTH_LONG).show()
//        }
//    }

//    private fun processTextBlock(result: FirebaseVisionText) {
//        binding.chipGroup.removeAllViews()
//        result.textBlocks.map {
//            Chip(context, null, R.style.Widget_MaterialComponents_Chip_Choice)
//                .apply { text = it.text }
//
//        }.forEach{binding.chipGroup.addView(it)}
//    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}