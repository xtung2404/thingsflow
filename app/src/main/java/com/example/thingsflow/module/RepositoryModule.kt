package com.example.thingsflow.module

import com.example.thingsflow.module.repository.AuthenticationRepository
import com.example.thingsflow.module.repository.ConfigWileDirectRepository
import com.example.thingsflow.module.repository.GroupRepository
import com.example.thingsflow.module.repository.LocationRepository
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
    fun provideAuthenticationRepository() = AuthenticationRepository()
    @Singleton
    @Provides
    fun provideLocationRepository() = LocationRepository()
    @Singleton
    @Provides
    fun provideConfigWileDirectRepository() = ConfigWileDirectRepository()

    @Singleton
    @Provides
    fun provideGroupRepository() = GroupRepository()
}