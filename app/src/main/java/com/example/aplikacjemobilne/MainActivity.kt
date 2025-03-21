package com.example.aplikacjemobilne

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.aplikacjemobilne.ui.theme.AplikacjeMobilneTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AplikacjeMobilneTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun CocktailList(navController: NavController, modifier: Modifier = Modifier) {
    val cocktails = remember {
        listOf("Mojito", "Margarita", "Old Fashioned", "Daiquiri", "Negroni")
    }

    LazyColumn(modifier = modifier.padding(16.dp)) {
        items(cocktails) { cocktail ->
            Text(
                text = cocktail,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navController.navigate("detail/$cocktail")
                    }
                    .padding(16.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun CocktailDetail(navController: NavController, cocktailName: String?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(50.dp)
    ) {
        Text(text = "Cocktail Details", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Selected: $cocktailName", style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.popBackStack() }) {
            Text(text = "Back to List")
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "list") {
        composable("list") { CocktailList(navController) }
        composable(
            "detail/{cocktailName}",
            arguments = listOf(navArgument("cocktailName") { type = NavType.StringType })
        ) { backStackEntry ->
            CocktailDetail(navController, backStackEntry.arguments?.getString("cocktailName"))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CocktailListPreview() {
    AplikacjeMobilneTheme {
        // Provide a dummy NavController to avoid preview errors
        val navController = rememberNavController()
        CocktailList(navController)
    }
}
