class EducationalArticle {
  final String id;
  final String title;
  final String category;
  final String summary;
  final String content;
  final String author;
  final String imageUrl;
  final int readTimeMin;
  final int timestamp;

  EducationalArticle({
    required this.id,
    required this.title,
    required this.category,
    required this.summary,
    required this.content,
    required this.author,
    this.imageUrl = '',
    this.readTimeMin = 3,
    required this.timestamp,
  });

  Map<String, dynamic> toJson() => {
        'id': id,
        'title': title,
        'category': category,
        'summary': summary,
        'content': content,
        'author': author,
        'imageUrl': imageUrl,
        'readTimeMin': readTimeMin,
        'timestamp': timestamp,
      };

  factory EducationalArticle.fromJson(Map<String, dynamic> json) => EducationalArticle(
        id: json['id'] ?? '',
        title: json['title'] ?? '',
        category: json['category'] ?? 'Soil Guide',
        summary: json['summary'] ?? '',
        content: json['content'] ?? '',
        author: json['author'] ?? 'Agri Expert',
        imageUrl: json['imageUrl'] ?? '',
        readTimeMin: json['readTimeMin'] ?? 3,
        timestamp: json['timestamp'] ?? DateTime.now().millisecondsSinceEpoch,
      );
}
