package com.bestapp.rice.ui.profileimageselect.permission

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bestapp.rice.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * registerForActivity 때문에 onCreate 이전에 초기화 되어야 합니다.
 */
class ImagePermissionManager(private val fragment: Fragment) {

    private var onGranted: () -> Unit = {}
    private var isImageReselect = false

    private val requestMultiplePermissionLauncher =
        fragment.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { grantsInfo ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                if (grantsInfo[Manifest.permission.READ_MEDIA_IMAGES] == true) {
                    onGranted()
                } else if (grantsInfo[Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED] == true) {
                    if (isImageReselect) {
                        ActivityCompat.requestPermissions(
                            fragment.requireActivity(),
                            readImagePermission,
                            IMAGE_REQUEST_CODE
                        )
                    } else {
                        onGranted()
                    }
                } else {
                    showPermissionExplanation()
                }
            } else if (grantsInfo.values.first()) {
                onGranted()
            } else {
                showPermissionExplanation()
            }
        }

    private val requestSinglePermissionLauncher =
        fragment.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                if (isImageReselect) {
                    ActivityCompat.requestPermissions(
                        fragment.requireActivity(),
                        readImagePermission,
                        IMAGE_REQUEST_CODE
                    )
                } else {
                    onGranted()
                }
            } else {
                showPermissionExplanation()
            }
        }

    private val readImagePermission =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

    fun requestFullImageAccessPermission(
        onGranted: () -> Unit
    ) {
        this.onGranted = onGranted

        if (isFullAccessGranted()) {
            onGranted()
        } else if (isPartialAccessGranted()) {
            // 이미 부분적 권한을 허용한 경우, 전체 권한을 받으려면 사용자가 설정창에 직접 가서 변경하는 방법 밖에 없음
            redirectUserToSetting()
        } else if (isFullAccessGrantedUpToAPI32()) {
            onGranted()
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    fragment.requireActivity(),
                    readImagePermission.first()
                )
            ) {
                showPermissionExplanation(true) {
                    requestMultiplePermissionLauncher.launch(readImagePermission)
                }
            } else {
                requestSinglePermissionLauncher.launch(readImagePermission.first())
            }
        }
    }

    fun requestPartialImageAccessPermission(
        isImageReselect: Boolean = false,
        onGranted: () -> Unit,
    ) {
        this.isImageReselect = isImageReselect
        this.onGranted = onGranted

        if (isPartialAccessGranted()) {
            // 아래 함수는 허용한 이미지를 다시 선택하기 위해 호출됨
            if (isImageReselect) {
                ActivityCompat.requestPermissions(
                    fragment.requireActivity(),
                    readImagePermission,
                    IMAGE_REQUEST_CODE
                )
            } else {
                onGranted()
            }
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    fragment.requireActivity(),
                    readImagePermission.last()
                )
            ) {
                showPermissionExplanation(true) {
                    requestMultiplePermissionLauncher.launch(readImagePermission)
                }
            } else {
                requestMultiplePermissionLauncher.launch(readImagePermission)
            }
        }
    }

    private fun isFullAccessGrantedUpToAPI32(): Boolean = ContextCompat.checkSelfPermission(
        fragment.requireContext(),
        Manifest.permission.READ_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED

    private fun isFullAccessGranted(): Boolean =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(
            fragment.requireContext(),
            Manifest.permission.READ_MEDIA_IMAGES
        ) == PackageManager.PERMISSION_GRANTED

    private fun isPartialAccessGranted(): Boolean =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && ContextCompat.checkSelfPermission(
            fragment.requireContext(),
            Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
        ) == PackageManager.PERMISSION_GRANTED

    private fun showPermissionExplanation(
        isPossibleToShowPermission: Boolean = false,
        callback: () -> Unit = {}
    ) {
        MaterialAlertDialogBuilder(fragment.requireContext())
            .setTitle(
                ContextCompat.getString(
                    fragment.requireContext(),
                    R.string.image_permission_title
                )
            )
            .setMessage(
                ContextCompat.getString(
                    fragment.requireContext(),
                    R.string.image_permission_message
                )
            )
            .setNegativeButton(
                ContextCompat.getString(
                    fragment.requireContext(),
                    R.string.image_permission_negative
                )
            ) { dialog: DialogInterface, which: Int ->
            }
            .setPositiveButton(
                ContextCompat.getString(
                    fragment.requireContext(),
                    R.string.image_permission_positive
                )
            ) { dialog, which ->
                if (isPossibleToShowPermission) {
                    callback()
                    return@setPositiveButton
                }
                redirectUserToSetting()
            }
            .show()
    }

    private fun redirectUserToSetting() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data =
                Uri.parse("package:" + fragment.requireActivity().baseContext.packageName)
        }
        fragment.startActivity(intent)
    }

    fun isFullImageAccessGranted(): Boolean {
        return isFullAccessGranted() || isFullAccessGrantedUpToAPI32()
    }

    companion object {
        const val IMAGE_REQUEST_CODE = 1
    }
}