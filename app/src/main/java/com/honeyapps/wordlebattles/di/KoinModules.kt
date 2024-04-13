package com.honeyapps.wordlebattles.di

import com.honeyapps.wordlebattles.data.repository.StatsRepository
import com.honeyapps.wordlebattles.data.repository.StatsRepositoryImpl
import com.honeyapps.wordlebattles.sign_in.SignInViewModel
import com.honeyapps.wordlebattles.ui.viewmodels.BottomSheetViewModel
import com.honeyapps.wordlebattles.ui.viewmodels.ChallengeViewModel
import com.honeyapps.wordlebattles.ui.viewmodels.DialogsViewModel
import com.honeyapps.wordlebattles.ui.viewmodels.FriendsViewModel
import com.honeyapps.wordlebattles.ui.viewmodels.MatchViewModel
import com.honeyapps.wordlebattles.ui.viewmodels.RecentMatchViewModel
import com.honeyapps.wordlebattles.ui.viewmodels.StatsViewModel
import com.honeyapps.wordlebattles.ui.viewmodels.UserViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

object KoinModules {
//    private const val EAGER_SCOPE = "eager"
//    private const val LAZY_SCOPE = "lazy"
//    private const val MATCH_SCOPE = "match"

    private val repoModule: Module = module {
        single<StatsRepository> { StatsRepositoryImpl() }
    }

    private val eagerModule = module(createdAtStart = true) {
//        scope(named(EAGER_SCOPE)) {
            viewModel { ChallengeViewModel() }
            viewModel { RecentMatchViewModel() }
            viewModel { SignInViewModel() }
            viewModel { UserViewModel() }
//        }
    }

    private val lazyModule = module {
//        scope(named(LAZY_SCOPE)) {
            viewModel { BottomSheetViewModel() }
            viewModel { DialogsViewModel() }
            viewModel { FriendsViewModel() }
            viewModel { StatsViewModel(statsRepository = get()) }
//        }
    }

    private val matchModule = module {
//        scope(named(MATCH_SCOPE)) {
            viewModel { MatchViewModel() }
//        }
    }

    val modules = listOf(
        repoModule,
        eagerModule,
        lazyModule,
        matchModule
    )

//    fun clearModules(koinApp: Koin) {
//        modules.forEach {
//            koinApp.getScopeOrNull(
//                it.getScopeName().toString() // name
//            )?.close()
//        }
//    }
//
//    fun clearEagerModule(koinApp: Koin) {
//        // eagerModule.getScopeName()
//        koinApp.getScopeOrNull(EAGER_SCOPE)?.close()
//    }
//
//    fun clearLazyModule(koinApp: Koin) {
//        koinApp.getScopeOrNull(LAZY_SCOPE)?.close()
//    }
//
//    fun clearMatchModule(koinApp: Koin) {
//        koinApp.getScopeOrNull(MATCH_SCOPE)?.close()
//    }
}

// old syntax
// singleOf(::StatsRepositoryImpl) { bind<StatsRepository>() }
// viewModelOf(::UserViewModel)