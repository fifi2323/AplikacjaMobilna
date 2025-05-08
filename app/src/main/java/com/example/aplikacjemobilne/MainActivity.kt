package com.example.aplikacjemobilne

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.aplikacjemobilne.ui.theme.AplikacjeMobilneTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: CocktailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AplikacjeMobilneTheme {
                val drawerState = rememberDrawerState(DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                var showFavorites by remember { mutableStateOf(false) }

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        NavigationDrawerContent(
                            onClose = { scope.launch { drawerState.close() } },
                            onFavoriteSelected = {
                                showFavorites = !showFavorites
                                scope.launch { drawerState.close() }
                            },
                            onMenuClick = {
                                scope.launch { drawerState.close() }
                                showFavorites = false
                            }
                        )
                    }
                ) {
                    AppNavigation(
                        viewModel = viewModel,
                        showFavorites = showFavorites,
                        onMenuClick = { scope.launch { drawerState.open() } },
                        onFavoriteToggle = { name ->
                            // Handled in the AppNavigation now
                        }
                    )
                }
            }
        }
    }
}
data class Cocktail(
    val name: String,
    val imageRes: Int? = null,  // Make image resource nullable
    val isFavorite: Boolean
)
@Composable
fun NavigationDrawerContent(
    onClose: () -> Unit,
    onFavoriteSelected: () -> Unit,
    onMenuClick: () -> Unit
) {

    Column(modifier = Modifier.fillMaxSize().padding(top = 32.dp).background(MaterialTheme.colorScheme.surface)) {
        Text(
            text = "Menu",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(16.dp)

        )
        Divider()
        NavigationDrawerItem(
            label = { Text("Strona główna") },
            selected = false,
            onClick = {
                onMenuClick()
                onClose()
            }
        )
        NavigationDrawerItem(
            label = { Text("Ulubione") },
            selected = false,
            onClick = {
                onFavoriteSelected()
                onClose()
            }
        )
    }
}

@Composable
fun AppNavigation(
    viewModel: CocktailViewModel,
    showFavorites: Boolean,
    onMenuClick: () -> Unit,
    onFavoriteToggle: (String) -> Unit
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "list") {
        composable("list") {
            val cocktails by viewModel.allCocktails.observeAsState(emptyList())

            CocktailList(
                navController = navController,
                cocktails = cocktails.map {
                    Cocktail(it.name, it.imageResId, it.isFavourite)  // Use the actual imageResId from DB
                },
                showFavorites = showFavorites,
                onCocktailSelected = { name -> navController.navigate("detail/$name") },
                onMenuClick = onMenuClick,
                onFavoriteToggle = { name ->
                    val cocktail = cocktails.find { it.name == name }
                    cocktail?.let {
                        viewModel.updateCocktail(it.copy(isFavourite = !it.isFavourite))
                    }
                }
            )
        }
        composable(
            "detail/{cocktailName}",
            arguments = listOf(navArgument("cocktailName") { type = NavType.StringType })
        ) { backStackEntry ->
            val cocktails by viewModel.allCocktails.observeAsState(emptyList())
            val cocktailName = backStackEntry.arguments?.getString("cocktailName")

            CocktailDetail(
                navController = navController,
                cocktailName = cocktailName,
                cocktails = cocktails.map {
                    Cocktail(it.name, 0, it.isFavourite)
                },
                onMenuClick = onMenuClick,
                onFavoriteToggle = { name ->
                    val cocktail = cocktails.find { it.name == name }
                    cocktail?.let {
                        viewModel.updateCocktail(it.copy(isFavourite = !it.isFavourite))
                    }
                }
            )
        }
        composable("add") {
            AddCocktailScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCocktailScreen(
    viewModel: CocktailViewModel,
    onBack: () -> Unit
) {
    var cocktailName by remember { mutableStateOf("") }
    var isFavorite by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Cocktail") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = cocktailName,
                onValueChange = { cocktailName = it },
                label = { Text("Cocktail Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = isFavorite,
                    onCheckedChange = { isFavorite = it }
                )
                Text("Add to favorites", style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (cocktailName.isNotBlank()) {
                        viewModel.addCocktail(cocktailName, isFavorite)
                        onBack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = cocktailName.isNotBlank()
            ) {
                Text("Add Cocktail")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CocktailList(
    navController: NavController,
    cocktails: List<Cocktail>,
    showFavorites: Boolean,
    onCocktailSelected: (String) -> Unit,
    onMenuClick: () -> Unit,
    onFavoriteToggle: (String) -> Unit
) {
    val displayedCocktails = if (showFavorites) {
        cocktails.filter { it.isFavorite }
    } else {
        cocktails
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (showFavorites) "Ulubione" else "Lista koktajli") },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("add") }) {
                Icon(Icons.Default.Add, contentDescription = "Add Cocktail")
            }
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(innerPadding)
        ) {
            items(displayedCocktails) { cocktail ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCocktailSelected(cocktail.name) },
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (cocktail.imageRes != null) {
                            Image(
                                painter = painterResource(id = cocktail.imageRes),
                                contentDescription = cocktail.name,
                                modifier = Modifier
                                    .height(120.dp)
                                    .fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        } else {
                            // Show a placeholder or empty space when there's no image
                            Box(
                                modifier = Modifier
                                    .height(120.dp)
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Text(
                                    text = "No Image",
                                    modifier = Modifier.align(Alignment.Center),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(cocktail.name, style = MaterialTheme.typography.titleMedium)
                        IconButton(
                            onClick = { onFavoriteToggle(cocktail.name) },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Icon(
                                imageVector = if (cocktail.isFavorite)
                                    Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Ulubione",
                                tint = if (cocktail.isFavorite)
                                    MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CocktailDetail(
    navController: NavController,
    cocktailName: String?,
    cocktails: List<Cocktail>,
    onMenuClick: () -> Unit,
    onFavoriteToggle: (String) -> Unit
) {
    // Default details for predefined cocktails
    val defaultCocktailsDetails = remember {
        mapOf(
            "Mojito" to (listOf("50ml Rum", "Soda", "Limonka", "Mięta", "Cukier") to
                    "Rozgnieć limonkę z cukrem, dodaj miętę, rum, lód i dopełnij sodą."),
            "Margarita" to (listOf("50ml Tequila", "20ml Triple sec", "20ml Sok z limonki", "Sól") to
                    "Wymieszaj składniki w shakerze, przelej do kieliszka z solą na rancie."),
            "Old Fashioned" to (listOf("50ml Bourbon", "Kostka cukru", "Angostura", "Woda") to
                    "Rozpuść cukier z Angosturą, dodaj lód i bourbon, zamieszaj."),
            "Daiquiri" to (listOf("50ml Rum", "25ml Sok z limonki", "15ml Syrop cukrowy") to
                    "Wymieszaj w shakerze i przelej do schłodzonego kieliszka."),
            "Negroni" to (listOf("30ml Gin", "30ml Campari", "30ml Wermut czerwony") to
                    "Wymieszaj składniki w szklance z lodem, udekoruj pomarańczą.")
        )
    }

    val selected = cocktailName ?: "Mojito"
    val cocktail = cocktails.find { it.name == selected }

    // For custom cocktails, use default values
    val (ingredients, preparation) = if (defaultCocktailsDetails.containsKey(selected)) {
        defaultCocktailsDetails[selected] ?: (emptyList<String>() to "Brak danych")
    } else {
        // Default values for custom cocktails
        listOf("Składniki nieznane") to "Przepis nieznany. Dodaj składniki i instrukcje przygotowania."
    }


    var showMessage by rememberSaveable { mutableStateOf(false) }
    var timeInput by rememberSaveable { mutableStateOf("10") }
    var timeLeft by rememberSaveable { mutableStateOf(0) }
    var isTimerRunning by rememberSaveable { mutableStateOf(false) }
    var initialTime by rememberSaveable { mutableStateOf(0) }

    LaunchedEffect(isTimerRunning) {
        if (isTimerRunning && timeLeft > 0) {
            while (timeLeft > 0 && isTimerRunning) {
                delay(1000)
                timeLeft -= 1
            }
            isTimerRunning = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(selected) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Wróć")
                    }
                },
                actions = {
                    IconButton(onClick = { onFavoriteToggle(selected) }) {
                        Icon(
                            imageVector = if (cocktail?.isFavorite == true)
                                Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Ulubione",
                            tint = if (cocktail?.isFavorite == true)
                                MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showMessage = true }) {
                Text("SMS")
            }
        }
    ) { innerPadding ->
        if (showMessage) {
            AlertDialog(
                onDismissRequest = { showMessage = false },
                confirmButton = {
                    TextButton(onClick = { showMessage = false }) {
                        Text("OK")
                    }
                },
                title = { Text("Składniki do SMS") },
                text = { Text(ingredients.joinToString("\n")) }
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text("Ingredients:", style = MaterialTheme.typography.titleMedium)
            ingredients.forEach { Text("- $it", style = MaterialTheme.typography.bodyLarge) }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Preparation:", style = MaterialTheme.typography.titleMedium)
            Text(preparation, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Timer:", style = MaterialTheme.typography.titleMedium)
            if (!isTimerRunning && timeLeft == 0) {
                OutlinedTextField(
                    value = timeInput,
                    onValueChange = { if (it.all(Char::isDigit)) timeInput = it },
                    label = { Text("Czas w sekundach") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    val t = timeInput.toIntOrNull() ?: 0
                    if (t > 0) {
                        initialTime = t
                        timeLeft = t
                        isTimerRunning = true
                    }
                }) { Text("Start Timer") }
            } else if (isTimerRunning) {
                Text("Pozostało: $timeLeft s", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(onClick = { isTimerRunning = false }, modifier = Modifier.weight(1f)) { Text("Pauza") }
                    Button(onClick = {
                        isTimerRunning = false; timeLeft = 0; initialTime = 0; timeInput = "10"
                    }, modifier = Modifier.weight(1f)) { Text("Stop") }
                }
            } else {
                Text("Pozostało: $timeLeft s (pauza)", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(onClick = { isTimerRunning = true }, modifier = Modifier.weight(1f)) { Text("Wznów") }
                    Button(onClick = {
                        timeLeft = 0; initialTime = 0; timeInput = "10"
                    }, modifier = Modifier.weight(1f)) { Text("Stop") }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxWidth()) {
                Text("Back to List")
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 300)
@Composable
fun PhonePreview() {
    AplikacjeMobilneTheme {
        val navController = rememberNavController()
        CocktailList(
            navController = navController,
            cocktails = emptyList(),
            showFavorites = false,
            onCocktailSelected = {},
            onMenuClick = {},
            onFavoriteToggle = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 800)
@Composable
fun TabletPreview() {
    AplikacjeMobilneTheme {
        Row(Modifier.fillMaxSize()) {
            Box(Modifier.weight(1f)) {
                CocktailList(
                    navController = rememberNavController(),
                    cocktails = emptyList(),
                    showFavorites = false,
                    onCocktailSelected = {},
                    onMenuClick = {},
                    onFavoriteToggle = {}
                )
            }
            Box(Modifier.weight(2f)) {
                CocktailDetail(
                    navController = rememberNavController(),
                    cocktailName = "Mojito",
                    cocktails = emptyList(),
                    onMenuClick = {},
                    onFavoriteToggle = {}
                )
            }
        }
    }
}