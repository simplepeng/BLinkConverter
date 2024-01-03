package simple.peng.btv

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    var btvUrl by mutableStateOf("")

    var outTitle by mutableStateOf("")

    var outUrl by mutableStateOf("")
}