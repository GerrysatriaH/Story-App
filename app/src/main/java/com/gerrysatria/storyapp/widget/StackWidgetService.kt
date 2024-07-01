package com.gerrysatria.storyapp.widget

import android.content.Intent
import android.widget.RemoteViewsService
import com.gerrysatria.storyapp.di.Injection
import com.gerrysatria.storyapp.ui.viewmodel.MainViewModel

class StackWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        val viewModel = MainViewModel(Injection.provideRepository(applicationContext))
        return StackRemoteViewFactory(this.applicationContext, viewModel)
    }
}