package com.example.inklink.models

data class Article(
    var id: String? = null,
    var userId: String? = null,
    var authorName: String? = null,
    var title: String? = null,
    var content: String? = null,
    var status: String? = null,
    var reportCount: Int = 0,
    var creationDate: String? = null
) {
    override fun toString(): String {
        return """
        Article: {
            id: $id,
            userId: $userId,
            title: $title,
            content: $content,
            status: $status,
            reportCount: $reportCount,
            creationDate: $creationDate,
            authorName: $authorName,
        }
        """
    }
}