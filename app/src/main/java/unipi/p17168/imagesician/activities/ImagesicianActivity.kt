package unipi.p17168.imagesician.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.toolbar_settings.*
import unipi.p17168.imagesician.R
import unipi.p17168.imagesician.adapters.RecyclerViewWikiAdapter
import unipi.p17168.imagesician.databinding.ActivityImagesicianBinding
import unipi.p17168.imagesician.utils.Constants.NIGHTMODE
import unipi.p17168.imagesician.utils.ToolBox
import unipi.p17168.imagesician.wiki.WikiListItems
import java.io.IOException

class ImagesicianActivity : BaseActivity() {

    // ~~~~~~~VARIABLES~~~~~~~
    //VAR
    private var imageIsText = false
    private lateinit var binding: ActivityImagesicianBinding

    // ~~~~~~~~~~~~~~~~~~~~~~~

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityImagesicianBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        init()
    }

    private fun init(){
        setUpSettings(baseContext)
        setupClickListeners()
        setupActionBar()
    }

    private fun setupClickListeners(){

        binding.apply {

            switchContextText.setOnClickListener{ imageIsText = binding.switchContextText.isChecked }

            floatingButton.setOnClickListener{ pickImage() }

            btnCopyText.setOnClickListener{
                ToolBox().copyText(this@ImagesicianActivity,binding.etmForTextRecognition.text.toString()).also {
                    ToolBox().showSnackBar( window.decorView.rootView,
                        ContextCompat.getColor(this@ImagesicianActivity, R.color.colorSuccessBackgroundSnackbar),
                        ContextCompat.getColor(this@ImagesicianActivity, R.color.colorStrings),
                        getString(R.string.txt_copy_text_success),
                        getString(R.string.txt_all_right),
                        Snackbar.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun setupActionBar() {
        binding.toolbar.apply {
            setSupportActionBar(root)
            imgBtn_Settings.setOnClickListener {
                goToSettingsActivity(this@ImagesicianActivity)
            }
        }
    }


    private fun setUpSettings1(){
        sharePrefNightMode = PreferenceManager.getDefaultSharedPreferences(this)
        when(sharePrefNightMode.getBoolean(NIGHTMODE, false)){
            true-> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            false->AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

    }

    private fun isGoneTextRecognition(isGone:Boolean):Boolean{
        if(isGone){
            binding.etmForTextRecognition.clearComposingText()
            binding.btnCopyText.isGone = true
            binding.etmForTextRecognition.isGone = true

        }else if (!isGone){
            binding.btnCopyText.isVisible = true
            binding.etmForTextRecognition.isVisible = true
        }
        return true
    }

    private fun isGoneImage(isGone:Boolean):Boolean{
        if(isGone){
            binding.recyclerWiki.isGone = true
            binding.chipGroup.isGone = true
            binding.chipGroup.removeAllViews()
        }else if(!isGone){
            binding.recyclerWiki.isVisible = true
            binding.chipGroup.isVisible = true
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(ToolBox().isNetworkAvailbale(this)) {
            if (requestCode == 666) {
                if (resultCode == Activity.RESULT_OK) {
                    val bitmapImage = getImage(data)

                    bitmapImage.apply {
                        if (!imageIsText) {
                            if (bitmapImage != null) {
                                if (isGoneTextRecognition(true)) {
                                    binding.imageView.setImageBitmap(bitmapImage)
                                    processImageTagging(bitmapImage)
                                } else return
                            }
                        }

                        if (imageIsText) {
                            if (bitmapImage != null) {
                                if (isGoneImage(true)) {
                                    binding.imageView.setImageBitmap(bitmapImage)
                                    startTextRecognizing(bitmapImage)
                                } else return
                            }
                        }

                    }
                }
            }
        }else {
            isGoneImage(true)
            isGoneTextRecognition(true)
            ToolBox().copyText(this,binding.etmForTextRecognition.text.toString()).also {
                ToolBox().showSnackBar(window.decorView.rootView,
                    ContextCompat.getColor(this, R.color.colorErrorBackgroundSnackbar),
                    ContextCompat.getColor(this, R.color.colorStrings),
                    getString(R.string.txt_no_internet_connection),
                    getString(R.string.txt_all_right),
                    Snackbar.ANIMATION_MODE_SLIDE)
                    .show()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun startTextRecognizing(bitmap: Bitmap) {

        val recognizerText = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val builder = StringBuilder()
        val image = InputImage.fromBitmap(bitmap,0)
        val result = recognizerText.process(image)
        binding.etmForTextRecognition.clearComposingText()
        isGoneTextRecognition(true)
        result.addOnSuccessListener {
            val resultText = result.result
            for (block in resultText.textBlocks) {
                for (line in block.lines) {
                    for (element in line.elements) {
                        val elementText = element.text
                        builder.append("$elementText ")
                    }
                }
            }
        }.addOnCompleteListener {

            // Returns `true` if this string is empty or consists solely of whitespace characters.
            fun CharSequence.isBlank(): Boolean =
                length == 0 || indices.all { this[it].isWhitespace() }
            if (builder.toString().isBlank()) {
                val dialogNotFound = ToolBox().showNotFoundDialog(this)
                dialogNotFound.show()
            } else {
                isGoneTextRecognition(false)
                binding.etmForTextRecognition.setText(builder.toString())
            }

        }.addOnFailureListener {
            // Task failed with an exception
            val dialogWrong = ToolBox().showWrongDialog(this)
            dialogWrong.show()

        }
    }

    private fun processImageTagging(bitmap: Bitmap){

        // Multiple object detection in static images
        val options = ObjectDetectorOptions.Builder()
            .setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE)
            .enableMultipleObjects()
            .enableClassification()
            .build()

        val objectDetector = ObjectDetection.getClient(options)
        val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
        val listWithLabels = arrayListOf<String>()

        try {

            val image = InputImage.fromBitmap(bitmap,0)

            fun objDetector(firstFailed :Boolean) {
                //OBJECT DETECTOR
                objectDetector.process(image).addOnSuccessListener {
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
                    isGoneImage(false)
                    listWithLabels.sortedByDescending {item->
                        item.length
                    }.map {
                        Chip(this, null, R.style.Widget_MaterialComponents_Chip_Choice)
                            .apply { text = it }
                    }.forEach { item ->
                        binding.chipGroup.addView(item)
                    }

                    wikiSearch(listWithLabels)

                }.addOnFailureListener {
                    // Task failed with an exception
                    if(firstFailed){
                        val dialogNotFound = ToolBox().showNotFoundDialog(this)
                        dialogNotFound.show()
                    }
                }
            }
            // LABELER
            labeler.process(image).addOnSuccessListener {
                // Task completed successfully
                for (label in it) {
                    val textLabeler = label.text
                    if (!listWithLabels.contains(label.text)) {
                        listWithLabels.add(textLabeler)
                    }
                }
                objDetector(false)  //OBJECT DETECTOR
            }.addOnFailureListener {
                objDetector(true)  // Task failed with an exception
            }
        } catch (e: IOException) {
            val dialogWrong = ToolBox().showWrongDialog(this)
            dialogWrong.show()
            e.printStackTrace()
        }
    }

    //Get image from data
    private fun getImage(data: Intent?): Bitmap?{
        val selectedImage = data?.data
        return MediaStore.Images.Media.getBitmap(this.contentResolver,selectedImage)
    }

    private fun pickImage(){
        val openGalleryIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(openGalleryIntent, 666)
    }

    private fun wikiSearch(finalList: ArrayList<String>) {
        val data: MutableList<WikiListItems> = ArrayList()

        finalList.forEach {
            data.add(WikiListItems("${R.string.txt_informations_about} $it","https://en.wikipedia.org/wiki/$it"))
        }

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)
        val adapter = RecyclerViewWikiAdapter(data)

        binding.recyclerWiki.layoutManager = layoutManager
        binding.recyclerWiki.setHasFixedSize(true)
        binding.recyclerWiki.adapter = adapter

    }

    /*
   class WikipediaParser() {

      private val baseUrl: String = String.format("https://en.wikipedia.org/wiki/potato")

     @Throws(IOException::class)
     fun fetchFirstParagraph(article: String): String {
         return try{
             val url = baseUrl + article
             val doc: Document = Jsoup.connect(url).get()
             val paragraphs: Elements = doc.select(".mw-content-ltr p")
             val firstParagraph: Element = paragraphs.first()
             Log.e("My First Paragraph:" , firstParagraph)
             firstParagraph.text()
         }catch (e:IOException) {
              "Failed"
         }
     }
       fun printWiki(){
           try {
               val parser = WikipediaParser("en")
               val firstParagraph = parser.fetchFirstParagraph("Potato")
               Log.e("My First Paragraph:" , firstParagraph) // "The potato is a starchy [...]."

           }catch (e:IOException){
               Log.e("ImageActivity" , "error")
           }
       }
   }
*/
}