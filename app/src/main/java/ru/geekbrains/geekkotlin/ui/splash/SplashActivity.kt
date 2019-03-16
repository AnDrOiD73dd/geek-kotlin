package ru.geekbrains.geekkotlin.ui.splash

import android.arch.lifecycle.ViewModelProviders
import android.os.Handler
import ru.geekbrains.geekkotlin.ui.base.BaseActivity
import ru.geekbrains.geekkotlin.ui.main.MainActivity

class SplashActivity : BaseActivity<Boolean?, SplashViewState>() {
    companion object {
        private const val START_DELAY = 1000L
    }

    override val viewModel: SplashViewModel by lazy {
        ViewModelProviders.of(this).get(SplashViewModel::class.java)
    }

    override val layoutRes: Int? = null

    override fun onResume() {
        super.onResume()
        Handler().postDelayed({ viewModel.requestUser() }, START_DELAY)
    }

    override fun renderData(data: Boolean?) {
        data?.takeIf { it }?.let {
            startMainActivity()
        }
    }

    private fun startMainActivity() {
        MainActivity.start(this)
        finish()
    }

}