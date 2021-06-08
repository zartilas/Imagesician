package unipi.p17168.imagesician

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizerOptions
import unipi.p17168.imagesician.adapters.RecyclerViewWikiAdapter
import unipi.p17168.imagesician.databinding.FragmentImageBinding
import unipi.p17168.imagesician.wiki.WikiListItems
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
                val bitmapImage = getImage(data)

//                if (bitmap != null) {
//                    binding.imageView.setImageBitmap(bitmap)
//                    processImageTagging(bitmap)
//                }
                bitmapImage.apply {
                    if(!imageIsText){
                        if (bitmapImage != null) {
//                            val imageForIV = checkIfImageNeededRotation(bitmapImage)//TODO FIX THIS LINE

                            binding.chipTextRecognizing.isVisible = false
                            binding.imageView.setImageBitmap(bitmapImage)
                            processImageTagging(bitmapImage)
                        }
                    }
                    if (imageIsText){
                        if (bitmapImage != null) {
                            binding.recyclerWiki.isGone = true
                            binding.chipGroup.removeAllViews()
                            binding.imageView.setImageBitmap(bitmapImage)
                            startTextRecognizing(bitmapImage)
                        }
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    @SuppressLint("SetTextI18n")
    private fun startTextRecognizing(bitmap: Bitmap) {
        val recognizerText = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
      //  val image = FirebaseVision.fromBitmap(bitmap,0)
        val image = InputImage.fromBitmap(bitmap,0)
        val result = recognizerText.process(image)
        val listWithText = arrayListOf<String>()
        result.addOnSuccessListener {
            println("I AM TEXT BOOS")
            val resultText = result.result
            for (block in resultText.textBlocks) {
                val blockText = block.text
                println("The blockText: $blockText")

                for (line in block.lines) {
                    val lineText = line.text
                    println("The lineText: $lineText")
                    listWithText.add(lineText)
                    binding.chipTextRecognizing.isVisible = true
                    binding.chipTextRecognizing.text = lineText

//                    for (element in line.elements) {
//                        val elementText = element.text
//                        val elementCornerPoints = element.cornerPoints
//                        val elementFrame = element.boundingBox
//                    }
                }

                listWithText.forEach{
                    println("My text boss is: $it")
                }
            }

            }.addOnFailureListener {
                // Task failed with an exception
                binding.chipTextRecognizing.isVisible = true
                binding.chipTextRecognizing.chipText = "i am text Fail"
                println("I AM TEXT  FAIL BOSS")
            }
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
        val listWithLabels = arrayListOf<String>()

        try {
            val image = InputImage.fromBitmap(bitmap,0)
            fun objDetector(firstFailed :Boolean) {
                //OBJECT DETECTOR
                objectDetector.process(image)
                    .addOnSuccessListener {
                        // Task completed successfully
                        for (detectedObject in it) {
                            for (label in detectedObject.labels) {
                                val textDetected = label.text
                                if (!listWithLabels.contains(textDetected)) {
                                    listWithLabels.add(textDetected)
                                }
                            }
                        }
                        binding.chipGroup.removeAllViews()
                        listWithLabels.sortedByDescending { it.length }
                            .map {
                                Chip(
                                    context,
                                    null,
                                    R.style.Widget_MaterialComponents_Chip_Choice
                                ).apply {
                                        text = it }
                                    }.forEach {
                                        binding.chipGroup.addView(it)
                                    }
                        wikiSearch(listWithLabels)
                    }.addOnFailureListener {
                        // Task failed with an exception
                        if(firstFailed){
                            binding.chipTextRecognizing.isVisible = true
                            binding.chipTextRecognizing.text = "I may be an AI app but not God, I can't know everything, I'm sorry." }
                        }
            }
           // LABELER
            labeler.process(image)
                .addOnSuccessListener {
                    // Task completed successfully
                     for (label in it) {
                         val textLabeler = label.text
                         if (!listWithLabels.contains(label.text)) {
                             listWithLabels.add(textLabeler)
                     }
                         Log.wtf("LABELER","The labeler : $textLabeler")
                     }
                    objDetector(false)  //OBJECT DETECTOR
                }.addOnFailureListener {
                    objDetector(true)  // Task failed with an exception
                }

        } catch (e: IOException) {
            binding.chipTextRecognizing.isVisible = true
            binding.chipTextRecognizing.text = "Something went wrong,try again."
            e.printStackTrace()
        }
    }

    //get image from data
    private fun getImage(data: Intent?): Bitmap?{
        val selectedImage = data?.data
        return MediaStore.Images.Media.getBitmap(context?.contentResolver,selectedImage)
    }

//    fun rotateBitmap(original: Bitmap, degrees: Float): Bitmap? {
//        val width = original.width
//        val height = original.height
//        val matrix = Matrix()
//        matrix.preRotate(degrees)
//        val rotatedBitmap = Bitmap.createBitmap(original, 0, 0, width, height, matrix, true)
//        val canvas = Canvas(rotatedBitmap)
//        canvas.drawBitmap(original, 5.0f, 0.0f, null)
//        return rotatedBitmap
//    }

//    //Get Image Uri from bitmap
//    private fun  getImageUri(context: Context, bitmap: Bitmap): Uri? {
//       // val bytes =  ByteArrayOutputStream()
//        //bitmap.compress( Bitmap.CompressFormat.JPEG,100,bytes)
//        val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "imageUri", null)
//        return parse(path)
//    }
//


//    //Check if the photo needs rotation
//    @Throws(IOException::class)
//    private fun checkIfImageNeededRotation(newImage: Bitmap): Bitmap? {
//
//    val bos = ByteArrayOutputStream()
//    newImage.compress(CompressFormat.PNG, 0 /*ignored for PNG*/, bos)
//    val bitmapdata: ByteArray = bos.toByteArray()
//    val bs = ByteArrayInputStream(bitmapdata)
//
//        val ei = ExifInterface(bs)
//        return when (ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
//            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(newImage, 90F)
//            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(newImage, 180F)
//            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(newImage, 270F)
//
//            else -> newImage
//        }
//    }
//
//    private fun rotateImage(newImage: Bitmap, degree: Float): Bitmap? {
//        val matrix = Matrix()
//        matrix.postRotate(degree)
//        val rotatedNewImage = Bitmap.createBitmap(newImage, 0, 0, newImage.width, newImage.height, matrix, true)
//        newImage.recycle()
//        println("I AM HERE TOO BOSS")
//        return rotatedNewImage
//    }



//    class WikipediaParser() {
//        private val baseUrl: String = String.format("https://en.wikipedia.org/wiki/potato")
//
//        @Throws(IOException::class)
//        fun fetchFirstParagraph(article: String): String {
//            return try{
//                val url = baseUrl + article
//                val doc: Document = Jsoup.connect(url).get()
//                val paragraphs: Elements = doc.select(".mw-content-ltr p")
//                val firstParagraph: Element = paragraphs.first()
//                println("My First Paragraph: $firstParagraph")
//                firstParagraph.text()
//            }catch (e:IOException) {
//                "Failed"
//            }
//        }
////                fun printWiki(){
////                    try {
////                        val parser = WikipediaParser("en")
////                        val firstParagraph = parser.fetchFirstParagraph("Potato")
////
////                        println("My paragraph: $firstParagraph") // prints "The potato is a starchy [...]."
////                    }catch (e:IOException){
////                        print("SORRY BOSS")
////                    }
////
////                }
//    }


    private fun wikiSearch(finalList: ArrayList<String>) {
        val data: MutableList<WikiListItems> = ArrayList()

        finalList.forEach {
            data.add(WikiListItems("Information\'s about $it","https://en.wikipedia.org/wiki/$it"))
        }

        val layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        val adapter = RecyclerViewWikiAdapter(data)

        binding.recyclerWiki.isVisible = true
        binding.recyclerWiki.layoutManager = layoutManager
        binding.recyclerWiki.setHasFixedSize(true)
        binding.recyclerWiki.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}



