package unipi.p17168.imagesician

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.chip.Chip
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions

import unipi.p17168.imagesician.databinding.FragmentImageBinding
import java.io.IOException


@Suppress("DEPRECATION") //for the method getImage
class ImageFragment : Fragment() {

    //~~~~~~~VARIABLES~~~~~~~

    //var
    private var _binding : FragmentImageBinding ? = null
    private var imageIsText = false

    //val
    private val binding get() = _binding!!


//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        _binding = FragmentImageBinding.inflate(inflater, container, false)
        val view = binding.root

        userTriggerButtons()

        return view
    }

//    fun init(){
//
//    }


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

    @SuppressLint("SetTextI18n", "ResourceAsColor")
    private fun processImageTagging(bitmap: Bitmap){

        // Multiple object detection in static images
        val options = ObjectDetectorOptions.Builder()
            .setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE)
            .enableMultipleObjects()
            .enableClassification()  // Optional
            .build()

        val objectDetector = ObjectDetection.getClient(options)
        val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

        try {
            val image = InputImage.fromBitmap(bitmap,0)

            //LABELER
            labeler.process(image)
                .addOnSuccessListener {
                    // Task completed successfully
                     for (label in it) {
                         binding.chipGroup.removeAllViews()
                         it.sortedByDescending { it.confidence }
                             .map{
                                 Chip(context,null,R.style.Widget_MaterialComponents_Chip_Choice)
                                     .apply { text=it.text }
                             }
                             .forEach{
                                 binding.chipGroup.addView(it)
                             }
                         val textLabeler = label.text
                         println("The labeler : $textLabeler")
                     }
                }
                .addOnFailureListener {
                    // Task failed with an exception
                }
            //OBJECT DETECTOR
            objectDetector.process(image)
                .addOnSuccessListener {
                    // Task completed successfully
                    val listWithLabels = arrayListOf<String>()
                    for (detectedObject in it) {
                        // val boundingBox = detectedObject.boundingBox //giro apo to box
                        // val trackingId = detectedObject.trackingId
                        for (label in detectedObject.labels) {
                            val textDetected = label.text
                                if (!listWithLabels.contains(textDetected)) {
                                    listWithLabels.add(textDetected)
                                }

//                                    it.sortedByDescending { it.trackingId }
//                                        .map {
//                                            Chip(
//                                                context,
//                                                null,
//                                                R.style.Widget_MaterialComponents_Chip_Choice
//                                            ).apply {
//                                                text = textDetected
//                                            }
//                                        }
//                                        .forEach {
//                                            if(it == it){
//                                                binding.chipGroup.addView(it)}
//
//
//                                        }
                        }
                        //TODO FIX  CHIP GROUP
                      listWithLabels.sortedByDescending { it.length }.map {
                                Chip(
                                    context,
                                    null,
                                    R.style.Widget_MaterialComponents_Chip_Choice
                                ).apply {
                                    // text = it.subSequence(0,listWithLabels.size)
                                }
                            }
                            .forEach {
                                binding.chipGroup.addView(it)
                        }
                        listWithLabels.forEach(System.out::println)
                    }

                }.addOnFailureListener {
                    // Task failed with an exception
                    // ...
                }

        } catch (e: IOException) {
            e.printStackTrace()
        }

    }


    //get image from data
    private fun getImage(data: Intent?): Bitmap?{
        val selectedImage = data?.data
        return MediaStore.Images.Media.getBitmap(context?.contentResolver,selectedImage)
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


