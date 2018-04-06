package com.challenge.profileviewer.ui.profile

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.challenge.profileviewer.R
import com.challenge.profileviewer.data.user.model.User
import com.challenge.profileviewer.ui.login.LoginActivity
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_profile.*
import javax.inject.Inject

class ProfileActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        viewModel.profileModelLiveData.observe(this, Observer {
            when (it?.command) {
                ProfileViewModel.Command.STANDBY -> {
                    showUserProfile(it.user)
                    showProgress(false)
                }
                ProfileViewModel.Command.SHOW_LOADING -> showProgress(true)
                ProfileViewModel.Command.ADD_PHOTO -> {
                    openChangeAvatarActivity()
                }
                ProfileViewModel.Command.SHOW_ERROR -> {
                    viewModel.clean()
                }
                ProfileViewModel.Command.RELOGIN -> {
                    openLoginActivity()
                }
            }
        })

        avatar.setOnClickListener { viewModel.changeAvatarClicked() }
    }

    private fun openLoginActivity() {
        startActivity(LoginActivity.getIntent(this))
    }

    private fun showProgress(show: Boolean) {
        val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

        user_profile.visibility = if (show) View.GONE else View.VISIBLE
        user_profile.animate()
                .setDuration(shortAnimTime)
                .alpha((if (show) 0 else 1).toFloat())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        user_profile.visibility = if (show) View.GONE else View.VISIBLE
                    }
                })

        progress.visibility = if (show) View.VISIBLE else View.GONE
        progress.animate()
                .setDuration(shortAnimTime)
                .alpha((if (show) 1 else 0).toFloat())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        progress.visibility = if (show) View.VISIBLE else View.GONE
                    }
                })
    }

    private fun showUserProfile(user: User?) {
        user?.let {
            email.text = user.email
            password.text = user.password
            Picasso.get().load(user.avatarUrl).into(avatar)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.clean()
    }

    fun openChangeAvatarActivity() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                viewModel.changeAvatar(result.uri.path)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                viewModel.showError()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}