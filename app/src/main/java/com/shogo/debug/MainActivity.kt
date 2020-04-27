package com.shogo.debug

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.shogo.debug.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val binding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        val dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setDomainUriPrefix("https://shogogeek.com")
            .setLongLink(Uri.parse("https://shogogeek.com/?link=https://zozo.jp/shop/classicalelf/goods/35056123&apn=com.shogo.debug&afl=https://note.com&ibi=com.example.test&st=title&si=https://firebasestorage.googleapis.com/v0/b/retory-debug.appspot.com/o/shareImage%2FApple000699.e2b04d396f774c878e6e5767525ccfa4.0211%2F2020-04-18%2021%3A22%3A59.909721.png?alt=media&token=42b3d531-9a2e-4a4c-96ee-872438ab2cf1&sd=detail"))
            .buildShortDynamicLink()
            .addOnSuccessListener { result ->
                // Short link created
                val shortLink = result.shortLink
                val flowchartLink = result.previewLink

                binding.generatedUrl.visibility = View.VISIBLE
                binding.generatedUrl.text = flowchartLink.toString()

                val mManager: ClipboardManager =
                    applicationContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                binding.generatedUrl.setOnClickListener {
                    mManager.setPrimaryClip(ClipData.newPlainText("", shortLink.toString()))
                    Toast.makeText(this, "クリップボードにコピーしました", Toast.LENGTH_LONG).show()
                }
            }

        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(intent)
            .addOnSuccessListener(
                this
            ) { pendingDynamicLinkData ->
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                }
                val id = deepLink?.getQueryParameter("id")
                if (id == null || id == "") {
                    binding.id.text = "現在IDは空です。"
                } else {
                    binding.id.text = "id=$id"
                }
            }
            .addOnFailureListener(
                this
            ) { e -> Log.w("ダイナミックリンクのエラー", "getDynamicLink:onFailure", e) }
    }
}
