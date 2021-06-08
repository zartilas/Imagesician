package unipi.p17168.imagesician.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.wiki_items.view.*
import unipi.p17168.imagesician.R
import unipi.p17168.imagesician.wiki.WikiListItems


class RecyclerViewWikiAdapter(
    private val data: List<WikiListItems>) :
    RecyclerView.Adapter<RecyclerViewWikiAdapter.ViewHolder>() {

    private val item: MutableList<CardView>

    init {
        this.item = ArrayList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewWikiAdapter.ViewHolder {
       val v = LayoutInflater.from(parent.context)
           .inflate(R.layout.wiki_items,parent,false)
        return ViewHolder(v)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onBindViewHolder(holder: RecyclerViewWikiAdapter.ViewHolder, position: Int) {
        holder.nameItems.text = data[position].itemName
        holder.infoItem.loadUrl(data[position].itemInfo)
        holder.infoItem.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.settings.javaScriptEnabled = true

                return false
            }
        }
        item.add(holder.card)
    }


    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder
    internal constructor(
        itemView: View

    ): RecyclerView.ViewHolder(itemView){
        val nameItems: TextView = itemView.itemName
        val infoItem: WebView = itemView.webItemView

        val card : CardView = itemView.cardViewSingleItem_Wiki
    }
}