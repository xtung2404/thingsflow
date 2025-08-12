package com.example.thingsflow.module

import com.example.thingsflow.module.repository.RepoAuthentication
import com.example.thingsflow.module.repository.RepoConfigWileDirect
import com.example.thingsflow.module.repository.RepoGroup
import com.example.thingsflow.module.repository.RepoLocation
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class RepositoryModule {
    @Singleton
    @Provides
    fun provideRepoAuthentication() = RepoAuthentication()
    @Singleton
    @Provides
    fun provideRepoLocation() = RepoLocation()
    @Singleton
    @Provides
    fun provideRepoConfigWileDirect() = RepoConfigWileDirect()

    @Singleton
    @Provides
    fun provideRepoGroup() = RepoGroup()
}