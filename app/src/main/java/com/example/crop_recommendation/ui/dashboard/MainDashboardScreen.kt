package com.example.crop_recommendation.ui.dashboard

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.crop_recommendation.data.FarmRepository
import com.example.crop_recommendation.model.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainDashboardScreen(
    repository: FarmRepository,
    onLogout: () -> Unit
) {
    val currentUser by repository.currentUser.collectAsState()
    val listings by repository.listings.collectAsState()
    val requests by repository.purchaseRequests.collectAsState()
    val articles by repository.articles.collectAsState()
    val recommendations by repository.recommendations.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }

    // Dialog states
    var showAddListingDialog by remember { mutableStateOf(false) }
    var selectedListingForPurchase by remember { mutableStateOf<MarketplaceListing?>(null) }
    var selectedArticle by remember { mutableStateOf<EducationalArticle?>(null) }
    var selectedRequestForCounter by remember { mutableStateOf<PurchaseRequest?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Farm2Market")
                        Text(
                            text = "${currentUser?.name ?: ""} (${currentUser?.role?.name ?: ""})",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Rounded.ExitToApp, contentDescription = "Logout")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            if (selectedTab == 0 && currentUser?.role == UserRole.FARMER) {
                FloatingActionButton(
                    onClick = { showAddListingDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = "Add Crop Listing")
                }
            }
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Rounded.Store, contentDescription = null) },
                    label = { Text("Market") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Rounded.Agriculture, contentDescription = null) },
                    label = { Text("Crops & Yield") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
                NavigationBarItem(
                    icon = { 
                        BadgedBox(
                            badge = {
                                val pendingCount = requests.count { req ->
                                    currentUser?.let { user ->
                                        if (user.role == UserRole.FARMER) req.farmerId == user.id && req.status == RequestStatus.PENDING
                                        else req.vendorId == user.id && (req.status == RequestStatus.COUNTEROFFER_RECEIVED || req.status == RequestStatus.ACCEPTED)
                                    } ?: false
                                }
                                if (pendingCount > 0) {
                                    Badge { Text("$pendingCount") }
                                }
                            }
                        ) {
                            Icon(Icons.Rounded.ListAlt, contentDescription = null)
                        }
                    },
                    label = { Text("Requests") },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Rounded.MenuBook, contentDescription = null) },
                    label = { Text("Learn") },
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            when (selectedTab) {
                0 -> MarketplaceTab(
                    listings = listings,
                    currentUser = currentUser,
                    repository = repository,
                    onRequestPurchase = { listing -> selectedListingForPurchase = listing }
                )
                1 -> CropsAndYieldTab(
                    recommendations = recommendations,
                    repository = repository,
                    currentUser = currentUser
                )
                2 -> RequestsTab(
                    requests = requests,
                    currentUser = currentUser,
                    repository = repository,
                    onOpenCounterDialog = { req -> selectedRequestForCounter = req }
                )
                3 -> LearnTab(
                    articles = articles,
                    categories = repository.educationalCategories,
                    onArticleClick = { article -> selectedArticle = article }
                )
            }
        }

        // Add Listing Dialog for Farmers
        if (showAddListingDialog) {
            AddListingDialog(
                currentUser = currentUser,
                repository = repository,
                onDismiss = { showAddListingDialog = false },
                onSubmit = { newListing ->
                    repository.addListing(newListing)
                    showAddListingDialog = false
                }
            )
        }

        // Purchase / Negotiate Dialog for Vendors
        selectedListingForPurchase?.let { listing ->
            PurchaseDialog(
                listing = listing,
                currentUser = currentUser,
                onDismiss = { selectedListingForPurchase = null },
                onSubmitRequest = { qty, price ->
                    val request = PurchaseRequest(
                        id = "req_${System.currentTimeMillis()}",
                        listingId = listing.id,
                        cropName = listing.cropName,
                        vendorId = currentUser?.id ?: "u2",
                        vendorName = currentUser?.name ?: "Vendor",
                        farmerId = listing.farmerId,
                        quantityKg = qty,
                        offeredPricePerKg = price,
                        status = RequestStatus.PENDING
                    )
                    repository.createPurchaseRequest(request)
                    selectedListingForPurchase = null
                }
            )
        }

        // Counter Offer Dialog for Farmers
        selectedRequestForCounter?.let { req ->
            CounterOfferDialog(
                request = req,
                onDismiss = { selectedRequestForCounter = null },
                onSubmitCounter = { counterPrice ->
                    repository.updateRequestStatus(req.id, RequestStatus.COUNTEROFFER_RECEIVED, counterPrice)
                    selectedRequestForCounter = null
                }
            )
        }

        // Article Detail Dialog
        selectedArticle?.let { article ->
            ArticleDetailDialog(
                article = article,
                onDismiss = { selectedArticle = null }
            )
        }
    }
}

@Composable
fun MarketplaceTab(
    listings: List<MarketplaceListing>,
    currentUser: User?,
    repository: FarmRepository,
    onRequestPurchase: (MarketplaceListing) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedLocation by remember { mutableStateOf("All") }
    var minQuantityFilter by remember { mutableStateOf("") }

    val categories = repository.cropCategories
    val locations = listOf("All") + repository.indiaLocations

    val filteredListings = listings.filter { listing ->
        val matchesSearch = listing.cropName.contains(searchQuery, ignoreCase = true) || 
                            listing.location.contains(searchQuery, ignoreCase = true) ||
                            listing.farmerName.contains(searchQuery, ignoreCase = true) ||
                            listing.description.contains(searchQuery, ignoreCase = true)
        
        val matchesCategory = selectedCategory == "All" || listing.category.equals(selectedCategory, ignoreCase = true)
        val matchesLocation = selectedLocation == "All" || listing.location.equals(selectedLocation, ignoreCase = true)
        
        val minQty = minQuantityFilter.toDoubleOrNull() ?: 0.0
        val matchesQuantity = listing.quantityKg >= minQty

        matchesSearch && matchesCategory && matchesLocation && matchesQuantity && listing.active
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text("All India Agricultural Marketplace", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search crops, location, description...") },
            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Category Filter Chips
        Text("Category:", style = MaterialTheme.typography.labelMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { cat ->
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = { selectedCategory = cat },
                    label = { Text(cat) }
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Location & Quantity Filters Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Location Filter
            var expandedLoc by remember { mutableStateOf(false) }
            Box(modifier = Modifier.weight(1f)) {
                OutlinedButton(
                    onClick = { expandedLoc = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Rounded.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Loc: $selectedLocation", maxLines = 1)
                }
                DropdownMenu(expanded = expandedLoc, onDismissRequest = { expandedLoc = false }) {
                    locations.forEach { loc ->
                        DropdownMenuItem(
                            text = { Text(loc) },
                            onClick = {
                                selectedLocation = loc
                                expandedLoc = false
                            }
                        )
                    }
                }
            }

            // Minimum Quantity Filter Input
            OutlinedTextField(
                value = minQuantityFilter,
                onValueChange = { minQuantityFilter = it },
                label = { Text("Min Qty (kg)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        if (filteredListings.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No matching crop listings found.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredListings) { listing ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = listing.cropName, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                                AssistChip(onClick = {}, label = { Text("📍 ${listing.location} • ${listing.category}") })
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Farmer: ${listing.farmerName}", style = MaterialTheme.typography.bodyMedium)
                            Text(text = "Available: ${listing.quantityKg} kg  •  Price: ₹${listing.pricePerKg}/kg", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.secondary)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = listing.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(12.dp))
                            if (currentUser?.role == UserRole.VENDOR) {
                                Button(
                                    onClick = { onRequestPurchase(listing) },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Rounded.ShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Request Purchase / Negotiate")
                                }
                            } else {
                                Text(
                                    text = if (listing.farmerId == currentUser?.id) "Your Active Listing" else "Farmer View",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CropsAndYieldTab(
    recommendations: List<CropRecommendation>,
    repository: FarmRepository,
    currentUser: User?
) {
    var soilType by remember { mutableStateOf("Clayey / Alluvial") }
    var pHStr by remember { mutableStateOf("6.5") }
    var nitrogenStr by remember { mutableStateOf("110") }
    var phosphorusStr by remember { mutableStateOf("45") }
    var potassiumStr by remember { mutableStateOf("50") }
    var lastGeneratedRec by remember { mutableStateOf<CropRecommendation?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Yield Management Summary Card
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("🌾 Yield Management & Soil Health", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSecondaryContainer)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Monitor district soil status, optimal fertilizer schedules, and expected harvest output across Tamil Nadu agrarian zones.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSecondaryContainer)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("District: ${currentUser?.location ?: "Vellore"}", style = MaterialTheme.typography.labelLarge)
                    Text("Soil Rating: Optimal (A+)", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        // AI Crop Recommendation Generator Form
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("🧠 AI Smart Crop Recommendation Engine", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = soilType,
                    onValueChange = { soilType = it },
                    label = { Text("Soil Type (e.g. Clayey, Black Cotton, Red)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = pHStr,
                        onValueChange = { pHStr = it },
                        label = { Text("pH Level") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = nitrogenStr,
                        onValueChange = { nitrogenStr = it },
                        label = { Text("Nitrogen (N)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = phosphorusStr,
                        onValueChange = { phosphorusStr = it },
                        label = { Text("Phosphorus (P)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = potassiumStr,
                        onValueChange = { potassiumStr = it },
                        label = { Text("Potassium (K)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        val pH = pHStr.toFloatOrNull() ?: 6.5f
                        val n = nitrogenStr.toIntOrNull() ?: 100
                        val p = phosphorusStr.toIntOrNull() ?: 40
                        val k = potassiumStr.toIntOrNull() ?: 45
                        val rec = repository.generateAiRecommendation(currentUser?.location ?: "Vellore", soilType, pH, n, p, k)
                        lastGeneratedRec = rec
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Rounded.AutoAwesome, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Generate AI Crop Recommendation")
                }

                lastGeneratedRec?.let { rec ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("✨ Recommended: ${rec.cropName} (${(rec.confidence * 100).toInt()}% Match)", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(rec.advice, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                    }
                }
            }
        }

        Text("Saved Recommendations History", style = MaterialTheme.typography.titleMedium)
        recommendations.forEach { rec ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "${rec.cropName} (${(rec.confidence * 100).toInt()}% Confidence)", style = MaterialTheme.typography.titleSmall)
                    Text(text = "Soil: ${rec.soilType} | pH: ${rec.pH} | NPK: N=${rec.nitrogen}, P=${rec.phosphorus}, K=${rec.potassium}")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = rec.advice, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
fun RequestsTab(
    requests: List<PurchaseRequest>,
    currentUser: User?,
    repository: FarmRepository,
    onOpenCounterDialog: (PurchaseRequest) -> Unit
) {
    var selectedRequestTab by remember { mutableIntStateOf(0) } // 0: Active, 1: Completed / History

    val relevantRequests = requests.filter { req ->
        currentUser?.let { user ->
            if (user.role == UserRole.FARMER) req.farmerId == user.id || req.farmerId == "u1"
            else req.vendorId == user.id || req.vendorId == "u2"
        } ?: true
    }

    val activeRequests = relevantRequests.filter { 
        it.status == RequestStatus.PENDING || it.status == RequestStatus.ACCEPTED || it.status == RequestStatus.COUNTEROFFER_RECEIVED 
    }
    val completedRequests = relevantRequests.filter { 
        it.status == RequestStatus.COMPLETED || it.status == RequestStatus.REJECTED || it.status == RequestStatus.CANCELLED 
    }

    val displayedRequests = if (selectedRequestTab == 0) activeRequests else completedRequests

    Column(modifier = Modifier.fillMaxSize()) {
        Text("Purchase Requests & Interactive Negotiations", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        // Active vs Completed Tabs
        TabRow(selectedTabIndex = selectedRequestTab) {
            Tab(
                selected = selectedRequestTab == 0,
                onClick = { selectedRequestTab = 0 },
                text = { Text("Active (${activeRequests.size})") }
            )
            Tab(
                selected = selectedRequestTab == 1,
                onClick = { selectedRequestTab = 1 },
                text = { Text("History (${completedRequests.size})") }
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        if (displayedRequests.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    if (selectedRequestTab == 0) "No active purchase requests." else "No request history found.", 
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(displayedRequests) { req ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = req.cropName, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                                
                                // Status Badge with Color
                                val (badgeText, badgeColor) = when (req.status) {
                                    RequestStatus.PENDING -> "Pending" to Color(0xFFF57C00) // Orange
                                    RequestStatus.ACCEPTED -> "Accepted" to Color(0xFF388E3C) // Green
                                    RequestStatus.REJECTED -> "Rejected" to Color(0xFFD32F2F) // Red
                                    RequestStatus.COUNTEROFFER_RECEIVED -> "Counteroffer" to Color(0xFF7B1FA2) // Purple
                                    RequestStatus.COMPLETED -> "Completed" to Color(0xFF00796B) // Teal
                                    RequestStatus.CANCELLED -> "Cancelled" to Color(0xFF616161) // Grey
                                }
                                AssistChip(
                                    onClick = {},
                                    label = { Text(badgeText, color = badgeColor) },
                                    colors = AssistChipDefaults.assistChipColors(containerColor = badgeColor.copy(alpha = 0.15f))
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Vendor: ${req.vendorName}", style = MaterialTheme.typography.bodyMedium)
                            Text(text = "Quantity: ${req.quantityKg} kg  •  Offered: ₹${req.offeredPricePerKg}/kg", style = MaterialTheme.typography.bodyMedium)
                            
                            if (req.counterOfferPricePerKg != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "🔄 Farmer Counteroffer: ₹${req.counterOfferPricePerKg}/kg",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Role-based Action Buttons
                            if (currentUser?.role == UserRole.FARMER) {
                                if (req.status == RequestStatus.PENDING || req.status == RequestStatus.COUNTEROFFER_RECEIVED) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = { repository.updateRequestStatus(req.id, RequestStatus.ACCEPTED) },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                        ) {
                                            Text("Accept")
                                        }
                                        OutlinedButton(
                                            onClick = { onOpenCounterDialog(req) },
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Counter")
                                        }
                                        OutlinedButton(
                                            onClick = { repository.updateRequestStatus(req.id, RequestStatus.REJECTED) },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                                        ) {
                                            Text("Reject")
                                        }
                                    }
                                } else if (req.status == RequestStatus.ACCEPTED) {
                                    OutlinedButton(
                                        onClick = { repository.updateRequestStatus(req.id, RequestStatus.CANCELLED) },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Cancel Request")
                                    }
                                }
                            } else {
                                // Vendor Actions
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (req.status == RequestStatus.COUNTEROFFER_RECEIVED) {
                                        Button(
                                            onClick = { repository.updateRequestStatus(req.id, RequestStatus.ACCEPTED) },
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Accept Counter")
                                        }
                                    }
                                    if (req.status == RequestStatus.ACCEPTED) {
                                        Button(
                                            onClick = { repository.updateRequestStatus(req.id, RequestStatus.COMPLETED) },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                                        ) {
                                            Text("Mark Completed")
                                        }
                                    }
                                    if (req.status == RequestStatus.PENDING || req.status == RequestStatus.ACCEPTED || req.status == RequestStatus.COUNTEROFFER_RECEIVED) {
                                        OutlinedButton(
                                            onClick = { repository.updateRequestStatus(req.id, RequestStatus.CANCELLED) },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                                        ) {
                                            Text("Cancel")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LearnTab(
    articles: List<EducationalArticle>,
    categories: List<String>,
    onArticleClick: (EducationalArticle) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val filteredArticles = remember(articles, searchQuery, selectedCategory) {
        articles.filter { article ->
            val matchesCategory = selectedCategory == "All" || article.category.equals(selectedCategory, ignoreCase = true)
            val matchesSearch = searchQuery.isBlank() ||
                    article.title.contains(searchQuery, ignoreCase = true) ||
                    article.summary.contains(searchQuery, ignoreCase = true) ||
                    article.content.contains(searchQuery, ignoreCase = true) ||
                    article.category.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Learn Farming & Educational Guides", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search Soil Guide, Crop Guide, Seasonal...") },
            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotBlank()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Rounded.Clear, contentDescription = "Clear search")
                    }
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Category Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(categories) { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { selectedCategory = category },
                    label = { Text(category) },
                    leadingIcon = if (selectedCategory == category) {
                        { Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    } else null
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (filteredArticles.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Rounded.MenuBook, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.outline)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No guides found matching \"$searchQuery\"", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(filteredArticles) { article ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onArticleClick(article) },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = article.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                AssistChip(
                                    onClick = {},
                                    label = { Text("⏱️ ${article.readTimeMin}m") }
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Category: ${article.category} • By ${article.author}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = article.summary,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddListingDialog(
    currentUser: User?,
    repository: FarmRepository,
    onDismiss: () -> Unit,
    onSubmit: (MarketplaceListing) -> Unit
) {
    var cropName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Cereals") }
    var quantityKgStr by remember { mutableStateOf("") }
    var pricePerKgStr by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    val categories = repository.cropCategories.filter { it != "All" }
    var expandedCategory by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Produce Listing (${currentUser?.name ?: "Farmer"})") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = cropName,
                    onValueChange = { cropName = it },
                    label = { Text("Crop Name (e.g. Banana, Turmeric)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Category Dropdown Selection
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { expandedCategory = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Category: $selectedCategory")
                    }
                    DropdownMenu(expanded = expandedCategory, onDismissRequest = { expandedCategory = false }) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    selectedCategory = cat
                                    expandedCategory = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = quantityKgStr,
                    onValueChange = { quantityKgStr = it },
                    label = { Text("Quantity (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = pricePerKgStr,
                    onValueChange = { pricePerKgStr = it },
                    label = { Text("Price per kg (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description & Quality Details") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val qty = quantityKgStr.toDoubleOrNull() ?: 100.0
                    val price = pricePerKgStr.toDoubleOrNull() ?: 20.0
                    val listing = MarketplaceListing(
                        id = "l_${System.currentTimeMillis()}",
                        farmerId = currentUser?.id ?: "u1",
                        farmerName = currentUser?.name ?: "Murugan Farmer",
                        cropName = cropName.ifBlank { "Organic Crop" },
                        category = selectedCategory,
                        quantityKg = qty,
                        pricePerKg = price,
                        location = currentUser?.location ?: "Vellore",
                        description = description.ifBlank { "Fresh farm produce from across India." }
                    )
                    onSubmit(listing)
                }
            ) {
                Text("Publish Listing")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun PurchaseDialog(
    listing: MarketplaceListing,
    currentUser: User?,
    onDismiss: () -> Unit,
    onSubmitRequest: (Double, Double) -> Unit
) {
    var qtyStr by remember { mutableStateOf("100") }
    var priceStr by remember { mutableStateOf(listing.pricePerKg.toString()) }

    val requestedQty = qtyStr.toDoubleOrNull() ?: 0.0
    val isQuantityValid = requestedQty > 0.0 && requestedQty <= listing.quantityKg

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Purchase / Negotiate: ${listing.cropName}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Farmer: ${listing.farmerName} (${listing.location})", style = MaterialTheme.typography.bodyMedium)
                Text("Listed Price: ₹${listing.pricePerKg}/kg (Max Available: ${listing.quantityKg} kg)", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = qtyStr,
                    onValueChange = { qtyStr = it },
                    label = { Text("Requested Quantity (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    isError = !isQuantityValid && qtyStr.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                )
                if (!isQuantityValid && qtyStr.isNotBlank()) {
                    Text(
                        text = "Quantity must be between 1 and ${listing.quantityKg} kg",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                OutlinedTextField(
                    value = priceStr,
                    onValueChange = { priceStr = it },
                    label = { Text("Your Offered Price per kg (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val qty = qtyStr.toDoubleOrNull() ?: 50.0
                    val price = priceStr.toDoubleOrNull() ?: listing.pricePerKg
                    onSubmitRequest(qty, price)
                },
                enabled = isQuantityValid
            ) {
                Text("Send Request")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun CounterOfferDialog(
    request: PurchaseRequest,
    onDismiss: () -> Unit,
    onSubmitCounter: (Double) -> Unit
) {
    var counterPriceStr by remember { mutableStateOf(request.offeredPricePerKg.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Submit Counteroffer for ${request.cropName}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Vendor Offered: ₹${request.offeredPricePerKg}/kg for ${request.quantityKg} kg", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = counterPriceStr,
                    onValueChange = { counterPriceStr = it },
                    label = { Text("Counteroffer Price per kg (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val price = counterPriceStr.toDoubleOrNull() ?: request.offeredPricePerKg
                    onSubmitCounter(price)
                }
            ) {
                Text("Send Counteroffer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ArticleDetailDialog(
    article: EducationalArticle,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(article.title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AssistChip(onClick = {}, label = { Text(article.category) })
                    AssistChip(onClick = {}, label = { Text("⏱️ ${article.readTimeMin}m read") })
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Author: ${article.author}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
                HorizontalDivider()
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = article.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close Guide")
            }
        }
    )
}
