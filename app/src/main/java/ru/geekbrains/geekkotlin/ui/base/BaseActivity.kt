package ru.geekbrains.geekkotlin.ui.base

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import ru.geekbrains.geekkotlin.R
import ru.geekbrains.geekkotlin.data.errors.NoAuthException
import timber.log.Timber

abstract class BaseActivity<T, S : BaseViewState<T>> : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE_LOGIN = 1001
    }

    abstract val viewModel: BaseViewModel<T, S>
    abstract val layoutRes: Int?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutRes?.let { setContentView(it) }
        viewModel.getViewState().observe(this, Observer<S> { viewState ->
            viewState?.apply {
                data?.let { renderData(it) }
                error?.let { renderError(it) }
            }
        })
    }

    abstract fun renderData(data: T)

    protected fun renderError(error: Throwable) {
        Timber.e(error)
        when (error) {
            is NoAuthException -> startLoginActivity()
            else -> error.message?.let { showError(it) }
        }
    }

    protected fun showError(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }

    private fun startLoginActivity() {
        val providers = listOf(
                AuthUI.IdpConfig.GoogleBuilder().build()
        )

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setLogo(R.drawable.android_robot)
                        .setTheme(R.style.LoginStyle)
                        .setAvailableProviders(providers)
                        .build(),
                REQUEST_CODE_LOGIN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_LOGIN && resultCode != Activity.RESULT_OK) {
            finish()
        }
    }
}