package com.example.minimoneybox.di

import com.example.minimoneybox.Constants
import com.example.minimoneybox.network.MoneyBoxService
import com.example.minimoneybox.repositories.productRepository.ProductRepository
import com.example.minimoneybox.repositories.productRepository.ProductRepositoryImpl
import com.example.minimoneybox.repositories.userRepository.UserRepository
import com.example.minimoneybox.repositories.userRepository.UserRepositoryImpl
import com.example.minimoneybox.rx.RxSchedulers
import com.example.minimoneybox.rx.SchedulersImpl
import com.example.minimoneybox.ui.login.LoginViewModel
import com.example.minimoneybox.ui.userAccount.UserAccountViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

val repositoryModule = module {
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<ProductRepository> { ProductRepositoryImpl(get(), get()) }
}

val networkModule = module {
    single { get<Retrofit>().create(MoneyBoxService::class.java) }
    single { RxJava2CallAdapterFactory.create() }
    single { GsonConverterFactory.create() }
    single {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()
        client.interceptors()
        Retrofit.Builder()
            .client(client)
            .baseUrl(Constants.MONEYBOX_BASE_URL)
            .addCallAdapterFactory(get<RxJava2CallAdapterFactory>())
            .addConverterFactory(get<GsonConverterFactory>())
            .build()
    }
}

val viewModelModule = module {
    viewModel { LoginViewModel(get(), get()) }
    viewModel { UserAccountViewModel(get(), get(), get()) }
}

val schedulerModule = module {
    factory<RxSchedulers> { SchedulersImpl() }
}


