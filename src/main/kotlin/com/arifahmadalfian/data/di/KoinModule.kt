package com.arifahmadalfian.data.di

import com.arifahmadalfian.data.repository.HeroRepository
import com.arifahmadalfian.data.repository.IHeroRepository
import org.koin.dsl.module

val koinModule = module {
    single<IHeroRepository> {
        HeroRepository()
    }
}