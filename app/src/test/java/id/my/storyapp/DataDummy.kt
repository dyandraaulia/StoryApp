package id.my.storyapp

import id.my.storyapp.data.remote.response.ListStoryItem

object DataDummy {

    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                id = i.toString(),
                photoUrl = "photo $i",
                createdAt = "created At $i",
                name = "name $i",
                description = "description $i",
                lon = i.toDouble(),
                lat = i.toDouble()
            )
            items.add(story)
        }
        return items
    }
}