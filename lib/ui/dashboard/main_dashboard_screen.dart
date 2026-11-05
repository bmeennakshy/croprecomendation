import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../models/user.dart';
import '../../models/marketplace_listing.dart';
import '../../models/purchase_request.dart';
import '../../providers/app_provider.dart';

class MainDashboardScreen extends StatefulWidget {
  const MainDashboardScreen({super.key});

  @override
  State<MainDashboardScreen> createState() => _MainDashboardScreenState();
}

class _MainDashboardScreenState extends State<MainDashboardScreen> {
  int _currentIndex = 0;

  @override
  Widget build(BuildContext context) {
    final List<Widget> tabs = [
      const HomeOverviewTab(),
      const CropAdvisorTab(),
      const MarketplaceTab(),
      const PurchaseRequestsTab(),
      const KnowledgeHubTab(),
      const ProfileTab(),
    ];

    return Scaffold(
      body: tabs[_currentIndex],
      bottomNavigationBar: NavigationBar(
        selectedIndex: _currentIndex,
        onDestinationSelected: (index) => setState(() => _currentIndex = index),
        destinations: const [
          NavigationDestination(
            icon: Icon(Icons.home_outlined),
            selectedIcon: Icon(Icons.home),
            label: 'Home',
          ),
          NavigationDestination(
            icon: Icon(Icons.psychology_outlined),
            selectedIcon: Icon(Icons.psychology),
            label: 'AI Advisor',
          ),
          NavigationDestination(
            icon: Icon(Icons.storefront_outlined),
            selectedIcon: Icon(Icons.storefront),
            label: 'Marketplace',
          ),
          NavigationDestination(
            icon: Icon(Icons.receipt_long_outlined),
            selectedIcon: Icon(Icons.receipt_long),
            label: 'Orders',
          ),
          NavigationDestination(
            icon: Icon(Icons.menu_book_outlined),
            selectedIcon: Icon(Icons.menu_book),
            label: 'Knowledge',
          ),
          NavigationDestination(
            icon: Icon(Icons.person_outline),
            selectedIcon: Icon(Icons.person),
            label: 'Profile',
          ),
        ],
      ),
    );
  }
}

// 1. Home Overview Tab
class HomeOverviewTab extends StatelessWidget {
  const HomeOverviewTab({super.key});

  @override
  Widget build(BuildContext context) {
    final provider = Provider.of<AppProvider>(context);
    final user = provider.currentUser;
    final theme = Theme.of(context);

    return Scaffold(
      appBar: AppBar(
        title: const Text('Farm2Market India'),
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: () => provider.loadMarketData(),
          ),
        ],
      ),
      body: RefreshIndicator(
        onRefresh: () => provider.loadMarketData(),
        child: ListView(
          padding: const EdgeInsets.all(16.0),
          children: [
            Card(
              color: theme.colorScheme.primaryContainer,
              child: Padding(
                padding: const EdgeInsets.all(20.0),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Row(
                      children: [
                        CircleAvatar(
                          backgroundColor: theme.colorScheme.primary,
                          child: Text(
                            user?.name.substring(0, 1).toUpperCase() ?? 'A',
                            style: const TextStyle(color: Colors.white, fontWeight: FontWeight.bold),
                          ),
                        ),
                        const SizedBox(width: 12),
                        Expanded(
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Text(
                                'Welcome back, ${user?.name ?? 'User'}!',
                                style: theme.textTheme.titleLarge?.copyWith(fontWeight: FontWeight.bold),
                              ),
                              Text(
                                '${user?.role == UserRole.farmer ? 'Farmer' : 'Vendor'} • ${user?.location ?? 'All India'}',
                                style: theme.textTheme.bodyMedium,
                              ),
                            ],
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(height: 16),
                    Container(
                      padding: const EdgeInsets.all(12),
                      decoration: BoxDecoration(
                        color: Colors.white.withAlpha(150),
                        borderRadius: BorderRadius.circular(10),
                      ),
                      child: Row(
                        children: [
                          const Icon(Icons.wb_sunny, color: Colors.orange, size: 28),
                          const SizedBox(width: 12),
                          Expanded(
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                const Text('Weather Outlook: Sunny & Favorable', style: TextStyle(fontWeight: FontWeight.bold)),
                                Text('Location: ${user?.location ?? 'All India'} • Temp: 31°C • Humidity: 65%', style: theme.textTheme.bodySmall),
                              ],
                            ),
                          ),
                        ],
                      ),
                    ),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 20),
            Text('Quick Stats & Highlights', style: theme.textTheme.titleMedium?.copyWith(fontWeight: FontWeight.bold)),
            const SizedBox(height: 12),
            Row(
              children: [
                Expanded(
                  child: _StatCard(
                    title: 'Active Listings',
                    value: '${provider.listings.length}',
                    icon: Icons.storefront,
                    color: Colors.green,
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: _StatCard(
                    title: 'Purchase Requests',
                    value: '${provider.purchaseRequests.length}',
                    icon: Icons.assignment,
                    color: Colors.blue,
                  ),
                ),
              ],
            ),
            const SizedBox(height: 24),
            Text('Recent Marketplace Crops', style: theme.textTheme.titleMedium?.copyWith(fontWeight: FontWeight.bold)),
            const SizedBox(height: 12),
            ...provider.listings.take(3).map((listing) => Card(
                  margin: const EdgeInsets.only(bottom: 12),
                  child: ListTile(
                    leading: CircleAvatar(
                      backgroundColor: theme.colorScheme.secondaryContainer,
                      child: const Icon(Icons.grass, color: Colors.brown),
                    ),
                    title: Text(listing.cropName, style: const TextStyle(fontWeight: FontWeight.bold)),
                    subtitle: Text('${listing.category} • ${listing.location}\n₹${listing.pricePerKg}/kg • Qty: ${listing.quantityKg} kg'),
                    isThreeLine: true,
                    trailing: Chip(
                      label: Text(listing.active ? 'Available' : 'Sold'),
                      backgroundColor: listing.active ? Colors.green[100] : Colors.grey[300],
                    ),
                  ),
                )),
          ],
        ),
      ),
    );
  }
}

class _StatCard extends StatelessWidget {
  final String title;
  final String value;
  final IconData icon;
  final Color color;

  const _StatCard({required this.title, required this.value, required this.icon, required this.color});

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Icon(icon, color: color, size: 28),
            const SizedBox(height: 8),
            Text(value, style: const TextStyle(fontSize: 22, fontWeight: FontWeight.bold)),
            Text(title, style: TextStyle(color: Colors.grey[600], fontSize: 12)),
          ],
        ),
      ),
    );
  }
}

// 2. Crop Advisor AI Tab
class CropAdvisorTab extends StatefulWidget {
  const CropAdvisorTab({super.key});

  @override
  State<CropAdvisorTab> createState() => _CropAdvisorTabState();
}

class _CropAdvisorTabState extends State<CropAdvisorTab> {
  String _soilType = 'Alluvial';
  double _pH = 6.5;
  int _nitrogen = 90;
  int _phosphorus = 45;
  int _potassium = 50;

  final List<String> _soilTypes = ['Alluvial', 'Clayey', 'Red', 'Black', 'Laterite', 'Sandy'];

  @override
  Widget build(BuildContext context) {
    final provider = Provider.of<AppProvider>(context);
    final theme = Theme.of(context);
    final rec = provider.lastRecommendation;

    return Scaffold(
      appBar: AppBar(title: const Text('AI Crop Advisor')),
      body: ListView(
        padding: const EdgeInsets.all(16.0),
        children: [
          Text('Soil & Nutrient Analysis', style: theme.textTheme.titleMedium?.copyWith(fontWeight: FontWeight.bold)),
          const SizedBox(height: 8),
          const Text('Enter your soil laboratory test values to get AI-powered crop recommendations tailored for your region.'),
          const SizedBox(height: 16),
          DropdownButtonFormField<String>(
            initialValue: _soilType,
            decoration: InputDecoration(
              labelText: 'Soil Type',
              border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
            ),
            items: _soilTypes.map((s) => DropdownMenuItem(value: s, child: Text(s))).toList(),
            onChanged: (val) {
              if (val != null) setState(() => _soilType = val);
            },
          ),
          const SizedBox(height: 16),
          Text('Soil pH Level: ${_pH.toStringAsFixed(1)}', style: const TextStyle(fontWeight: FontWeight.bold)),
          Slider(
            value: _pH,
            min: 4.0,
            max: 9.0,
            divisions: 50,
            label: _pH.toStringAsFixed(1),
            onChanged: (val) => setState(() => _pH = val),
          ),
          const SizedBox(height: 8),
          Text('Nitrogen (N) kg/ha: $_nitrogen', style: const TextStyle(fontWeight: FontWeight.bold)),
          Slider(
            value: _nitrogen.toDouble(),
            min: 10,
            max: 200,
            divisions: 38,
            label: '$_nitrogen',
            onChanged: (val) => setState(() => _nitrogen = val.toInt()),
          ),
          const SizedBox(height: 8),
          Text('Phosphorus (P) kg/ha: $_phosphorus', style: const TextStyle(fontWeight: FontWeight.bold)),
          Slider(
            value: _phosphorus.toDouble(),
            min: 5,
            max: 150,
            divisions: 29,
            label: '$_phosphorus',
            onChanged: (val) => setState(() => _phosphorus = val.toInt()),
          ),
          const SizedBox(height: 8),
          Text('Potassium (K) kg/ha: $_potassium', style: const TextStyle(fontWeight: FontWeight.bold)),
          Slider(
            value: _potassium.toDouble(),
            min: 5,
            max: 150,
            divisions: 29,
            label: '$_potassium',
            onChanged: (val) => setState(() => _potassium = val.toInt()),
          ),
          const SizedBox(height: 24),
          ElevatedButton.icon(
            icon: const Icon(Icons.psychology),
            label: const Text('Generate AI Crop Recommendation'),
            onPressed: () {
              provider.getCropRecommendation(
                soilType: _soilType,
                pH: _pH,
                nitrogen: _nitrogen,
                phosphorus: _phosphorus,
                potassium: _potassium,
              );
              ScaffoldMessenger.of(context).showSnackBar(
                const SnackBar(content: Text('AI Recommendation Generated successfully!')),
              );
            },
          ),
          if (rec != null) ...[
            const SizedBox(height: 24),
            Card(
              color: Colors.green[50],
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(16),
                side: BorderSide(color: Colors.green.shade300),
              ),
              child: Padding(
                padding: const EdgeInsets.all(20.0),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Row(
                      children: [
                        const Icon(Icons.verified, color: Colors.green, size: 28),
                        const SizedBox(width: 8),
                        Text('Recommended Crop', style: theme.textTheme.titleMedium?.copyWith(fontWeight: FontWeight.bold)),
                        const Spacer(),
                        Chip(
                          label: Text('${rec.confidence.toStringAsFixed(1)}% Match'),
                          backgroundColor: Colors.green[200],
                        ),
                      ],
                    ),
                    const SizedBox(height: 12),
                    Text(rec.cropName, style: const TextStyle(fontSize: 22, fontWeight: FontWeight.bold, color: Color(0xFF1B5E20))),
                    const SizedBox(height: 8),
                    Text('Soil: ${rec.soilType} • pH: ${rec.pH} • NPK: ${rec.nitrogen}-${rec.phosphorus}-${rec.potassium}'),
                    const Divider(height: 20),
                    const Text('Agronomic Expert Advice:', style: TextStyle(fontWeight: FontWeight.bold)),
                    const SizedBox(height: 4),
                    Text(rec.advice),
                    const SizedBox(height: 16),
                    ElevatedButton(
                      onPressed: () {
                        showDialog(
                          context: context,
                          builder: (ctx) => AlertDialog(
                            title: const Text('Publish to B2B Marketplace?'),
                            content: Text('Do you want to list ${rec.cropName} on the Marketplace for vendors across India?'),
                            actions: [
                              TextButton(
                                onPressed: () => Navigator.pop(ctx),
                                child: const Text('Cancel'),
                              ),
                              ElevatedButton(
                                onPressed: () {
                                  provider.addListing(
                                    cropName: rec.cropName,
                                    category: 'Cereals',
                                    quantityKg: 500.0,
                                    pricePerKg: 35.0,
                                    description: 'Grown with AI optimized soil parameters (${rec.soilType}, pH ${rec.pH}).',
                                  );
                                  Navigator.pop(ctx);
                                  ScaffoldMessenger.of(context).showSnackBar(
                                    const SnackBar(content: Text('Successfully listed on Marketplace!')),
                                  );
                                },
                                child: const Text('Publish'),
                              ),
                            ],
                          ),
                        );
                      },
                      child: const Text('List This Crop on Marketplace'),
                    ),
                  ],
                ),
              ),
            ),
          ],
        ],
      ),
    );
  }
}

// 3. Marketplace Tab
class MarketplaceTab extends StatelessWidget {
  const MarketplaceTab({super.key});

  @override
  Widget build(BuildContext context) {
    final provider = Provider.of<AppProvider>(context);
    final user = provider.currentUser;
    final theme = Theme.of(context);

    final filteredListings = provider.listings.where((l) {
      if (provider.selectedCategory != 'All' && l.category != provider.selectedCategory) {
        return false;
      }
      return true;
    }).toList();

    return Scaffold(
      appBar: AppBar(
        title: const Text('B2B Agri Marketplace'),
        actions: [
          if (user?.role == UserRole.farmer)
            IconButton(
              icon: const Icon(Icons.add_circle_outline),
              tooltip: 'Add Crop Listing',
              onPressed: () {
                _showAddListingDialog(context, provider);
              },
            ),
        ],
      ),
      body: Column(
        children: [
          SizedBox(
            height: 60,
            child: ListView(
              scrollDirection: Axis.horizontal,
              padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 10),
              children: provider.cropCategories.map((cat) {
                final isSelected = provider.selectedCategory == cat;
                return Padding(
                  padding: const EdgeInsets.only(right: 8.0),
                  child: ChoiceChip(
                    label: Text(cat),
                    selected: isSelected,
                    onSelected: (_) => provider.setSelectedCategory(cat),
                  ),
                );
              }).toList(),
            ),
          ),
          Expanded(
            child: filteredListings.isEmpty
                ? const Center(child: Text('No marketplace listings found.'))
                : ListView.builder(
                    padding: const EdgeInsets.all(12),
                    itemCount: filteredListings.length,
                    itemBuilder: (context, index) {
                      final listing = filteredListings[index];
                      return Card(
                        margin: const EdgeInsets.only(bottom: 12),
                        child: Padding(
                          padding: const EdgeInsets.all(16.0),
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Row(
                                children: [
                                  Expanded(
                                    child: Text(
                                      listing.cropName,
                                      style: theme.textTheme.titleMedium?.copyWith(fontWeight: FontWeight.bold),
                                    ),
                                  ),
                                  Chip(
                                    label: Text('₹${listing.pricePerKg} / kg'),
                                    backgroundColor: theme.colorScheme.secondaryContainer,
                                  ),
                                ],
                              ),
                              const SizedBox(height: 6),
                              Text('Farmer: ${listing.farmerName} • ${listing.location}'),
                              const SizedBox(height: 6),
                              Text('Available Quantity: ${listing.quantityKg} kg'),
                              const SizedBox(height: 6),
                              Text(listing.description, style: TextStyle(color: Colors.grey[700])),
                              const SizedBox(height: 12),
                              if (user?.role == UserRole.vendor)
                                Align(
                                  alignment: Alignment.centerRight,
                                  child: ElevatedButton.icon(
                                    icon: const Icon(Icons.shopping_cart),
                                    label: const Text('Send Purchase Request'),
                                    onPressed: () {
                                      _showPurchaseDialog(context, provider, listing);
                                    },
                                  ),
                                ),
                            ],
                          ),
                        ),
                      );
                    },
                  ),
          ),
        ],
      ),
    );
  }

  void _showAddListingDialog(BuildContext context, AppProvider provider) {
    final cropNameController = TextEditingController();
    String category = 'Cereals';
    final qtyController = TextEditingController();
    final priceController = TextEditingController();
    final descController = TextEditingController();

    showDialog(
      context: context,
      builder: (ctx) => AlertDialog(
        title: const Text('Add Crop Listing'),
        content: SingleChildScrollView(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              TextField(
                controller: cropNameController,
                decoration: const InputDecoration(labelText: 'Crop Name (e.g. Basmati Rice)'),
              ),
              DropdownButtonFormField<String>(
                initialValue: category,
                decoration: const InputDecoration(labelText: 'Category'),
                items: provider.cropCategories.where((c) => c != 'All').map((c) => DropdownMenuItem(value: c, child: Text(c))).toList(),
                onChanged: (val) {
                  if (val != null) category = val;
                },
              ),
              TextField(
                controller: qtyController,
                keyboardType: TextInputType.number,
                decoration: const InputDecoration(labelText: 'Quantity (Kg)'),
              ),
              TextField(
                controller: priceController,
                keyboardType: TextInputType.number,
                decoration: const InputDecoration(labelText: 'Price per Kg (₹)'),
              ),
              TextField(
                controller: descController,
                decoration: const InputDecoration(labelText: 'Description / Quality details'),
                maxLines: 2,
              ),
            ],
          ),
        ),
        actions: [
          TextButton(onPressed: () => Navigator.pop(ctx), child: const Text('Cancel')),
          ElevatedButton(
            onPressed: () {
              final qty = double.tryParse(qtyController.text) ?? 100.0;
              final price = double.tryParse(priceController.text) ?? 30.0;
              if (cropNameController.text.isNotEmpty) {
                provider.addListing(
                  cropName: cropNameController.text,
                  category: category,
                  quantityKg: qty,
                  pricePerKg: price,
                  description: descController.text,
                );
                Navigator.pop(ctx);
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(content: Text('Listing published successfully!')),
                );
              }
            },
            child: const Text('Publish'),
          ),
        ],
      ),
    );
  }

  void _showPurchaseDialog(BuildContext context, AppProvider provider, MarketplaceListing listing) {
    final qtyController = TextEditingController(text: '${listing.quantityKg}');
    final priceController = TextEditingController(text: '${listing.pricePerKg}');

    showDialog(
      context: context,
      builder: (ctx) => AlertDialog(
        title: Text('Purchase Request: ${listing.cropName}'),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Text('Farmer: ${listing.farmerName}'),
            Text('Listed Price: ₹${listing.pricePerKg}/kg'),
            const SizedBox(height: 12),
            TextField(
              controller: qtyController,
              keyboardType: TextInputType.number,
              decoration: const InputDecoration(labelText: 'Requested Quantity (Kg)'),
            ),
            TextField(
              controller: priceController,
              keyboardType: TextInputType.number,
              decoration: const InputDecoration(labelText: 'Offered Price per Kg (₹)'),
            ),
          ],
        ),
        actions: [
          TextButton(onPressed: () => Navigator.pop(ctx), child: const Text('Cancel')),
          ElevatedButton(
            onPressed: () {
              final qty = double.tryParse(qtyController.text) ?? listing.quantityKg;
              final price = double.tryParse(priceController.text) ?? listing.pricePerKg;
              provider.createPurchaseRequest(
                listing: listing,
                quantityKg: qty,
                offeredPricePerKg: price,
              );
              Navigator.pop(ctx);
              ScaffoldMessenger.of(context).showSnackBar(
                const SnackBar(content: Text('Purchase request sent to farmer!')),
              );
            },
            child: const Text('Send Request'),
          ),
        ],
      ),
    );
  }
}

// 4. Purchase Requests Tab
class PurchaseRequestsTab extends StatelessWidget {
  const PurchaseRequestsTab({super.key});

  @override
  Widget build(BuildContext context) {
    final provider = Provider.of<AppProvider>(context);
    final user = provider.currentUser;
    final theme = Theme.of(context);

    return Scaffold(
      appBar: AppBar(title: const Text('B2B Orders & Requests')),
      body: provider.purchaseRequests.isEmpty
          ? const Center(child: Text('No purchase requests found.'))
          : ListView.builder(
              padding: const EdgeInsets.all(12),
              itemCount: provider.purchaseRequests.length,
              itemBuilder: (context, index) {
                final req = provider.purchaseRequests[index];
                return Card(
                  margin: const EdgeInsets.only(bottom: 12),
                  child: Padding(
                    padding: const EdgeInsets.all(16.0),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Row(
                          children: [
                            Expanded(
                              child: Text(req.cropName, style: theme.textTheme.titleMedium?.copyWith(fontWeight: FontWeight.bold)),
                            ),
                            Chip(
                              label: Text(req.status.name.toUpperCase()),
                              backgroundColor: req.status == RequestStatus.accepted
                                  ? Colors.green[100]
                                  : req.status == RequestStatus.rejected
                                      ? Colors.red[100]
                                      : Colors.orange[100],
                            ),
                          ],
                        ),
                        const SizedBox(height: 6),
                        Text('Vendor: ${req.vendorName}'),
                        Text('Quantity: ${req.quantityKg} kg • Offered: ₹${req.offeredPricePerKg}/kg'),
                        if (req.counterOfferPricePerKg != null)
                          Text('Counter Offer: ₹${req.counterOfferPricePerKg}/kg', style: const TextStyle(color: Colors.blue, fontWeight: FontWeight.bold)),
                        const SizedBox(height: 12),
                        if (user?.role == UserRole.farmer && req.status == RequestStatus.pending)
                          Row(
                            mainAxisAlignment: MainAxisAlignment.end,
                            children: [
                              OutlinedButton(
                                onPressed: () => provider.updateRequestStatus(req, RequestStatus.rejected),
                                child: const Text('Reject', style: TextStyle(color: Colors.red)),
                              ),
                              const SizedBox(width: 8),
                              ElevatedButton(
                                onPressed: () => provider.updateRequestStatus(req, RequestStatus.accepted),
                                child: const Text('Accept'),
                              ),
                            ],
                          ),
                      ],
                    ),
                  ),
                );
              },
            ),
    );
  }
}

// 5. Knowledge Hub Tab
class KnowledgeHubTab extends StatelessWidget {
  const KnowledgeHubTab({super.key});

  @override
  Widget build(BuildContext context) {
    final provider = Provider.of<AppProvider>(context);
    final theme = Theme.of(context);

    return Scaffold(
      appBar: AppBar(title: const Text('Agri Knowledge Hub')),
      body: ListView.builder(
        padding: const EdgeInsets.all(12),
        itemCount: provider.articles.length,
        itemBuilder: (context, index) {
          final article = provider.articles[index];
          return Card(
            margin: const EdgeInsets.only(bottom: 12),
            child: Padding(
              padding: const EdgeInsets.all(16.0),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    children: [
                      Chip(label: Text(article.category)),
                      const Spacer(),
                      Text('${article.readTimeMin} min read', style: TextStyle(color: Colors.grey[600], fontSize: 12)),
                    ],
                  ),
                  const SizedBox(height: 8),
                  Text(article.title, style: theme.textTheme.titleMedium?.copyWith(fontWeight: FontWeight.bold)),
                  const SizedBox(height: 6),
                  Text(article.summary, style: TextStyle(color: Colors.grey[700])),
                  const SizedBox(height: 12),
                  Row(
                    children: [
                      Text('By ${article.author}', style: const TextStyle(fontStyle: FontStyle.italic, fontSize: 12)),
                      const Spacer(),
                      TextButton(
                        onPressed: () {
                          showDialog(
                            context: context,
                            builder: (ctx) => AlertDialog(
                              title: Text(article.title),
                              content: SingleChildScrollView(child: Text(article.content)),
                              actions: [
                                TextButton(onPressed: () => Navigator.pop(ctx), child: const Text('Close')),
                              ],
                            ),
                          );
                        },
                        child: const Text('Read Article'),
                      ),
                    ],
                  ),
                ],
              ),
            ),
          );
        },
      ),
    );
  }
}

// 6. Profile Tab
class ProfileTab extends StatelessWidget {
  const ProfileTab({super.key});

  @override
  Widget build(BuildContext context) {
    final provider = Provider.of<AppProvider>(context);
    final user = provider.currentUser;
    final theme = Theme.of(context);

    return Scaffold(
      appBar: AppBar(title: const Text('User Profile')),
      body: Padding(
        padding: const EdgeInsets.all(24.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            const SizedBox(height: 20),
            CircleAvatar(
              radius: 50,
              backgroundColor: theme.colorScheme.primary,
              child: Text(
                user?.name.substring(0, 1).toUpperCase() ?? 'U',
                style: const TextStyle(fontSize: 40, color: Colors.white, fontWeight: FontWeight.bold),
              ),
            ),
            const SizedBox(height: 16),
            Text(user?.name ?? 'User Name', style: theme.textTheme.headlineSmall?.copyWith(fontWeight: FontWeight.bold)),
            Text('${user?.role == UserRole.farmer ? 'Farmer' : 'Vendor'} • ${user?.location ?? 'All India'}', style: theme.textTheme.bodyLarge),
            const SizedBox(height: 24),
            Card(
              child: Padding(
                padding: const EdgeInsets.all(16.0),
                child: Column(
                  children: [
                    ListTile(
                      leading: const Icon(Icons.email),
                      title: const Text('Email'),
                      subtitle: Text(user?.email ?? ''),
                    ),
                    ListTile(
                      leading: const Icon(Icons.phone),
                      title: const Text('Phone'),
                      subtitle: Text(user?.phone ?? ''),
                    ),
                    ListTile(
                      leading: const Icon(Icons.location_on),
                      title: const Text('Region / Location'),
                      subtitle: Text(user?.location ?? ''),
                    ),
                  ],
                ),
              ),
            ),
            const Spacer(),
            ElevatedButton.icon(
              style: ElevatedButton.styleFrom(backgroundColor: Colors.red[700]),
              icon: const Icon(Icons.logout),
              label: const Text('Sign Out'),
              onPressed: () {
                provider.signOut();
              },
            ),
            const SizedBox(height: 20),
          ],
        ),
      ),
    );
  }
}
