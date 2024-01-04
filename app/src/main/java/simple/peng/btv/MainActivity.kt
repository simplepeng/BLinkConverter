package simple.peng.btv

import android.content.ClipData
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.os.postDelayed
import simple.peng.btv.ui.theme.BTVShrotToLongTheme

class MainActivity : ComponentActivity() {

    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BTVShrotToLongTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainPage(mainViewModel)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Handler(Looper.getMainLooper()).postDelayed(200) {
//            Log.d("MainActivity", "onResume -- ${clipboardManager.hasPrimaryClip()}")
            mainViewModel.checkPrimaryClip()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage(
    viewModel: MainViewModel
) {
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
                        viewModel.fillInputUrl()
                    },
                    modifier = Modifier.padding(end = 10.dp)
                ) {
                    Text(text = "粘贴")
                }

                if (viewModel.canDecode) {
                    viewModel.decodeUrl(onDecoding = {
                        isLoading = it
                    })
                    viewModel.canDecode = false
                }

                Button(onClick = {
                    viewModel.canDecode = true
                }) {
                    Text(text = "转换")
                }
            }

            OutlinedTextField(
                value = viewModel.outTitle,
                onValueChange = {
                    viewModel.outTitle = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                label = {
                    Text(text = "标题")
                }
            )
            Button(onClick = {
                viewModel.copyOutTitle()
            }) {
                Text(text = "复制")
            }

            OutlinedTextField(
                value = viewModel.outUrl,
                onValueChange = {
                    viewModel.outUrl = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                label = {
                    Text(text = "真实链接")
                }
            )
            Button(onClick = {
                viewModel.copyOutUrl()
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