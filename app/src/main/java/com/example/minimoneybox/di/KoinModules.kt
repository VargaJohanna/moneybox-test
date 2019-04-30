package com.example.minimoneybox.di

import com.example.minimoneybox.Constants
import com.example.minimoneybox.network.MoneyBoxService
import com.example.minimoneybox.repositories.UserRepository
import com.example.minimoneybox.repositories.UserRepositoryImpl
import com.example.minimoneybox.rx.RxSchedulers
import com.example.minimoneybox.rx.SchedulersImpl
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

val repositoryModule = module {
    single<UserRepository> { UserRepositoryImpl(get()) }
}

val networkModule = module {
    single { get<Retrofit>().create(MoneyBoxService::class.java) }
    single { RxJava2CallAdapterFactory.create() }
    single { GsonConverterFactory.create() }
    single {
        Retrofit.Builder()
            .baseUrl(Constants.MONEYBOX_BASE_URL)
            .addCallAdapterFactory(get<RxJava2CallAdapterFactory>())
            .addConverterFactory(get<GsonConverterFactory>())
            .build()
    }
}

val viewModelModule = module {
    //    viewModel { DictionaryListViewModel(get(), get(), get()) }
}

val schedulerModule = module {
    factory<RxSchedulers> { SchedulersImpl() }
}


