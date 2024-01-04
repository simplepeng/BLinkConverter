package simple.peng.btv

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val clipboardManager by lazy {
        application.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }

    var btvUrl by mutableStateOf("")

    var outTitle by mutableStateOf("")

    var outUrl by mutableStateOf("")

    var canDecode by mutableStateOf(false)

    fun decodeUrl(
        onDecoding: ((Boolean) -> Unit)? = null,
    ) {
        onDecoding?.invoke(true)
        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }
        viewModelScope.launch(exceptionHandler) {
            withContext(Dispatchers.IO) {
                val (title, url) = ParseUtils.decode(btvUrl)
                outTitle = title
                outUrl = url
                onDecoding?.invoke(false)
            }
        }
    }

    fun checkPrimaryClip() {
        if (clipboardManager.hasPrimaryClip()) {
            val primaryClip = clipboardManager.primaryClip ?: return
            if (primaryClip.itemCount <= 0) return

            val text = primaryClip.getItemAt(0)?.text
            if (text.isNullOrEmpty()) return

            //用正则把mainViewModel.btvUrl里面的url取出来
            val pattern = "https://\\S+".toRegex()
            val matches = pattern.findAll(text)
            val url = matches.toList().firstOrNull()?.value.orEmpty()
            btvUrl = url
            Log.d("MainActivity", "btvUrl -- $url")

            if (btvUrl.isNotEmpty()) {
                canDecode = true
            }
        }
    }

    fun copyOutTitle() {
        clipboardManager.setPrimaryClip(ClipData.newPlainText("标题", outTitle))
    }

    fun copyOutUrl() {
        clipboardManager.setPrimaryClip(ClipData.newPlainText("链接", outUrl))
    }

    fun fillInputUrl(){
        if (clipboardManager.hasPrimaryClip()) {
            val clipData = clipboardManager.primaryClip
            val item = clipData?.getItemAt(0)
            item?.text?.let {
                btvUrl = it.toString()
            }
        }
    }
}