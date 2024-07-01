package com.gerrysatria.storyapp.widget

import android.content.Context
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.gerrysatria.storyapp.R
import com.gerrysatria.storyapp.ui.viewmodel.MainViewModel
import com.gerrysatria.storyapp.utils.urlToBitmap

internal class StackRemoteViewFactory(
    private val mContext: Context,
    private val viewModel: MainViewModel
) : RemoteViewsService.RemoteViewsFactory {

    private var stories: MutableList<String> = mutableListOf()

    override fun onCreate() {}

    override fun onDataSetChanged() {
        viewModel.getPhotoStory().forEach { photoUrl ->
            stories.add(
                photoUrl
            )
        }
    }

    override fun onDestroy() {}

    override fun getCount(): Int = stories.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(mContext.packageName, R.layout.widget_item)
        rv.setImageViewBitmap(R.id.imageView, urlToBitmap(stories[position]))
        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(i: Int): Long = 0

    override fun hasStableIds(): Boolean = false
}