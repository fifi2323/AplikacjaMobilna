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

    LazyColumn(modifier = modifier.padding(start = 16.dp, end = 16.dp, top = 32.dp)) {
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
    val cocktailsDetails = remember {
        mapOf(
            "Mojito" to Pair(
                listOf("50ml Rum", "Soda", "Limonka", "Mięta", "Cukier"),
                "Rozgnieć limonkę z cukrem, dodaj miętę, rum, lód i dopełnij sodą."
            ),
            "Margarita" to Pair(
                listOf("50ml Tequila", "20ml Triple sec", "20ml Sok z limonki", "Sól"),
                "Wymieszaj składniki w shakerze, przelej do kieliszka z solą na rancie."
            ),
            "Old Fashioned" to Pair(
                listOf("50ml Bourbon", "Kostka cukru", "Angostura", "Woda"),
                "Rozpuść cukier z Angosturą, dodaj lód i bourbon, zamieszaj."
            ),
            "Daiquiri" to Pair(
                listOf("50ml Rum", "25ml Sok z limonki", "15ml Syrop cukrowy"),
                "Wymieszaj w shakerze i przelej do schłodzonego kieliszka."
            ),
            "Negroni" to Pair(
                listOf("30ml Gin", "30ml Campari", "30ml Wermut czerwony"),
                "Wymieszaj składniki w szklance z lodem, udekoruj pomarańczą."
            )
        )
    }

    val (ingredients, preparation) = cocktailsDetails[cocktailName] ?: Pair(emptyList(), "Brak danych")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 32.dp)
    ) {
        Text(text = "Cocktail Details", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Selected: $cocktailName", style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Ingredients:", style = MaterialTheme.typography.titleMedium)
        ingredients.forEach { ingredient ->
            Text(text = "- $ingredient", style = MaterialTheme.typography.bodyLarge)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Preparation:", style = MaterialTheme.typography.titleMedium)
        Text(text = preparation, style = MaterialTheme.typography.bodyLarge)

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
