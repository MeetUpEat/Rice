package com.bestapp.rice.ui.profileimageselect

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bestapp.rice.databinding.FragmentProfileImageSelectBinding
import com.bestapp.rice.model.toArg
import com.bestapp.rice.ui.profileimageselect.permission.ImagePermissionManager
import com.bestapp.rice.ui.profileimageselect.permission.ImagePermissionType
import com.bestapp.rice.util.setVisibility

class ProfileImageSelectFragment : Fragment() {

    private var _binding: FragmentProfileImageSelectBinding? = null
    private val binding: FragmentProfileImageSelectBinding
        get() = _binding!!

    private val imagePermissionManager = ImagePermissionManager(this)
    private val adapter = ProfileImageSelectAdapter {
        findNavController().previousBackStackEntry?.savedStateHandle?.set(
            PROFILE_IMAGE_SELECT_KEY,
            it.toArg()
        )
        if (!findNavController().popBackStack()) {
            requireActivity().finish()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileImageSelectBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFragmentResultListener(PROFILE_IMAGE_PERMISSION_TYPE_KEY) { requestKey, bundle ->
            val imagePermissionType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bundle.getParcelable(
                    ImagePermissionType.IMAGE_PERMISSION_REQUEST_KEY,
                    ImagePermissionType::class.java
                )
            } else {
                bundle.getParcelable(ImagePermissionType.IMAGE_PERMISSION_REQUEST_KEY)
            } ?: return@setFragmentResultListener
            when (imagePermissionType) {
                ImagePermissionType.FULL -> imagePermissionManager.requestFullImageAccessPermission { images: List<GalleryImageInfo> ->
                    adapter.submitList(images)
                }

                ImagePermissionType.PARTIAL -> imagePermissionManager.requestPartialImageAccessPermission(
                    true
                ) { images: List<GalleryImageInfo> ->
                    adapter.submitList(images)
                }
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setPermissionManager()
        setRecyclerView()
        setListener()
    }

    private fun setPermissionManager() {
        imagePermissionManager.setScope(viewLifecycleOwner.lifecycleScope)

        if (imagePermissionManager.isFullImageAccessGranted()) {
            imagePermissionManager.requestFullImageAccessPermission { images: List<GalleryImageInfo> ->
                adapter.submitList(images)
            }
        } else {
            imagePermissionManager.requestPartialImageAccessPermission { images: List<GalleryImageInfo> ->
                adapter.submitList(images)
            }
        }
    }

    private fun setRecyclerView() {
        binding.rvGallery.adapter = adapter
    }

    private fun setListener() {
        binding.vPermissionRequestBackground.setOnClickListener {
            val action =
                ProfileImageSelectFragmentDirections.actionProfileImageSelectFragmentToImagePermissionModalBottomSheet()
            findNavController().navigate(action)
        }
        binding.mt.setNavigationOnClickListener {
            if (!findNavController().popBackStack()) {
                requireActivity().finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        setPermissionUI()
    }

    private fun setPermissionUI() {
        val isFullImageAccessGranted = imagePermissionManager.isFullImageAccessGranted()

        listOf(
            binding.vPermissionRequestBackground,
            binding.tvPermissionDescription,
            binding.tvRequestPermission
        ).map { view ->
            view.setVisibility(isFullImageAccessGranted.not())
        }
        if (isFullImageAccessGranted) {
            imagePermissionManager.requestFullImageAccessPermission { images ->
                adapter.submitList(images)
            }
        }
    }

    override fun onDestroyView() {
        _binding = null

        super.onDestroyView()
    }

    companion object {
        const val PROFILE_IMAGE_PERMISSION_TYPE_KEY = "PROFILE_IMAGE_PERMISSION_TYPE_KEY"
        const val PROFILE_IMAGE_SELECT_KEY = "PROFILE_IMAGE_SELECT_KEY"
    }
}