package com.gerrysatria.storyapp.utils

import com.gerrysatria.storyapp.data.database.StoryEntity

object DataDummy {
    fun generateDummyStoryResponse(): List<StoryEntity> {
        val items: MutableList<StoryEntity> = arrayListOf()
        for (i in 0..100) {
            val story = StoryEntity(
                id = i.toString(),
                photoUrl = "https://www.simplilearn.com/ice9/free_resources_article_thumb/what_is_image_Processing.jpg",
                name = "Name $i",
                description = "Description $i"
            )
            items.add(story)
        }
        return items
    }
}