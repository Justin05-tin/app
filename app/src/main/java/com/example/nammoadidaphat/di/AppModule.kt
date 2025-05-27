package com.example.nammoadidaphat.di

import android.content.Context
import com.example.nammoadidaphat.data.repository.AuthRepositoryImpl
import com.example.nammoadidaphat.data.repository.CategoryRepositoryImpl
import com.example.nammoadidaphat.data.repository.ExerciseRepositoryImpl
import com.example.nammoadidaphat.data.repository.LevelRepositoryImpl
import com.example.nammoadidaphat.data.repository.UserPreferencesRepository
import com.example.nammoadidaphat.data.repository.WorkoutTypeRepositoryImpl
import com.example.nammoadidaphat.domain.repository.AuthRepository
import com.example.nammoadidaphat.domain.repository.CategoryRepository
import com.example.nammoadidaphat.domain.repository.ExerciseRepository
import com.example.nammoadidaphat.domain.repository.LevelRepository
import com.example.nammoadidaphat.domain.repository.WorkoutTypeRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository
    
    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        categoryRepositoryImpl: CategoryRepositoryImpl
    ): CategoryRepository
    
    @Binds
    @Singleton
    abstract fun bindWorkoutTypeRepository(
        workoutTypeRepositoryImpl: WorkoutTypeRepositoryImpl
    ): WorkoutTypeRepository
    
    @Binds
    @Singleton
    abstract fun bindLevelRepository(
        levelRepositoryImpl: LevelRepositoryImpl
    ): LevelRepository
    
    @Binds
    @Singleton
    abstract fun bindExerciseRepository(
        exerciseRepositoryImpl: ExerciseRepositoryImpl
    ): ExerciseRepository

    companion object {
        @Provides
        @Singleton
        fun provideFirebaseAuth(): FirebaseAuth {
            return FirebaseAuth.getInstance()
        }

        @Provides
        @Singleton
        fun provideFirebaseFirestore(): FirebaseFirestore {
            return FirebaseFirestore.getInstance()
        }
        
        @Provides
        @Singleton
        fun provideUserPreferencesRepository(
            @ApplicationContext context: Context
        ): UserPreferencesRepository {
            return UserPreferencesRepository(context)
        }
    }
} 