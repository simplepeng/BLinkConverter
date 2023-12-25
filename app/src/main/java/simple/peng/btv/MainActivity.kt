package simple.peng.btv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import simple.peng.btv.ui.theme.BTVShrotToLongTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BTVShrotToLongTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainPage()
                }
            }
        }
    }
}

@Composable
fun MainPage() {
    val scope = rememberCoroutineScope()

    var inputUrl by remember { mutableStateOf("https://b23.tv/uL5RRLq") }

    var outputUrl by remember { mutableStateOf("") }
    var outTitle by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = inputUrl,
            onValueChange = {
                inputUrl = it
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

                },
                modifier = Modifier.padding(end = 10.dp)
            ) {
                Text(text = "粘贴")
            }

            Button(onClick = {
                if (inputUrl.isEmpty()) return@Button

                val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
                    throwable.printStackTrace()
                }
                scope.launch(exceptionHandler) {
                    withContext(Dispatchers.IO) {
                        val (title, url) = ParseUtils.decode(inputUrl)
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

        }) {
            Text(text = "复制")
        }

        OutlinedTextField(
            value = outputUrl,
            onValueChange = {
                outTitle = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            label = {
                Text(text = "真实链接")
            }
        )
        Button(onClick = {

        }) {
            Text(text = "复制")
        }

    }
}