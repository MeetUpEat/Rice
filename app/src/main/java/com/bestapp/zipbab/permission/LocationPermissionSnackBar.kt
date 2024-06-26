package com.bestapp.zipbab.permission

import androidx.fragment.app.Fragment
import com.bestapp.zipbab.R
import com.google.android.material.snackbar.Snackbar
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.Gravity
import android.widget.FrameLayout

class LocationPermissionSnackBar(
    private val fragment: Fragment
) {
    private val snackBar: Snackbar by lazy {
        Snackbar.make(
            fragment.requireView(),
            fragment.requireContext().getString(R.string.meet_up_map_no_permission),
            Snackbar.LENGTH_INDEFINITE
        )
    }

    fun showPermissionSettingSnackBar() {
        snackBar.setStyleAndAction()

        val isShown = snackBar.isShown

        if (!isShown) {
            snackBar.show()
        }
    }

    fun hidePermissionSettingSnackBar() {
        val isShown = snackBar.isShown

        if (isShown) {
            snackBar.dismiss()
        }
    }

    private fun Snackbar.setStyleAndAction() {
        animationMode = Snackbar.ANIMATION_MODE_FADE

        setAction(
            fragment.requireContext().getString(R.string.meet_up_map_set_permission)
        ) {
            val settingPermissionIntent =
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.parse("package:" + fragment.requireActivity().baseContext.packageName)
                }
            fragment.startActivity(settingPermissionIntent)
        }

        snackBar.setBackgroundTint(getColorFromResources(R.color.snackbar_background))
        setTextColor(getColorFromResources(R.color.snackbar_text))
        setActionTextColor(getColorFromResources(R.color.main_color))
        setAnchorViewTopGravity()
    }

    private fun getColorFromResources(color: Int): Int {
        return fragment.resources.getColor(
            color,
            fragment.requireContext().theme
        )
    }

    private fun Snackbar.setAnchorViewTopGravity() {
        val layoutParams = FrameLayout.LayoutParams(view.layoutParams)
        layoutParams.gravity = Gravity.TOP

        val paddingHorizontal = 20
        val paddingTop = 60
        view.setPadding(paddingHorizontal, paddingTop, paddingHorizontal, 0)
        view.layoutParams = layoutParams
    }
}