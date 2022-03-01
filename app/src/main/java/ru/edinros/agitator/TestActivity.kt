package ru.edinros.agitator

import android.os.Bundle
import android.os.Parcelable
import android.os.PersistableBundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.edinros.agitator.features.task.TaskListVM


sealed class Screen(val screenName: String, val titleResourceId: Int) {
    object Main : Screen("main", -1)
    object User : Screen("user", -1)
}

@AndroidEntryPoint
class TestActivity : ComponentActivity() {
    override fun onBackPressed() {
        finish()
        //todo create confirmation dialog
        super.onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = Screen.Main.screenName
            ) {
                composable(Screen.Main.screenName) {
                    MainScreen1(navController)
                }
                composable(Screen.User.screenName) {
                    UserScreen1(navController)
                }
            }

        }
    }

    fun NavController.navigate(
        route: String,
        params: List<Pair<String, Parcelable>>?,
        builder: NavOptionsBuilder.() -> Unit = {}
    ) {
        params?.let {
            val arguments = this.currentBackStackEntry?.arguments
            params.forEach { arguments?.putParcelable(it.first, it.second) }
        }

        navigate(route, builder)
    }

    @Composable
    private fun MainScreen1(navController: NavHostController) {
        val scaffoldState = rememberScaffoldState()
        Scaffold(scaffoldState = scaffoldState) {

            Button(onClick = { navController.navigate(Screen.User.screenName) }) {

            }
        }
    }

    @Composable
    private fun UserScreen1(navController: NavHostController) {
        val scaffoldState = rememberScaffoldState()
        val model: TaskListVM = hiltViewModel()
        Scaffold(scaffoldState = scaffoldState) {


        }
    }
}