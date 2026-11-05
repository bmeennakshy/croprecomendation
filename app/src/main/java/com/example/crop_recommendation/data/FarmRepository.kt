package com.example.crop_recommendation.data

import android.content.Context
import com.example.crop_recommendation.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FarmRepository(context: Context) {
    private val sessionManager = SessionManager(context)
    val currentUser: StateFlow<User?> = sessionManager.currentUser

    // All India Locations Seed
    val indiaLocations = listOf(
        "Ludhiana, Punjab",
        "Amritsar, Punjab",
        "Pune, Maharashtra",
        "Nagpur, Maharashtra",
        "Nashik, Maharashtra",
        "Lucknow, Uttar Pradesh",
        "Varanasi, Uttar Pradesh",
        "Chennai, Tamil Nadu",
        "Coimbatore, Tamil Nadu",
        "Madurai, Tamil Nadu",
        "Vellore, Tamil Nadu",
        "Bengaluru, Karnataka",
        "Mysuru, Karnataka",
        "Ahmedabad, Gujarat",
        "Surat, Gujarat",
        "Indore, Madhya Pradesh",
        "Bhopal, Madhya Pradesh",
        "Kolkata, West Bengal",
        "Patna, Bihar",
        "Jaipur, Rajasthan",
        "Karnal, Haryana",
        "Hyderabad, Telangana",
        "Vijayawada, Andhra Pradesh",
        "Kochi, Kerala",
        "Bhubaneswar, Odisha",
        "Guwahati, Assam"
    )
    val cropCategories = listOf("All", "Cereals", "Vegetables", "Fruits", "Spices", "Cash Crops", "Flowers")
    val educationalCategories = listOf("All", "Soil Guide", "Crop Guide", "Seasonal Farming", "Sustainable Farming", "Water Management")

    // In-memory demo users store (Email -> Pair(User, Password))
    private val demoUsers = mutableMapOf<String, Pair<User, String>>(
        "farmer@farm2market.com" to Pair(
            User("u1", "Murugan Farmer", "farmer@farm2market.com", UserRole.FARMER, "Vellore", "+91 9876543210"),
            "password123"
        ),
        "vendor@farm2market.com" to Pair(
            User("u2", "Kumar Vendor", "vendor@farm2market.com", UserRole.VENDOR, "Chennai", "+91 9123456789"),
            "password123"
        ),
        "farmer@demo.com" to Pair(
            User("u1", "Murugan Farmer", "farmer@demo.com", UserRole.FARMER, "Vellore", "+91 9876543210"),
            "password"
        ),
        "vendor@demo.com" to Pair(
            User("u2", "Kumar Vendor", "vendor@demo.com", UserRole.VENDOR, "Chennai", "+91 9123456789"),
            "password"
        )
    )

    private val _listings = MutableStateFlow<List<MarketplaceListing>>(
        listOf(
            MarketplaceListing(
                id = "l1",
                farmerId = "u1",
                farmerName = "Murugan Farmer",
                cropName = "Samba Rice (Paddy)",
                category = "Cereals",
                quantityKg = 500.0,
                pricePerKg = 42.0,
                location = "Vellore, Tamil Nadu",
                description = "High quality organic Samba rice freshly harvested from Vellore delta.",
                active = true
            ),
            MarketplaceListing(
                id = "l2",
                farmerId = "u1",
                farmerName = "Murugan Farmer",
                cropName = "Sugarcane",
                category = "Cash Crops",
                quantityKg = 1200.0,
                pricePerKg = 3.5,
                location = "Vellore, Tamil Nadu",
                description = "Sweet high-sucrose sugarcane ready for jaggery or processing.",
                active = true
            ),
            MarketplaceListing(
                id = "l3",
                farmerId = "u3",
                farmerName = "Selvam Farm",
                cropName = "Coimbatore Tomato",
                category = "Vegetables",
                quantityKg = 250.0,
                pricePerKg = 28.0,
                location = "Coimbatore, Tamil Nadu",
                description = "Fresh farm-grade juicy red tomatoes packed securely.",
                active = true
            ),
            MarketplaceListing(
                id = "l4",
                farmerId = "u4",
                farmerName = "Meenakshi Agro",
                cropName = "Madurai Malli (Jasmine)",
                category = "Flowers",
                quantityKg = 50.0,
                pricePerKg = 350.0,
                location = "Madurai, Tamil Nadu",
                description = "Aromatic fresh Madurai jasmine flowers for vendors & exporters.",
                active = true
            ),
            MarketplaceListing(
                id = "l5",
                farmerId = "u5",
                farmerName = "Gurpreet Singh",
                cropName = "Premium Sharbati Wheat",
                category = "Cereals",
                quantityKg = 1000.0,
                pricePerKg = 32.0,
                location = "Ludhiana, Punjab",
                description = "Golden grains of Sharbati wheat grown in fertile alluvial plains of Punjab.",
                active = true
            ),
            MarketplaceListing(
                id = "l6",
                farmerId = "u6",
                farmerName = "Rajesh Patil",
                cropName = "Nagpur Organic Oranges",
                category = "Fruits",
                quantityKg = 400.0,
                pricePerKg = 60.0,
                location = "Nagpur, Maharashtra",
                description = "Juicy, vitamin-rich organic Nagpur mandarins direct from orchard.",
                active = true
            ),
            MarketplaceListing(
                id = "l7",
                farmerId = "u7",
                farmerName = "Amitabh Sharma",
                cropName = "Lucknowi Basmati Rice",
                category = "Cereals",
                quantityKg = 800.0,
                pricePerKg = 85.0,
                location = "Lucknow, Uttar Pradesh",
                description = "Aromatic long-grain Basmati rice with exquisite aroma and taste.",
                active = true
            ),
            MarketplaceListing(
                id = "l8",
                farmerId = "u8",
                farmerName = "Gowda Farms",
                cropName = "Mysuru Coffee Beans",
                category = "Cash Crops",
                quantityKg = 150.0,
                pricePerKg = 450.0,
                location = "Mysuru, Karnataka",
                description = "Shade-grown Arabica coffee beans harvested from Western Ghats foothills.",
                active = true
            )
        )
    )
    val listings: StateFlow<List<MarketplaceListing>> = _listings.asStateFlow()

    private val _purchaseRequests = MutableStateFlow<List<PurchaseRequest>>(
        listOf(
            PurchaseRequest(
                id = "r1",
                listingId = "l1",
                cropName = "Samba Rice (Paddy)",
                vendorId = "u2",
                vendorName = "Kumar Vendor",
                farmerId = "u1",
                quantityKg = 100.0,
                offeredPricePerKg = 40.0,
                status = RequestStatus.PENDING
            ),
            PurchaseRequest(
                id = "r2",
                listingId = "l2",
                cropName = "Sugarcane",
                vendorId = "u2",
                vendorName = "Kumar Vendor",
                farmerId = "u1",
                quantityKg = 300.0,
                offeredPricePerKg = 3.2,
                status = RequestStatus.COUNTEROFFER_RECEIVED,
                counterOfferPricePerKg = 3.4
            )
        )
    )
    val purchaseRequests: StateFlow<List<PurchaseRequest>> = _purchaseRequests.asStateFlow()

    private val _recommendations = MutableStateFlow<List<CropRecommendation>>(
        listOf(
            CropRecommendation(
                id = "rec1",
                farmerId = "u1",
                cropName = "Rice (Paddy)",
                confidence = 0.94f,
                soilType = "Clayey / Alluvial",
                pH = 6.5f,
                nitrogen = 110,
                phosphorus = 45,
                potassium = 50,
                advice = "Ideal soil conditions for Paddy in Vellore region. Maintain water level at 5cm during tillering stage."
            )
        )
    )
    val recommendations: StateFlow<List<CropRecommendation>> = _recommendations.asStateFlow()

    private val _articles = MutableStateFlow<List<EducationalArticle>>(
        listOf(
            EducationalArticle(
                id = "a1",
                title = "Comprehensive Soil Guide: Tamil Nadu Soil Types & NPK Balance",
                category = "Soil Guide",
                summary = "Detailed breakdown of alluvial, black cotton, and red soils across Vellore, Coimbatore, and delta districts.",
                content = "Tamil Nadu features diverse soil profiles crucial for agricultural planning.\n\n" +
                        "1. Alluvial Soils: Found in river deltas (Cauvery basin). Rich in lime, potash, and phosphoric acid. Ideal for Samba paddy, sugarcane, and banana.\n\n" +
                        "2. Black Cotton Soils (Regur): Found in Coimbatore, Madurai, and Tirunelveli. High moisture retention capacity. Excellent for cotton, pulses, and sorghum.\n\n" +
                        "3. Red Loam & Sandy Soils: Found in Vellore, Salem, and Dharmapuri. Requires organic enrichment (FYM, compost) for high crop performance.\n\n" +
                        "Soil Testing & pH Management:\nMaintain soil pH between 6.0 and 7.5. Apply agricultural lime for acidic soils and gypsum for sodic soils to maximize nutrient uptake.",
                author = "Dr. R. Ramanathan, Senior Agronomist",
                readTimeMin = 5
            ),
            EducationalArticle(
                id = "a2",
                title = "Master Crop Guide: High-Yield Cultivation of Paddy, Sugarcane & Millets",
                category = "Crop Guide",
                summary = "Step-by-step agronomy guide from seed selection and nursery preparation to harvest and storage.",
                content = "Successful crop production depends on following scientific crop schedules:\n\n" +
                        "- Samba Paddy: Sow in August-September. Keep 5 cm water level during tillering stage. Treat seeds with Pseudomonas fluorescens.\n\n" +
                        "- Sugarcane: Plant 3-node disease-free setts with 90 cm row-to-row spacing. Irrigate every 7-10 days.\n\n" +
                        "- Millets (Ragi & Kuthiraivali): Drought-tolerant crops requiring minimal irrigation, highly suitable for dryland farming regions.",
                author = "Anitha Sundaram, Crop Scientist",
                readTimeMin = 6
            ),
            EducationalArticle(
                id = "a3",
                title = "Seasonal Farming Calendar: Navigating Kharif, Rabi & Monsoons",
                category = "Seasonal Farming",
                summary = "Aligning crop sowing with South-West and North-East monsoons for maximum productivity in Tamil Nadu.",
                content = "Tamil Nadu agriculture relies on two primary monsoons:\n\n" +
                        "- Kuruvai Season (June - October): Short-term paddy in Cauvery delta.\n\n" +
                        "- Samba Season (August - January): Long-term paddy, sugarcane, and cotton.\n\n" +
                        "- Navarai Season (January - April): Vegetables, pulses, and green gram.\n\n" +
                        "Monsoon Preparedness:\nEnsure proper field drainage channels before North-East monsoon downpours to prevent waterlogging and root rot.",
                author = "K. K. Selvan, Agricultural Meteorologist",
                readTimeMin = 5
            ),
            EducationalArticle(
                id = "a4",
                title = "Sustainable Farming: Natural Pest Control & Soil Microbiology",
                category = "Sustainable Farming",
                summary = "Eco-friendly practices to eliminate chemical dependency, preserve soil microflora, and boost profits.",
                content = "Transitioning to sustainable agriculture protects long-term land fertility:\n\n" +
                        "- Natural Pest Management (NPM): Use Neem oil spray (5ml/L) and Panchagavya for insect deterrence.\n\n" +
                        "- Crop Rotation: Alternate cereal crops with leguminous pulses to naturally fix atmospheric nitrogen.\n\n" +
                        "- Green Manuring: Grow sunnhemp (Dhaincha) and incorporate into soil before flowering.",
                author = "Dr. V. Meenakshi, Sustainable Agro-Ecologist",
                readTimeMin = 6
            ),
            EducationalArticle(
                id = "a5",
                title = "Modern Drip Irrigation Techniques & Government Subsidies",
                category = "Water Management",
                summary = "Learn how micro-irrigation saves up to 50% water while boosting crop yields in arid districts.",
                content = "Drip irrigation delivers water directly to plant roots, minimizing evaporation. Subsidies up to 100% for small and marginal farmers are available through the Tamil Nadu Horticulture Department under PMKSY.",
                author = "Dr. R. Ramanathan, Agronomist",
                readTimeMin = 4
            ),
            EducationalArticle(
                id = "a6",
                title = "Soil Health Card Scheme & NPK Optimization",
                category = "Soil Guide",
                summary = "Interpreting your soil test reports for Vellore, Coimbatore, and Madurai farms.",
                content = "Balanced NPK application prevents nutrient lockout. Test your soil every 2 years before sowing season to know exact nitrogen, phosphorus, and potassium requirements.",
                author = "K. K. Selvan",
                readTimeMin = 3
            )
        )
    )
    val articles: StateFlow<List<EducationalArticle>> = _articles.asStateFlow()

    // Auth functions
    fun login(email: String, pass: String): Result<User> {
        val entry = demoUsers[email.trim().lowercase()]
        if (entry != null && entry.second == pass) {
            sessionManager.saveUser(entry.first)
            return Result.success(entry.first)
        }
        return Result.failure(IllegalArgumentException("Invalid email or password. Try demo credentials."))
    }

    fun signUp(name: String, email: String, pass: String, role: UserRole, location: String, phone: String): Result<User> {
        val cleanEmail = email.trim().lowercase()
        if (demoUsers.containsKey(cleanEmail)) {
            return Result.failure(IllegalArgumentException("Email already registered."))
        }
        val newUser = User(
            id = "u_${System.currentTimeMillis()}",
            name = name,
            email = cleanEmail,
            role = role,
            location = location,
            phone = phone
        )
        demoUsers[cleanEmail] = Pair(newUser, pass)
        sessionManager.saveUser(newUser)
        return Result.success(newUser)
    }

    fun logout() {
        sessionManager.clearSession()
    }

    // Marketplace actions
    fun addListing(listing: MarketplaceListing) {
        _listings.value = listOf(listing) + _listings.value
    }

    fun updateListing(updated: MarketplaceListing) {
        _listings.value = _listings.value.map { if (it.id == updated.id) updated else it }
    }

    fun deleteListing(listingId: String) {
        _listings.value = _listings.value.filter { it.id != listingId }
    }

    // Purchase Request actions
    fun createPurchaseRequest(request: PurchaseRequest) {
        _purchaseRequests.value = listOf(request) + _purchaseRequests.value
    }

    fun updateRequestStatus(requestId: String, newStatus: RequestStatus, counterOffer: Double? = null) {
        val req = _purchaseRequests.value.find { it.id == requestId }
        if (req != null && req.status != newStatus) {
            if (newStatus == RequestStatus.ACCEPTED && req.status != RequestStatus.ACCEPTED) {
                val listing = _listings.value.find { it.id == req.listingId }
                if (listing != null) {
                    val updatedQty = (listing.quantityKg - req.quantityKg).coerceAtLeast(0.0)
                    updateListing(listing.copy(quantityKg = updatedQty, active = updatedQty > 0.0))
                }
            }
        }
        _purchaseRequests.value = _purchaseRequests.value.map { r ->
            if (r.id == requestId) {
                r.copy(
                    status = newStatus,
                    counterOfferPricePerKg = counterOffer ?: r.counterOfferPricePerKg
                )
            } else {
                r
            }
        }
    }

    // Recommendations actions
    fun addRecommendation(rec: CropRecommendation) {
        _recommendations.value = listOf(rec) + _recommendations.value
    }

    fun generateAiRecommendation(location: String, soilType: String, pH: Float, n: Int, p: Int, k: Int): CropRecommendation {
        val (crop, conf, advice) = when {
            soilType.contains("Clay", ignoreCase = true) || n > 90 -> 
                Triple("Samba Rice (Paddy)", 0.96f, "High nitrogen & clay soil retention is optimal for high-yield paddy cultivation in $location.")
            soilType.contains("Black", ignoreCase = true) || k > 40 -> 
                Triple("Coimbatore Cotton / Sugarcane", 0.92f, "Black cotton soil with balanced potassium supports strong fiber & sugar development.")
            soilType.contains("Red", ignoreCase = true) -> 
                Triple("Millets & Groundnut", 0.89f, "Red soil drains well; ideal for drought-tolerant millets and oilseeds.")
            else -> 
                Triple("Organic Vegetables (Tomato/Bhendi)", 0.91f, "Balanced nutrient profile suitable for intensive horticulture and market gardening in $location.")
        }
        val rec = CropRecommendation(
            id = "rec_${System.currentTimeMillis()}",
            farmerId = currentUser.value?.id ?: "u1",
            cropName = crop,
            confidence = conf,
            soilType = soilType,
            pH = pH,
            nitrogen = n,
            phosphorus = p,
            potassium = k,
            advice = advice
        )
        addRecommendation(rec)
        return rec
    }
}
