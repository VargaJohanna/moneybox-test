package com.example.minimoneybox

import com.example.minimoneybox.rx.RxSchedulers
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

class TestScheduler  : RxSchedulers {
    override fun io(): Scheduler {
        return Schedulers.trampoline()
    }

    override fun main(): Scheduler {
        return Schedulers.trampoline()
    }
}