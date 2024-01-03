package simple.peng.btv

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.os.postDelayed
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import simple.peng.btv.ui.theme.BTVShrotToLongTheme

class MainActivity : ComponentActivity() {

    private val clipboardManager by lazy {
        applicationContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }

//    private var btvUrl by mutableStateOf("")

    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BTVShrotToLongTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainPage(clipboardManager, mainViewModel)
                }
            }
        }
    }

    private fun checkPrimaryClip() {
        if (clipboardManager.hasPrimaryClip()) {
            val primaryClip = clipboardManager.primaryClip ?: return
            if (primaryClip.itemCount <= 0) return

            val text = primaryClip.getItemAt(0)?.text
            if (text.isNullOrEmpty()) return

            if (text.startsWith("http")) {
                mainViewModel.btvUrl = text.toString()
            } else {
//                mainViewModel.btvUrl.substringAfterLast("https").let {
//                    mainViewModel.btvUrl = "https$it"
//                    Log.d("MainActivity", "btvUrl: ${mainViewModel.btvUrl}")
//                }
                //用正则把mainViewModel.btvUrl里面的url取出来
                val pattern = "https://\\S+".toRegex()
                val matches = pattern.findAll(text)
                val url = matches.toList().firstOrNull()?.value.orEmpty()
                mainViewModel.btvUrl = url
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Handler(Looper.getMainLooper()).postDelayed(200) {
            Log.d("MainActivity", "onResume -- ${clipboardManager.hasPrimaryClip()}")
            checkPrimaryClip()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage(
    clipboardManager: ClipboardManager,
    viewModel: MainViewModel
) {
    val scope = rememberCoroutineScope()

//    var inputUrl by remember { mutableStateOf(btvUrl) }

    var outputUrl by remember { mutableStateOf("") }
    var outTitle by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }

    LoadingLayout(isLoading = isLoading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopAppBar(title = {
                Text(text = "不要短链")
            })

            OutlinedTextField(
                value = viewModel.btvUrl,
                onValueChange = {
                    viewModel.btvUrl = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                label = {
                    Text(text = "粘帖短链")
                }
            )

            Row {
                Button(
                    onClick = {
                        if (clipboardManager.hasPrimaryClip()) {
                            val clipData = clipboardManager.primaryClip
                            val item = clipData?.getItemAt(0)
                            item?.text?.let {
                                viewModel.btvUrl = it.toString()
                            }
                        }
                    },
                    modifier = Modifier.padding(end = 10.dp)
                ) {
                    Text(text = "粘贴")
                }

                Button(onClick = {
                    if (viewModel.btvUrl.isEmpty()) return@Button

                    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
                        throwable.printStackTrace()
                        isLoading = false
                    }
                    isLoading = true
                    scope.launch(exceptionHandler) {
                        withContext(Dispatchers.IO) {
                            val (title, url) = ParseUtils.decode(viewModel.btvUrl)
                            isLoading = false
                            outTitle = title
                            outputUrl = url
                        }
                    }
                }) {
                    Text(text = "转换")
                }
            }

            OutlinedTextField(
                value = outTitle,
                onValueChange = {
                    outTitle = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                label = {
                    Text(text = "标题")
                }
            )
            Button(onClick = {
                clipboardManager.setPrimaryClip(ClipData.newPlainText("标题", outTitle))
            }) {
                Text(text = "复制")
            }

            OutlinedTextField(
                value = outputUrl,
                onValueChange = {
                    outputUrl = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                label = {
                    Text(text = "真实链接")
                }
            )
            Button(onClick = {
                clipboardManager.setPrimaryClip(ClipData.newPlainText("真实链接", outputUrl))
            }) {
                Text(text = "复制")
            }

        }
    }
}

@Composable
fun LoadingLayout(
    isLoading: Boolean,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        content()
        if (isLoading) {
            CircularProgressIndicator()
        }
    }
}